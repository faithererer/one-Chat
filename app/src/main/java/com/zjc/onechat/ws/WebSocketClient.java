package com.zjc.onechat.ws;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.adapter.UserAdapter;
import com.zjc.onechat.api.UserInfo;
import com.zjc.onechat.config.RequestServer;
import com.zjc.onechat.dao.MessageDao;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.Message;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient {
    // 获取单例的 Gson 对象（已处理容错）
    private Gson gson = GsonFactory.getSingletonGson();

    private OkHttpClient client;
    private WebSocket webSocket;
    private UserDao userDao;
    private MessageDao messageDao;
    private MessagesListAdapter<MessageAdapter> adapter; // 适配器引用，用于更新UI
    private Activity activity; // Activity引用，用于UI更新
    private User my;
    private String token;
    private String chatId;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Handler handler;
    private Integer PING_INTERVAL = 30; //s;
    private boolean isConn = false;


    public WebSocketClient(Activity activity, MessagesListAdapter<MessageAdapter> adapter, User my,String token, MessageDao messageDao, String chatId) {
        this.activity = activity;
        this.adapter = adapter;
        this.my=my;
        this.token=token;
        this.messageDao=messageDao;
        this.chatId=chatId;
        // 确保 ChatApp 中有一个公共静态方法来获取 Application 实例
        this.userDao = ((ChatApp) activity.getApplication()).getUserDao();
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        client = new OkHttpClient();
        Log.d( "ws: ","开始连接");

        Request request = new Request.Builder().url(RequestServer.getWebsocketChatUrl()).addHeader("authorization",token).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                super.onOpen(webSocket, response);
                isConn=true;
                Log.d(TAG, "ws连接成功...");
                Toaster.show("ws连接成功了");
                // 连接成功
                schedulePing(); // 连接建立时开始定时发送心跳包

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if(text.equals("pong")){
                    return;
                }

                Log.d("收到消息aa", text);
                Message message = parseMessage(text);
                message.setChat_id(Long.parseLong(chatId));
                message.setIs_read(true);
                // 保存消息到数据库
                executorService.execute(()->{
                    message.setId(null);
                    messageDao.insertMessage(message);
                });
                // 查询用户
                User user = userDao.getUserById(message.getSender_id());
                MessageAdapter messageAdapter1;
                Log.d(TAG, "收到用户： "+user);
                if(user==null){
                    // 从服务器获取用户信息
                    EasyHttp.get((LifecycleOwner) activity)
                            .api(new UserInfo().setUserId(message.getSender_id()))
                            .request(new OnHttpListener<Result<User>>() {
                                @Override
                                public void onHttpSuccess(Result<User> userResult) {
                                    User user = userResult.getData();
                                    executorService.execute(()->{
                                        userDao.insertUser(user);
                                        Log.d(TAG, "onHttpSuccess: "+user);
                                        MessageAdapter messageAdapter1=new MessageAdapter(message,new UserAdapter(user));
                                        activity.runOnUiThread(() -> {
                                            adapter.addToStart(messageAdapter1, true);
                                        });
                                    });
                                }

                                @Override
                                public void onHttpFail(Throwable throwable) {
                                    Toaster.show("服务异常");

                                }
                            });
                }else {
                    messageAdapter1 = new MessageAdapter(message, new UserAdapter(user));
                    Log.d(TAG, "收到: " + user);
                    activity.runOnUiThread(() -> {
                        adapter.addToStart(messageAdapter1, true);
                    });
                }
            }

            private Message parseMessage(String text) {
                // 解析json为Message
                Log.d(TAG, "解析前: ");

                Message message = gson.fromJson(text, Message.class);
                Log.d(TAG, "解析后: "+message);
                return message;
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
//                Toaster.show("ws连接关闭");
//                Log.d(TAG, "ws连接关闭"+reason);
                cancelPing(); // 连接关闭时取消定时发送心跳包
                isConn=false;
                executorService.shutdown();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
                Toaster.show("ws连接失败"+response);
                isConn=false;
                // 尝试重新连接
                reconnectWebSocket();
             }
        });

        client.dispatcher().executorService().shutdown(); // 关闭OkHttp客户端
    }
    private void schedulePing() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webSocket != null) {
                    webSocket.send("ping"); // 发送心跳包
                    schedulePing(); // 继续定时发送下一个心跳包
                }
            }
        }, PING_INTERVAL * 1000);
    }
    private void cancelPing() {
        handler.removeCallbacksAndMessages(null); // 取消所有心跳包的定时发送任务
    }
    public void sendMessage(String message) {
        Log.d("发送消息at websocket: ",message+"===="+webSocket.toString());
        if (webSocket != null&& isConn) {

            webSocket.send(message);
            executorService.execute(() -> {
                Message message1 = gson.fromJson(message, Message.class);
                messageDao.insertMessage(message1);
                activity.runOnUiThread(() -> adapter.addToStart(new MessageAdapter(message1,new UserAdapter(my)), true));
            });
        }
        else{
            Toaster.show("网络异常，检查网络或者服务器");
        }
    }

    public void closeConnection() {
        cancelPing(); // 连接关闭时取消定时发送心跳包
        isConn=false;
        if (webSocket != null) {
            webSocket.close(1000, "Closing Connection");
        } else {
            cancelPing(); // 连接关闭时取消定时发送心跳包
        }
        executorService.shutdown();

    }
    private void reconnectWebSocket() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start(); // 重新启动WebSocket连接
            }
        }, 3000); // 重试延迟时间，这里设定为3秒，可以根据实际情况调整
    }
}
