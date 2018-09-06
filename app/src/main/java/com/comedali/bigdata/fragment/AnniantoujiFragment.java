package com.comedali.bigdata.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comedali.bigdata.R;
import com.comedali.bigdata.utils.MyMarkView;
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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by 刘杨刚 on 2018/9/6.
 */
public class AnniantoujiFragment extends Fragment {
    private LineChart mChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.annian_touji_er,container,false);
        mChart=view.findViewById(R.id.nian_lineChart);
        initview();
        return view;
    }
    private void initview() {
        int time=12;
        initdata3(time);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.animateX(2400); //垂直轴动画 从下到上
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setNoDataText("数据获取失败");
        mChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//颜色数值
        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        //自定义markView,点击显示更多信息
        MyMarkView markerView = new MyMarkView(getActivity(),R.layout.custom_marker_view);
        markerView.setChartView(mChart);
        mChart.setMarker(markerView);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                return m+"月";
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setInverted(true);//Y轴倒序
        xAxis.setAvoidFirstLastClipping(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        //设置限制线 12代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(2000);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        //limitLine.setLabel("游客多");
        //设置限制线为虚线
        //limitLine.enableDashedLine(10f, 10f, 0f);
        //左边Y轴添加限制线
        leftAxis.addLimitLine(limitLine);

    }

    private void initdata3(int count) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 1; i < count+1; i++) {
            //float xVal = (float) (Math.random() * 20);
            int yVal = (int) (Math.random() * 2500);

            entries.add(new Entry(i, yVal));
        }
        // sort by x-value
        Collections.sort(entries, new EntryXComparator());
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(entries, "大理古城客流量月统计 单位：人");
        set1.setLineWidth(1.5f);
        set1.setCircleRadius(4f);
        set1.setValueTextSize(12);
        set1.setDrawCircleHole(false);//设置是否在数据点中间显示一个孔
        set1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int mm=(int)entry.getY();
                return String.valueOf(mm);
            }
        });
        // create a data object with the datasets
        LineData data2 = new LineData(set1);
        // set data
        mChart.setData(data2);
    }
}
