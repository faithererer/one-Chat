package com.zjc.onechat.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.adapter.UserAdapter;
import com.zjc.onechat.aliyunOss.OSSUploader;
import com.zjc.onechat.api.UserInfo;
import com.zjc.onechat.callback.UserInfoCallback;
import com.zjc.onechat.dao.ChatDao;
import com.zjc.onechat.dao.MessageDao;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.Chat;
import com.zjc.onechat.dao.entity.Message;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;
import com.zjc.onechat.holder.CustomOutcomingImageViewHolder;
import com.zjc.onechat.holder.CustomOutcomingMessageViewHolder;
import com.zjc.onechat.ws.WebSocketClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class ChatActivity extends AppCompatActivity {
    private Bundle bundle;
    private MessageDao messageDao;
    private static final int IMAGE_PICKER_REQUEST = 123; // 任意正整数作为请求码

    private UserDao userDao;
    private ChatDao chatDao;
    private User me;
    private MessagesListAdapter<MessageAdapter> adapter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String currentUserId;
    private WebSocketClient webSocketClient;
    // 获取单例的 Gson 对象（已处理容错）
    Gson gson = GsonFactory.getSingletonGson();
    private Chat chat;
    private OSSUploader ossUploader;

    private CountDownLatch latch = new CountDownLatch(1);
    private static final int PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE = 100;

    private ImageLoader imageLoader = new ImageLoader() {
        @Override
        public void loadImage(ImageView imageView, @Nullable String s, @Nullable Object o) {

            if (s != null) {
//                Log.d(TAG, "加载图片 "+s);
//                // 检查 url 是否是本地路径
//                if (s.startsWith("content://")) {
//
//                    Uri uri = Uri.parse(s);
//                    Toaster.show(uri);
//
//
//                    // 本地路径，使用 File 加载
//                    Glide.with(ChatActivity.this)
//                            .load(uri)
//                            .into(imageView);
//                } else {
                    // 远程 URL
                    Glide.with(ChatActivity.this)
                            .load(s)
                            .into(imageView);
//                }
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle=savedInstanceState;
        setContentView(R.layout.activity_chat_main);
        findViewById(R.id.viewBlur).post(()->{
            applyBlur(findViewById(R.id.viewBlur));
        });
        chatDao = ((ChatApp) getApplication()).getChatDao();
        messageDao = ((ChatApp) getApplication()).getMessageDao();
        userDao = ((ChatApp) getApplication()).getUserDao();
        MessagesList messagesList = findViewById(R.id.messagesList);
        MessageInput messageInput = findViewById(R.id.messageInput_);
        ossUploader = new OSSUploader(getApplication());
        currentUserId = getCurrentUserId(); // 获取当前用户ID的方法
        MessageHolders holders = new MessageHolders()
                .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
                .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message,
                        null
                )
                .setIncomingImageLayout(R.layout.item_custom_incoming_image_message)
                .setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message);



        adapter = new MessagesListAdapter<>(currentUserId, holders ,imageLoader);
        messagesList.setAdapter(adapter);
        loadMessages();
        adapter.setDateHeadersFormatter(date -> {
            if (date == null) {
                return "Date unavailable";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(date);
        });

        TextView chatTitle = findViewById(R.id.chat_tvTitle);

        String friendId = getIntent().getStringExtra("friend_id");
        Log.d("活到:"+friendId,"s");
        chatTitle.setText(getIntent().getStringExtra("friend_name"));


        messageInput.setInputListener(input -> {
            String text = input.toString();
            Log.d(TAG, "onCreate: 发送消息");
            if (!text.isEmpty()) {
                sendMessage(text);
                return true;
            }
            return false;
        });
        ImageView ivBack = findViewById(R.id.chat_ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webSocketClient!=null) {
                    webSocketClient.closeConnection();
                }
                finish();
            }
        });
        messageInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                // 附件按钮点击事件
                openImagePicker(); // 假设 openImagePicker() 是打开图片选择器的方法

            }
        });

        executorService.execute(() -> {

            chat = chatDao.getChat(Long.parseLong(currentUserId),Long.parseLong(friendId));
            Log.d("当前CHAT",String.valueOf(chat.getId()));
            // 判断chat是否存在,不存在插入
            if(chat==null){
                Chat newChat = new Chat();
                newChat.setUser_id(Long.parseLong(currentUserId));
                newChat.setFriend_id(Long.parseLong(friendId));
                Log.d("发现:","s"+newChat.toString());
                chatDao.insertChat(newChat);
                this.chat = chatDao.getChat(Long.parseLong(currentUserId),Long.parseLong(friendId));
            }
            Log.d(TAG, "如何 "+chat.toString());
            SharedPreferences sharedPreferences = getSharedPreferences("authorization", MODE_PRIVATE);
            String token = sharedPreferences.getString("authorization", "");
            User user = userDao.getUserById(Long.parseLong(currentUserId));
            me=user;
            webSocketClient = new WebSocketClient(this, adapter, user, token,messageDao,String.valueOf(chat.getId()));
            webSocketClient.start();

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(webSocketClient!=null) {
            webSocketClient.closeConnection();
            Toaster.show("关闭ws");
        }

    }



    private void openImagePicker() {
        // 这里是打开图片选择器的具体实现，可以根据具体情况选择合适的方式
        // 例如，使用系统自带的图片选择器
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST); // IMAGE_PICKER_REQUEST 是一个标识请求码
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                // 获取选中的图片 URI
                Uri selectedImageUri = data.getData();
                Message message = new Message();
                message.setMessage_type(2);
                message.setChat_id(chat.getId());
                message.setContent(selectedImageUri.toString());
                message.setTo_user_id(chat.getFriend_id());
                message.setSender_id(chat.user_id);
                message.setSent_time(System.currentTimeMillis());

                ossUploader.uploadImage(selectedImageUri, new OSSUploader.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toaster.show("上传图片成功："+imageUrl);
                        message.setContent(imageUrl);
                        webSocketClient.sendMessage(gson.toJson(message));
//                        // 显示图片通过选中的Uri显示，而不是获得的url节省流量。
//                        Log.d("selectedImageUri",selectedImageUri.toString());
//                        message.setContent(selectedImageUri.toString());
                        // 保存到数据库
                        executorService.execute(()->{
                            messageDao.insertMessage(message);
                        });
//                        runOnUiThread(()-> {
//                            adapter.addToStart(new MessageAdapter(message, new UserAdapter(me)), true);
//                        });

                    }

                    @Override
                    public void onUploadFailure(Exception e, ServiceException serviceException) {
                        Log.d(TAG, "onUploadFailure: "+e.getMessage());
                        Toaster.show(e);
                    }
                });



            }
        }
    }



    private void loadMessages() {
        String friendId = getIntent().getStringExtra("friend_id");
        executorService.execute(() -> {
            User user = userDao.getUserById(Long.parseLong(currentUserId));
            if (user == null) {
                getFriendAnMeInfo(Long.parseLong(currentUserId), new UserInfoCallback() {
                    @Override
                    public void onSuccess(Result<UserInfo.Bean> result) {
                        User user = new User();
                        user.setAvatar(result.getData().getAvatar());
                        user.setNick_name(result.getData().getNickName());
                        user.setId(result.getData().getId());
                        user.setPhone(result.getData().getPhone());
                        executorService.execute(() -> {
                            userDao.insertUser(user);
                        });
                        me=user;
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toaster.show(errorMessage));
                    }
                });
            }
            me=user;
            User friend = userDao.getUserById(Long.parseLong(friendId));
            if (friend == null) {
                getFriendAnMeInfo(Long.parseLong(friendId), new UserInfoCallback() {
                    @Override
                    public void onSuccess(Result<UserInfo.Bean> result) {
                        User user = new User();
                        user.setAvatar(result.getData().getAvatar());
                        user.setNick_name(result.getData().getNickName());
                        user.setPhone(result.getData().getPhone());
                        user.setId(result.getData().getId());
                        executorService.execute(() -> {
                            userDao.insertUser(user);
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toaster.show(errorMessage));
                    }
                });
            }
            // 从本地查询
            chat = chatDao.getChat(Long.parseLong(currentUserId), Long.parseLong(friendId));
            // 存在吗？
            Log.d(TAG, "c: 获取中"+chat);

            if(chat==null){
                chat = new Chat();
                chat.setFriend_id(Long.parseLong(friendId));
                chat.setUser_id(Long.valueOf(currentUserId));
                chatDao.insertChat(chat);
                chat = chatDao.getChat(Long.parseLong(currentUserId), Long.parseLong(friendId));
                Log.d(TAG, "getChats: 获取成功"+chat);
            }
            Log.d("","当前chat:"+chat);
            // 继续执行后续代码
            List<Message> messages = messageDao.getMessagesForChat(String.valueOf(chat.getId()));
            Log.d(TAG, "loadMessages: 列表"+messages.toString());
            List<MessageAdapter> messageAdapters = new ArrayList<>();
            for (Message message : messages) {
                Log.d(TAG, "loadMessages: 列表进行时"+messages.toString());
                User cur_user = userDao.getUserById(message.getSender_id());
                message.setIs_read(true);
                // 已读
                messageDao.update(message);
                Log.d(TAG, "消息对应用户"+message.getSender_id()+":"+cur_user);
                IUser userAdapter = new UserAdapter(cur_user);
                Log.d("头像", user.getAvatar() + "a");
                messageAdapters.add(new MessageAdapter(message, userAdapter));
            }

            runOnUiThread(() -> {
                for (MessageAdapter messageAdapter : messageAdapters) {
                    adapter.addToStart(messageAdapter, true);
                }
            });
        });
    }



    private void sendMessage(String text) {
        String friendId = getIntent().getStringExtra("friend_id");
        String userId = getCurrentUserId();
        Log.d(TAG, "常量 ");
        Log.d(TAG, "常量 "+friendId+" "+userId);
        Message message = new Message();
        message.setContent(text);
        message.setSender_id(Long.parseLong(userId));
        message.setTo_user_id(Long.parseLong(friendId));
        message.setSent_time(System.currentTimeMillis());
        message.setChat_id(chat.getId());

        executorService.execute(() -> {
            User user = userDao.getUserById(Long.parseLong(userId));
            if (user == null) {
                getFriendAnMeInfo(Long.parseLong(friendId), new UserInfoCallback() {

                    @Override
                    public void onSuccess(Result<UserInfo.Bean> result) {
                        // 获取到用户信息后执行发送消息的逻辑
                        webSocketClient.sendMessage(gson.toJson(message));

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toaster.show(errorMessage));
                    }
                });
            } else {
                // 获取到用户信息后执行发送消息的逻辑
                webSocketClient.sendMessage(gson.toJson(message));

            }
        });
    }







    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("authorization", MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }
    // 从远端获取chats
    private void getChats(String userId, String friendId) {
        Log.d(TAG, "getChats: 获取中");

        executorService.execute(()->{
            // 从本地查询
            chat = chatDao.getChat(Long.parseLong(userId), Long.parseLong(friendId));
            // 存在吗？
             Log.d(TAG, "getChats: 获取中"+chat);

             if(chat==null){
                chat = new Chat();
                chat.setFriend_id(Long.parseLong(friendId));
                chat.setUser_id(Long.valueOf(userId));
                chatDao.insertChat(chat);
                chat = chatDao.getChat(Long.parseLong(userId), Long.parseLong(friendId));
                Log.d(TAG, "getChats: 获取成功"+chat);
            }

        });
        }

    private void getFriendAnMeInfo(Long userId, UserInfoCallback callback) {
        EasyHttp.get(this)
                .api(new UserInfo().setUserId(userId))
                .request(new OnHttpListener<Result<UserInfo.Bean>>() {
                    @Override
                    public void onHttpSuccess(Result<UserInfo.Bean> result) {
                        UserInfo.Bean bean = result.getData();
                        Log.d(TAG, "获取用户id "+userId+bean.toString());
                        // 构造 User 对象或从 bean 中获取用户信息
                        User user = new User();
                        user.setId(userId);
                        user.setNick_name(bean.getNickName());  // 举例：假设用户有名称字段
                        // 其他设置用户信息的逻辑

                        callback.onSuccess(result);
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        callback.onFailure("获取用户信息失败");
                        Log.d("ChatActivity", "getFriendAnMeInfo() 调用失败: " + userId);
                    }
                });
    }

    // 应用模糊效果
    private void applyBlur(View viewBlur) {
        float radius = 10f; // 模糊半径，可以根据需要调整

        Bitmap bitmap = getBitmapFromView(viewBlur); // 获取视图的位图

        if (bitmap != null) {
            Bitmap blurredBitmap = blurBitmap(bitmap, radius); // 对位图进行模糊处理

            if (blurredBitmap != null) {
                viewBlur.setBackground(new BitmapDrawable(getResources(), blurredBitmap)); // 设置模糊后的背景
            }
        }
    }

    // 获取视图的位图
    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // 对位图进行模糊处理
    private Bitmap blurBitmap(Bitmap bitmap, float radius) {
        RenderScript renderScript = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        // 创建一个模糊效果的 RenderScript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blurScript.setInput(input);
        blurScript.setRadius(radius);
        blurScript.forEach(output);

        output.copyTo(bitmap); // 将处理后的位图复制回原始位图

        renderScript.destroy();
        return bitmap;
    }


}
