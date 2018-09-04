package com.comedali.bigdata.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.comedali.bigdata.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

/**
 * Created by 刘杨刚 on 2018/9/4.
 */
public class Youke_zhanbiActivity extends AppCompatActivity{
    private CommonTitleBar commonTitleBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youke_zhanbi_er);
        //顶部导航栏按钮事件
        commonTitleBar=findViewById(R.id.youke_zhanbi_back);
        commonTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action==commonTitleBar.ACTION_LEFT_BUTTON){
                    Youke_zhanbiActivity.this.finish();
                }
            }
        });
    }
}
