package com.comedali.bigdata;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;


import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.comedali.bigdata.activity.LoginActivity;
import com.comedali.bigdata.fragment.LvyouFragment;
import com.comedali.bigdata.fragment.ShouyeFragment;
import com.comedali.bigdata.fragment.XiaofeiFragment;
import com.comedali.bigdata.fragment.XingweiFragment;
import com.comedali.bigdata.fragment.YoukeFragment;
import com.comedali.bigdata.utils.NetworkUtil;
import com.comedali.bigdata.utils.ViewPagerSlide;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ViewPagerSlide viewPager;
    private BottomBarLayout mBottomBarLayout;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private Handler mHandler = new Handler();
    private long exitTime = 0;// 用来计算返回键的点击间隔时间
    private static MainActivity instance;
    private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QMUIStatusBarHelper.translucent(this);// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(this);//状态栏字体颜色--黑色
        initView();
        initData();
        initListener();
        yanzheng();
        instance = this;//存储引用
    }
    public static MainActivity getInstance(){
        return instance;
    }

    private void yanzheng() {
        SharedPreferences pref = getSharedPreferences("token", Context.MODE_PRIVATE);
        String account = pref.getString("account", null);
        String password = pref.getString("password", null);
        String url="http://192.168.190.119:8080/login/login?username="+account+"&passwd="+password;
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        if (NetworkUtil.checkNet(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"服务器异常,请重试",Toast.LENGTH_LONG).show();
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

                                            }else {
                                                new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                                                        .setTitle("提示")
                                                        .setMessage("账户信息已过期，请重新登录验证")
                                                        .setCanceledOnTouchOutside(false)
                                                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                                                            @Override
                                                            public void onClick(QMUIDialog dialog, int index) {
                                                                dialog.dismiss();
                                                                SharedPreferences pref = getSharedPreferences("token",Context.MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = pref.edit();
                                                                editor.clear();//清除保存的TOKEN
                                                                editor.commit();
                                                                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
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
        }else {
            Toast.makeText(MainActivity.this,"请检查您的网络是否开启",Toast.LENGTH_LONG).show();
        }



    }

    private void initView() {
        viewPager = findViewById(R.id.vp_content);
        mBottomBarLayout = findViewById(R.id.bbl);

    }

    private void initData() {

        mFragmentList.add(new ShouyeFragment());
        mFragmentList.add(new YoukeFragment());
        mFragmentList.add(new XingweiFragment());
        //mFragmentList.add(new XiaofeiFragment());
        //mFragmentList.add(new LvyouFragment());

    }

    private void initListener() {
        //设置viewPager适配器
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setSlide(false);//左右滑动是否开启
        //mBottomBarLayout.setSmoothScroll(true);//左右滑动开启
        mBottomBarLayout.setViewPager(viewPager);
       /* mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final BottomBarItem bottomBarItem, int previousPosition, final int currentPosition) {
                Log.i("MainActivity","position: " + currentPosition);
                if (currentPosition == 0){
                    //如果是第一个，即首页
                    if (previousPosition == currentPosition){
                        //如果是在原来位置上点击,更换首页图标并播放旋转动画
                        bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_loading);//更换成加载图标
                        bottomBarItem.setStatus(true);

                        //播放旋转动画
                        if (mRotateAnimation == null) {
                            mRotateAnimation = new RotateAnimation(0, 360,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                    0.5f);
                            mRotateAnimation.setDuration(800);
                            mRotateAnimation.setRepeatCount(-1);
                        }
                        ImageView bottomImageView = bottomBarItem.getImageView();
                        bottomImageView.setAnimation(mRotateAnimation);
                        bottomImageView.startAnimation(mRotateAnimation);//播放旋转动画

                        //模拟数据刷新完毕
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean tabNotChanged = mBottomBarLayout.getCurrentItem() == currentPosition; //是否还停留在当前页签
                                bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换成首页原来选中图标
                                bottomBarItem.setStatus(tabNotChanged);//刷新图标
                                cancelTabLoading(bottomBarItem);
                            }
                        },3000);
                        return;
                    }
                }

                //如果点击了其他条目
                BottomBarItem bottomItem = mBottomBarLayout.getBottomItem(0);
                bottomItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换为原来的图标

                cancelTabLoading(bottomItem);//停止旋转动画
            }
        });*/

        //mBottomBarLayout.setUnread(0,20);//设置第一个页签的未读数为20
        //mBottomBarLayout.setUnread(1,l4);//设置第二个页签的未读数
        //mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
        //mBottomBarLayout.setMsg(3,"NEW");//设置第四个页签显示NEW提示文字
        //mBottomBarLayout.setMsg(l4,"NEW");
    }

    /**停止首页页签的旋转动画*/
    private void cancelTabLoading(BottomBarItem bottomItem) {
        Animation animation = bottomItem.getImageView().getAnimation();
        if (animation != null){
            animation.cancel();
        }
    }


    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) { //返回需要展示的fragment
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {   //返回需要展示的fangment数量
            return mFragmentList.size();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                         && event.getAction() == KeyEvent.ACTION_DOWN) {
                         if ((System.currentTimeMillis() - exitTime) > 2000) {
                                 //弹出提示，可以有多种方式
                                 Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
                                 exitTime = System.currentTimeMillis();
                             } else {
                                 finish();
                             }
                         return true;
                     }
        return super.onKeyDown(keyCode, event);
    }

}
