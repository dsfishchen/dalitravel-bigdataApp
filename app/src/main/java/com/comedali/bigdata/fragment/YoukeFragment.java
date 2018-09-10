package com.comedali.bigdata.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.R;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘杨刚 on 2018/9/3.
 */
public class YoukeFragment extends Fragment {
    private PieChart mPicChart;
    private RecyclerView youke_recyclerView;
    private TabLayout mTabLayout;
    private YoukelaiyuanAdapter adapter;
    private List<YoukelaiyuanEntity> youkedatas;
    private LinearLayout youke_linearlayout;
    private LinearLayout youke2_linearLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.youke_yi,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());//状态栏字体颜色--黑色
        mTabLayout=view.findViewById(R.id.youke_SlidingTabLayout);
        youke_recyclerView=view.findViewById(R.id.youke_recyclerView);
        mPicChart = view.findViewById(R.id.age_chart);
        youke_linearlayout=view.findViewById(R.id.youke_linearlayout);
        youke_linearlayout.setVisibility(View.GONE);//隐藏数据
        youke2_linearLayout=view.findViewById(R.id.youke2_linearLayout);
        youke2_linearLayout.setVisibility(View.GONE);
        mTabLayout.addTab(mTabLayout.newTab().setText("年龄结构"));
        mTabLayout.addTab(mTabLayout.newTab().setText("性别比例"));
        mTabLayout.addTab(mTabLayout.newTab().setText("游客来源"));
        initviewmPicChart();

        initData();
        //Log.d("youkedatas", String.valueOf(youkedatas));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        youke_recyclerView.setLayoutManager(layoutManager);
        adapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
        adapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
        adapter.isFirstOnly(false);//重复执行可设置
        //给RecyclerView设置适配器
        youke_recyclerView.setAdapter(adapter);
        youke_recyclerView.setVisibility(View.GONE);//隐藏控件
        //添加Android自带的分割线
        youke_recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(getActivity(),"你点击了"+position,Toast.LENGTH_SHORT).show();
            }
        });
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //选中了tab的逻辑
                String name=tab.getText().toString();
                if (name=="年龄结构"){
                    mPicChart.setVisibility(View.VISIBLE);
                    youke_recyclerView.setVisibility(View.GONE);
                    youke_linearlayout.setVisibility(View.GONE);
                    youke2_linearLayout.setVisibility(View.GONE);
                    Log.d("mm", name);
                }
                if (name=="性别比例"){
                    mPicChart.setVisibility(View.GONE);
                    youke_recyclerView.setVisibility(View.GONE);
                    youke_linearlayout.setVisibility(View.GONE);
                    youke2_linearLayout.setVisibility(View.VISIBLE);
                    Log.d("mm", name);
                }
                if (name=="游客来源"){
                    mPicChart.setVisibility(View.GONE);
                    youke_recyclerView.setVisibility(View.VISIBLE);
                    youke_linearlayout.setVisibility(View.VISIBLE);
                    youke2_linearLayout.setVisibility(View.GONE);
                    Log.d("mm", name);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            //未选中tab的逻辑
                //initviewmPicChart();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            //再次选中tab的逻辑
            }
        });
        return view;
    }
    private void initData() {
        youkedatas=new ArrayList<>();
        YoukelaiyuanEntity model;
        for (int i = 0; i < 100; i++) {
            model=new YoukelaiyuanEntity();
            model.setId(i+1+"");
            model.setProvince("云南"+i);
            model.setBaifenbi("17.01%");
            youkedatas.add(model);
        }

    }
    /*
        饼图
         */
    private void initviewmPicChart() {
        setDatamPicChart();
        Description description = new Description();
        description.setText("");
        mPicChart.setDescription(description);//右下角字,添加图表描述
        //mPicChart.setCenterText("客流量占比");//中心字
        //mPicChart.setCenterTextSize(16);//中心字大小
        mPicChart.setHoleRadius(0f);//设置圆孔半径
        mPicChart.setTransparentCircleRadius(0f);//设置半透明圈的宽度
        mPicChart.setNoDataText("没有数据");//设置饼图没有数据时显示的文本
        mPicChart.setUsePercentValues(true); //Boolean类型  设置图表是否使用百分比
        //picChart.setTransparentCircleColor(R.color.qingse);//设置环形图与中间空心圆之间的环形的颜色
        mPicChart.setHighlightPerTapEnabled(true);//设置点击Item高亮是否可用
        //mPicChart.setExtraOffsets(5, 0, 5, 5);
        mPicChart.animateY(1400);//设置Y轴动画
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = mPicChart.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setFormSize(10);//设置图例的大小
        l.setDrawInside(false);
        //结束
        mPicChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.d("ww", e.toString());
            }

            @Override
            public void onNothingSelected() {
                //Toast.makeText(Youke_zhanbiActivity.this, "请点击里边", Toast.LENGTH_SHORT).show();
            }
        });   //监听器
    }
    private void setDatamPicChart() {
        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(30f,"19岁及以下"));
        strings.add(new PieEntry(170f,"20-29岁"));
        strings.add(new PieEntry(60f,"30-39岁"));
        strings.add(new PieEntry(50f,"40-49岁"));
        strings.add(new PieEntry(30f,"50岁以上"));
        PieDataSet dataSet = new PieDataSet(strings,"年龄占比");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.app_color_theme_8));
        colors.add(getResources().getColor(R.color.app_color_theme_7));
        colors.add(getResources().getColor(R.color.app_color_theme_9));
        colors.add(getResources().getColor(R.color.app_color_theme_5));
        colors.add(getResources().getColor(R.color.app_color_theme_4));
        colors.add(getResources().getColor(R.color.app_color_theme_3));

        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());//转化百分比
        pieData.setValueTextSize(12f);

        //dataSet.setValueLinePart1OffsetPercentage(80.f);
        //dataSet.setValueLinePart1Length(0.1f);//设置连接线的长度
       // dataSet.setValueLinePart2Length(0.6f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        mPicChart.highlightValues(null);
        mPicChart.setData(pieData);//设置数据
        mPicChart.invalidate();//重绘图表
    }
}
