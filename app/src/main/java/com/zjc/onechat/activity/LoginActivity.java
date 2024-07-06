package com.zjc.onechat.activity;






import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hjq.http.EasyConfig;
import com.hjq.http.exception.HttpException;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.toast.Toaster;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.R;
import com.zjc.onechat.api.QueryAvatarApi;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;
import com.zjc.onechat.api.SendCodeApi;
import com.zjc.onechat.api.LoginApi;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.zjc.onechat.entity.UserDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.blurry.Blurry;

public class LoginActivity extends BaseActivity {
    private EditText phoneInput;
    private EditText codeInput;
    private Button sendCodeButton;
    private Button loginButton;
    private ImageView backgroundImage;
    private ImageView blurredBackground;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("LoginActivity", "onCreate: 活动创建");
        userDao = ((ChatApp) getApplication()).getUserDao();


        setContentView(R.layout.login);



        phoneInput = findViewById(R.id.phoneInput);
        codeInput = findViewById(R.id.codeInput);
        sendCodeButton = findViewById(R.id.send_code_button);
        loginButton = findViewById(R.id.login_button);
        backgroundImage = findViewById(R.id.background_image);
        blurredBackground = findViewById(R.id.blurred_background);

        // 延迟应用毛玻璃效果，确保视图已经加载完成
        backgroundImage.post(() -> {
            Blurry.with(this)
                    .radius(10)    // 设置模糊半径
                    .sampling(8)   // 设置采样率，数值越高模糊越厉害但性能越好
                    .async()       // 异步处理模糊效果
                    .capture(backgroundImage) // 捕获背景图像
                    .into(blurredBackground); // 应用到模糊背景视图

            blurredBackground.setVisibility(View.VISIBLE); // 显示模糊背景
        });
        sendCodeButton.setOnClickListener(v -> sendCode());
        loginButton.setOnClickListener(v -> login());

        // 判断token是否有效
        SharedPreferences sharedPreferences = getSharedPreferences("authorization",MODE_PRIVATE);
        sharedPreferences.getString("authorization","");
        if (!sharedPreferences.getString("authorization","").isEmpty()){
            // 判断用户信息是否有效，有效直接跳转MainActivity
            SharedPreferences sharedPreferences_me = getSharedPreferences("authorization",MODE_PRIVATE);
            String json = sharedPreferences_me.getString("id", null);
            if (json != null ) {
                Log.d(TAG, "有效");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // 结束 LoginActivity
            }

        }
        // 假设在你的 onCreate() 方法中
        phoneInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String phone = phoneInput.getText().toString();
                    if (phone.isEmpty()) {
                        return;
                    }
                    // 发起获取头像的请求
                    EasyHttp.post(LoginActivity.this)
                            .api(new QueryAvatarApi().setPhone(phone))
                            .request(new HttpCallbackProxy<Result<String>>(LoginActivity.this) {
                                @Override
                                public void onHttpSuccess(Result<String> result) {
                                    if (result != null && result.getSuccess()) {
                                        // 在 UI 线程显示头像
                                        runOnUiThread(() -> {
                                            Glide.with(LoginActivity.this)
                                                    .load(result.getData())
                                                    .circleCrop()
                                                    .error(R.drawable.ic_profile)
                                                    .into((ImageView) findViewById(R.id.login_avatar));
                                        });
                                    }
                                }

                                @Override
                                public void onHttpFail(Throwable throwable) {
                                    super.onHttpFail(throwable);
                                    Toaster.show("网络异常");
                                }


                            });
                }
            }
        });
    }

        private void sendCode() {
        String phone = phoneInput.getText().toString();
        if (phone.isEmpty()) {
            Toaster.show("请输入手机号");
//            Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        EasyHttp.post(this)
                .api(new SendCodeApi().setPhone(phone))
                .request(new HttpCallbackProxy<Result<String>>(this) {
                    @Override
                    public void onHttpSuccess(Result<String> data) {
                        Log.d(TAG, "onHttpSuccess: "+data);
                        if (data.getSuccess()) {
                            Toaster.show("验证码已发送");

//                            Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                        } else {
                            Toaster.show("发送验证码失败: ");
//                            Toast.makeText(LoginActivity.this, "发送验证码失败: " + data.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toaster.show("发送验证码失败: ");

//                        Toast.makeText(LoginActivity.this, "发送验证码失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void login() {
        String phone = phoneInput.getText().toString();
        String code = codeInput.getText().toString();
        if (phone.isEmpty() || code.isEmpty()) {
            Toaster.show("请输入手机号和验证码");
            return;
        }

        EasyHttp.post(this)
                .api(new LoginApi().setPhone(phone).setCode(code))
                .request(new OnHttpListener<Result<UserDTO>>() {
                    @Override
                    public void onHttpSuccess(Result<UserDTO> result) {
                        if (result.getSuccess()) {
                            Toaster.show("登录成功");
                            // 保存登录信息
                            UserDTO data = result.getData();
                            if(data==null){
                                Toaster.show("返回参数为null");
                                return;
                            }
                            String token = data.getToken();
                            // 获取SharedPreferences对象

                            SharedPreferences sharedPreferences = getSharedPreferences("authorization",MODE_PRIVATE);

                            // 获取Editor对象
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // 存储token
                            editor.putString("authorization", token);
                            // 存储用户
                            Gson gson = new Gson();
                            String json = gson.toJson(result.getData());
                            editor.putString("id",result.getData().getId().toString());
                            editor.putString("nickname",result.getData().getNickname());
                            editor.putString("avatar",result.getData().getAvatar());
                            editor.putString("me",json);

                            // db
                            executorService.execute(()->{
                                User user = new User();
                                user.setNick_name(data.getNickname());
                                user.setAvatar(data.getAvatar());
                                user.setId(data.getId());
                                user.setPhone(data.getPhone());
                                if(userDao.userExists(data.getId())){
                                    userDao.updateUser(user);
                                } else {
                                    userDao.insertUser(user);
                                }
                            });



                            // 提交更改
                            editor.apply();
                            EasyConfig.getInstance().addHeader("authorization",token);
                          // Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toaster.show("登录失败: " + result.getErrorMsg());
//                            Toast.makeText(LoginActivity.this, "登录失败: " + result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable e) {
                        Toaster.show("登录失败" );

//                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LoginActivity", "onStart: 活动开始");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LoginActivity", "onResume: 活动恢复");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LoginActivity", "onPause: 活动暂停");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LoginActivity", "onStop: 活动停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LoginActivity", "onDestroy: 活动销毁");
    }



}
