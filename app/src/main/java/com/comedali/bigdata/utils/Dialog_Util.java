package com.comedali.bigdata.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Collections;
import java.util.List;

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

    private LineChart mChart;
    private Context mContext;

    public Dialog_Util(Context context) {
        super(context, R.style.QMUI_Dialog);
        mContext=context;
    }
    /**
     * 都是内容数据
     */
    private Spanned message;
    private String title;
    private String positive,negtive;
    private int imageResId = -1 ;
    private List<Entry> entries;
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
        if (null == entries || entries.size() ==0){
            mChart.setNoDataText("当前点没有数据");
            mChart.setNoDataTextColor(Color.rgb(192,192,192));
        }else {
            Collections.sort(entries, new EntryXComparator());
            LineDataSet set1 = new LineDataSet(entries, "瞬时人流");
            set1.setLineWidth(1.5f);
            set1.setCircleRadius(4f);
            set1.setValueTextSize(12);
            set1.setDrawCircleHole(false);//设置是否在数据点中间显示一个孔
            set1.setDrawFilled(true);
            //set1.setFillColor(Color.argb(255,10,30,40));
            //Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.wudi);
            //set1.setFillDrawable(drawable);
            set1.setValueTextColor(Color.rgb(192,192,192));
            //set1.setDrawValues(false);//不显示点数值
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    int mm=(int)entry.getY();
                    return String.valueOf(mm)+"人";
                }
            });
            if (entries.size()>10){
                set1.setDrawValues(false);
            }
            // create a data object with the datasets
            LineData data = new LineData(set1);
            // set data
            mChart.setData(data);
            mChart.invalidate();


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
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 设置窗口背景透明度
        attributes.width = screenWidth-100;
        //attributes.height=screenHeight/2;
        getWindow().setAttributes(attributes);

        guanbi = findViewById(R.id.guanbi);
        dialog_biaoti = findViewById(R.id.dialog_biaoti);
        xinxi = findViewById(R.id.xinxi);
        mChart = findViewById(R.id.dialog_lineChart);

        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.animateY(2000); //垂直轴动画 从下到上
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setNoDataText("正在获取数据...");
        mChart.setNoDataTextColor(Color.WHITE);
        mChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//颜色数值

        //自定义markView,点击显示更多信息
        RiMarkView markerView = new RiMarkView(mContext,R.layout.custom_marker_view);
        markerView.setChartView(mChart);
        mChart.setMarker(markerView);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(2f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setTextColor(Color.rgb(192,192,192));
        xAxis.setLabelRotationAngle(-20);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;

                return m+"时";
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setInverted(true);//Y轴倒序
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.rgb(192,192,192));
        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setTextColor(Color.rgb(192,192,192));

        //设置限制线 12代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(15000);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("超过1.5万人");
        //设置限制线为虚线
        //limitLine.enableDashedLine(10f, 10f, 0f);
        //左边Y轴添加限制线
        leftAxis.addLimitLine(limitLine);
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

    public Spanned getMessage() {
        return message;
    }

    public Dialog_Util setMessage(Spanned message) {
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

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
