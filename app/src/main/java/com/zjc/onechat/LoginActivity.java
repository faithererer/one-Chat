package com.zjc.onechat;






import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hjq.http.EasyConfig;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.toast.Toaster;
import com.zjc.onechat.entity.HttpData;
import com.zjc.onechat.entity.Result;
import com.zjc.onechat.api.SendCodeApi;
import com.zjc.onechat.api.LoginApi;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.zjc.onechat.entity.UserDTO;

import org.json.JSONObject;

import jp.wasabeef.blurry.Blurry;

public class LoginActivity extends BaseActivity {
    private EditText phoneInput;
    private EditText codeInput;
    private Button sendCodeButton;
    private Button loginButton;
    private ImageView backgroundImage;
    private ImageView blurredBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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
            Gson gson = new Gson();
            String json = sharedPreferences_me.getString("me", null);
            if (json != null ) {
                UserDTO user_me = gson.fromJson(json, UserDTO.class);
                Log.d(TAG, "我是 " + user_me.toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
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
                            String token = result.getData().getToken();
                            // 获取SharedPreferences对象

                            SharedPreferences sharedPreferences = getSharedPreferences("authorization",MODE_PRIVATE);

                            // 获取Editor对象
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // 存储token
                            editor.putString("authorization", token);
                            // 存储用户
                            Gson gson = new Gson();
                            String json = gson.toJson(result.getData());
                            editor.putString("me",json);
                            // 提交更改
                            editor.apply();
                            EasyConfig.getInstance().addHeader("authorization",token);
                          // Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
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


}
