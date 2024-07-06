package com.zjc.onechat.fragment;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.header.FalsifyHeader;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.zjc.onechat.activity.ChatActivity;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.adapter.UserAdapter;
import com.zjc.onechat.api.UnReadApi;
import com.zjc.onechat.callback.UnreadCountCallback;
import com.zjc.onechat.dao.ChatDao;
import com.zjc.onechat.dao.MessageDao;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.Chat;
import com.zjc.onechat.dao.entity.Dialog;
import com.zjc.onechat.dao.entity.Message;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesFragment extends Fragment {

    private DialogsList dialogsList;
    private List<Dialog> curDialogList;
    private DialogsListAdapter<Dialog> dialogsAdapter;
    private UserDao userDao;
    private boolean isFirstCreation = false;

    private MessageDao messageDao;
    private ChatDao chatDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SharedPreferences sharedPreferences;
    private View view;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        executorService.shutdown();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 逆天创建");
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RefreshLayout refreshLayout = (RefreshLayout)getActivity().findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new SetupDialogsTask(MessagesFragment.this, refreshlayout).execute();

            }
        });
    }


    private boolean setupDialogsList() {
        final boolean[] isSuccess = {false}; // Set error flag
        Toaster.show("加载聊天列表");
        dialogsAdapter = new DialogsListAdapter<>(com.stfalcon.chatkit.R.layout.item_dialog, DialogViewHolder.class, getImageLoader());
        getActivity().runOnUiThread(()->{
            dialogsList.setAdapter(dialogsAdapter);
        });
        // Load chat list from local database
        List<Chat> chatList = chatDao.getChatsForUser(Long.parseLong(getCurrentUserId()));
        List<Dialog> dialogList = new ArrayList<>();
        for (Chat chat : chatList) {
            final int[] flag = {0};
//            int unreadMessageCount = messageDao.getUnreadMessageCount(String.valueOf(chat.getId()),String.valueOf(chat.getFriend_id()));
//            Toaster.show("固有未读："+unreadMessageCount);
//            Log.d(TAG, "固有未读"+unreadMessageCount);
            User user = userDao.getUserById(chat.getUser_id());
            User friend = userDao.getUserById(chat.getFriend_id());
            List<Message> messageList = messageDao.getMessagesForChat(String.valueOf(chat.getId()));
            // Sort messages by sent_time to get the latest message
            messageList.sort(Comparator.comparingLong(Message::getSent_time).reversed());
            Message latestMessage = messageList.isEmpty() ? null : messageList.get(0);
            // Create Dialog object
            Dialog dialog = new Dialog();
//            dialog.setUnreadCount(unreadMessageCount);
            dialog.setId(String.valueOf(chat.getId()));
            dialog.setDialogName(friend.getNick_name());
            dialog.setLastMessage(latestMessage != null ? new MessageAdapter(latestMessage, new UserAdapter(user)) : null);
            dialog.setDialogPhoto(friend.getAvatar());
            dialog.setFriendId(String.valueOf(chat.friend_id));
            dialog.setUserId(String.valueOf(chat.getUser_id()));
            // Add dialog to the adapter
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogsAdapter.addItem(dialog);
                }
            });
            // Fetch unread count asynchronously
            dialogList.add(dialog);
        }
        dialogsAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {
                // 获取被点击的对话的 ID
                String dialogId = dialog.getId();
                Log.d("","点击");
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("friend_id", dialog.getFriendId());
                intent.putExtra("friend_name", dialog.getDialogName());
                //设置启动标志，确保ChatActivity独立于当前任务栈，并在返回时不重复创建
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });
        Log.d(TAG, "setupDialogsList: 初具规模"+dialogList.size());
        curDialogList = dialogList;
        return updateUnreadCounts(dialogList);
    }

    private boolean updateUnreadCounts(List<Dialog> dialogList) {
        final boolean[] isSuccess = {true}; // Use an array to hold boolean value (effectively final)
        CountDownLatch latch = new CountDownLatch(dialogList.size()); // CountDownLatch to wait for all callbacks

        for (Dialog dialog : dialogList) {
            getUnreadCount(Long.parseLong(dialog.getUserId()), Long.parseLong(dialog.getFriendId()), new UnreadCountCallback() {
                @Override
                public void onUnreadCountFetched(UnReadApi.Bean unreadInfo) {
                    Toaster.show("未读消息数：" + unreadInfo.getCount());
                    int pos = dialogsAdapter.getDialogPosition(dialog);
                    dialog.setUnreadCount(unreadInfo.getCount());
                    if(unreadInfo.getCount()!=0) {
                        executorService.execute(() -> {
                            User friend = userDao.getUserById(unreadInfo.getMessage().getSender_id());
                            dialog.setLastMessage(new MessageAdapter(unreadInfo.getMessage(), new UserAdapter(friend)));
                        });
                    }

                    // Update dialog in the adapter
                    dialogsAdapter.updateItem(pos, dialog);
                    latch.countDown(); // Decrement latch count
                }

                @Override
                public void onError(Throwable throwable) {
                    isSuccess[0] = false; // Set isSuccess to false on error
                    latch.countDown(); // Decrement latch count even on error
                }
            });
        }

        try {
            latch.await(); // Wait until all callbacks complete
        } catch (InterruptedException e) {
            e.printStackTrace();
            isSuccess[0] = false; // Handle interrupted exception if needed
        }

        return isSuccess[0]; // Return the final isSuccess value
    }

    // ImageLoader接口的实现，用于加载头像图片
    private ImageLoader getImageLoader() {
        return new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                // 使用Glide或其他图片加载库加载图片
                // 圆形
                RequestOptions requestOptions = new RequestOptions().circleCrop();
                Glide.with(MessagesFragment.this)
                        .load(url)
                        .apply(requestOptions)
                        .into(imageView);
            }
        };
    }

    // 自定义ViewHolder类，用于显示Dialog列表项
    public static class DialogViewHolder extends DialogsListAdapter.DialogViewHolder<Dialog> {
        public DialogViewHolder(View itemView) {
            super(itemView);
            // 在这里绑定和设置对话的UI显示逻辑
        }

        @Override
        public void onBind(Dialog dialog) {
            super.onBind(dialog);
            // 在这里处理对话的UI绑定逻辑，例如设置头像、未读消息数等

        }
    }
    private String getCurrentUserId() {
        return sharedPreferences.getString("id", "");
    }

    private void getUnreadCount(long userId, long friendId, UnreadCountCallback callback) {
        EasyHttp.get(this)
                .api(new UnReadApi()
                        .setFriendId((int) friendId)
                        .setUserId((int) userId))
                .request(new OnHttpListener<Result<UnReadApi.Bean>>() {
                    @Override
                    public void onHttpSuccess(Result<UnReadApi.Bean> beanResult) {
                        UnReadApi.Bean data = beanResult.getData();
                        callback.onUnreadCountFetched(beanResult.getData());
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }



    @Override
    public void onResume() {
        super.onResume();


        executorService.execute(()->{


            userDao = ((ChatApp) getActivity().getApplication()).getUserDao();
            messageDao = ((ChatApp) getActivity().getApplication()).getMessageDao();
            chatDao = ((ChatApp) getActivity().getApplication()).getChatDao();
            sharedPreferences = getActivity().getSharedPreferences("authorization", MODE_PRIVATE);
            dialogsList = view.findViewById(R.id.dialogsList);
            setupDialogsList();


        });
    }

    private static class SetupDialogsTask extends AsyncTask<Void, Void, Boolean> {
        private final MessagesFragment outerInstance;
        private final RefreshLayout refreshlayout;

        public SetupDialogsTask(MessagesFragment outerInstance, RefreshLayout refreshlayout) {
            this.outerInstance = outerInstance;
            this.refreshlayout = refreshlayout;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // 在后台执行耗时操作
            if (outerInstance != null) {
                return outerInstance.updateUnreadCounts(outerInstance.curDialogList);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // 在主线程中更新UI，结束刷新动作
            if (outerInstance != null && outerInstance.getActivity() != null) {
                outerInstance.getActivity().runOnUiThread(() -> {
                    refreshlayout.finishRefresh(result);
                });
            }
        }
    }


}
