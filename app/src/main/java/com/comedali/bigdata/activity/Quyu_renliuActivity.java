package com.comedali.bigdata.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.comedali.bigdata.R;
import com.comedali.bigdata.fragment.AnniantoujiFragment;
import com.comedali.bigdata.fragment.AnritoujiFragment;
import com.comedali.bigdata.fragment.AnyuetoujiFragment;
import com.comedali.bigdata.fragment.ShishijiankongFragment;
import com.comedali.bigdata.utils.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;

/**
 * Created by 刘杨刚 on 2018/9/6.
 */
public class Quyu_renliuActivity extends AppCompatActivity implements OnTabSelectListener {
    private CommonTitleBar commonTitleBar;
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "实时监控", "按日统计", "按月统计","按年统计"
    };
    private MyPagerAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quyu_renliu_er);
        //顶部导航栏按钮事件
        commonTitleBar=findViewById(R.id.quyurenliu_back);
        commonTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action==commonTitleBar.ACTION_LEFT_BUTTON){
                    Quyu_renliuActivity.this.finish();
                }
            }
        });
        mFragments.add(new ShishijiankongFragment());
        mFragments.add(new AnritoujiFragment());
        mFragments.add(new AnyuetoujiFragment());
        mFragments.add(new AnniantoujiFragment());
        /*for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }*/
        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.techan_viewpager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        /** indicator圆角色块 */
        SlidingTabLayout tabLayout_10 = ViewFindUtils.find(decorView, R.id.techan_SlidingTabLayout);
        tabLayout_10.setViewPager(vp);
    }
    @Override
    public void onTabSelect(int position) {
        Toast.makeText(mContext, "onTabSelect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabReselect(int position) {
        Toast.makeText(mContext, "onTabReselect&position--->" + position, Toast.LENGTH_SHORT).show();
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
