package com.comedali.bigdata;



import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.comedali.bigdata.fragment.DingdanFragment;
import com.comedali.bigdata.fragment.YoukeFragment;
import com.comedali.bigdata.fragment.HuodongFragment;
import com.comedali.bigdata.fragment.ShouyeFragment;
import com.comedali.bigdata.fragment.WodeFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private BottomBarLayout mBottomBarLayout;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QMUIStatusBarHelper.translucent(this);// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(this);//状态栏字体颜色--黑色
        initView();
        initData();
        initListener();
    }
    private void initView() {
        viewPager = findViewById(R.id.vp_content);
        mBottomBarLayout = findViewById(R.id.bbl);

    }

    private void initData() {

        mFragmentList.add(new ShouyeFragment());
        mFragmentList.add(new YoukeFragment());
        mFragmentList.add(new HuodongFragment());
        mFragmentList.add(new DingdanFragment());
        mFragmentList.add(new WodeFragment());

    }

    private void initListener() {
        //设置viewPager适配器
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mBottomBarLayout.setSmoothScroll(true);//左右滑动开启
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
        //mBottomBarLayout.setUnread(1,4);//设置第二个页签的未读数
        //mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
        //mBottomBarLayout.setMsg(3,"NEW");//设置第四个页签显示NEW提示文字
        //mBottomBarLayout.setMsg(4,"NEW");
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
}
