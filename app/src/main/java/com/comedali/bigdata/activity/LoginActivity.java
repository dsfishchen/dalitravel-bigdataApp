package com.comedali.bigdata.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.utils.NetworkUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 刘杨刚 on 2018/9/22.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private EditText _emailText;
    private EditText _addressText;
    private Button _loginButton;
    private EditText _passwordText;
    private OkHttpClient client = new OkHttpClient();
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QMUIStatusBarHelper.translucent(this);// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(this);//状态栏字体颜色--黑色
        _emailText=findViewById(R.id.input_email);
        _passwordText=findViewById(R.id.input_password);
        _loginButton=findViewById(R.id.btn_login);
        _addressText=findViewById(R.id.input_email);
        if (NetworkUtil.checkNet(this)){

        }else {
            Toast.makeText(LoginActivity.this,"请检查您的网络是否开启",Toast.LENGTH_LONG).show();
        }
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        /*handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what==123)
                {
                    //跳转到登录成功的界面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    //      Log.d("ww", "onItemClick: "+name);
                    //intent.putExtra("token", token);//传递token
                    // Log.d(TAG, token);
                    startActivity(intent);
                    finish();
                }
                else if (msg.what == 234)
                {
                    Toast.makeText(LoginActivity.this, "您登录失败，账号密码不正确",Toast.LENGTH_SHORT).show();
                }

            }
        };
        SharedPreferences pref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String is_token = pref.getString("token", null);
        Log.i(TAG, "onCreate: "+is_token);
        //判断token是否存在
        if (is_token==null){

        }else {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }*/
        SharedPreferences pref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String account = pref.getString("account", null);
        String password = pref.getString("password", null);
        if (account==null||password==null){

        }else {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(true);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String address = _addressText.getText().toString();
        // TODO: Implement your own authentication logic here.

        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("account",address)
                .add("password",password)
                .build();
        String url="http://192.168.190.119:8080/login/login?username="+address+"&passwd="+password;
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"服务器异常,请重试",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            Log.d("数据请求", "成功"+str);
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                final JSONArray result1=new JSONArray(result);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result1.length()>0){
                                            SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("account",address);
                                            editor.putString("password",password);
                                            editor.commit();
                                            //跳转到登录成功的界面
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            progressDialog.dismiss();
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });



                                /*SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("account",address);
                                editor.putString("password",password);
                                editor.commit();*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            response.body().close();
                        }
                    }
                });
            }
        }).start();


        //新建一个线程，用于得到服务器响应的参数
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        //mHandler.obtainMessage(1, response.body().string()).sendToTarget();
                        String str = response.body().string();
                        Log.d("数据请求", "成功"+str);
                        JSONObject jsonData = new JSONObject(str);
                        String resultStr = jsonData.getString("success");
                        if (resultStr.equals("true")){//成功
                            SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("account",address);
                            editor.putString("password",password);
                            editor.commit();
                            Message msg = handler.obtainMessage();
                            msg.what = 123;
                            handler.sendMessage(msg);
                            progressDialog.dismiss();
                            *//*new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    onLoginSuccess();

                                }
                            }).start();*//*
                        }else if (resultStr.equals("false")){
                            Message msg = handler.obtainMessage();
                            msg.what = 234;
                            handler.sendMessage(msg);
                            *//*new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();

                                }
                            }).start();*//*
                        }

                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        //_loginButton.setEnabled(true);
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();
        //_loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String address = _addressText.getText().toString();
        if (address.isEmpty()) {
            _addressText.setError("请输入有效账号");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            _passwordText.setError("密码6-16位");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
