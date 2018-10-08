package com.comedali.bigdata.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.charts.LineChart;

/**
 * Created by 刘杨刚 on 2018/10/8.
 */
public class Dialog_Util extends Dialog {

    private ImageView guanbi ;

    /**
     * 显示的标题
     */
    private TextView dialog_biaoti ;

    /**
     * 显示的消息
     */
    private TextView xinxi ;

    private LineChart dialog_lineChart;

    public Dialog_Util(Context context) {
        super(context, R.style.QMUI_Dialog);
    }
    /**
     * 都是内容数据
     */
    private String message;
    private String title;
    private String positive,negtive ;
    private int imageResId = -1 ;
    /**
     * 底部是否只有一个按钮
     */
    private boolean isSingle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }
    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        /*positiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( onClickBottomListener!= null) {
                    onClickBottomListener.onPositiveClick();
                }
            }
        });*/
        //设置取消按钮被点击后，向外界提供监听
        guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( onClickBottomListener!= null) {
                    onClickBottomListener.onNegtiveClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty(title)) {
            dialog_biaoti.setText(title);
            dialog_biaoti.setVisibility(View.VISIBLE);
        }else {
            dialog_biaoti.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message)) {
            xinxi.setText(message);
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty(negtive)) {
            guanbi.setImageResource(R.mipmap.guanbi);
        }else {
            guanbi.setImageResource(R.mipmap.guanbi);
        }

    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 设置窗口背景透明度
        attributes.width = screenWidth-100;
        getWindow().setAttributes(attributes);

        guanbi = findViewById(R.id.guanbi);
        dialog_biaoti = findViewById(R.id.dialog_biaoti);
        xinxi = findViewById(R.id.xinxi);
        dialog_lineChart = findViewById(R.id.dialog_lineChart);
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;
    public Dialog_Util setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }
    public interface OnClickBottomListener{
        /**
         * 点击取消按钮事件
         */
        public void onNegtiveClick();
    }

    public String getMessage() {
        return message;
    }

    public Dialog_Util setMessage(String message) {
        this.message = message;
        return this ;
    }

    public String getTitle() {
        return title;
    }

    public Dialog_Util setTitle(String title) {
        this.title = title;
        return this ;
    }

    public String getPositive() {
        return positive;
    }

    public Dialog_Util setPositive(String positive) {
        this.positive = positive;
        return this ;
    }

    public String getNegtive() {
        return negtive;
    }

    public Dialog_Util setNegtive(String negtive) {
        this.negtive = negtive;
        return this ;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public Dialog_Util setSingle(boolean single) {
        isSingle = single;
        return this ;
    }

    public Dialog_Util setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this ;
    }
}
