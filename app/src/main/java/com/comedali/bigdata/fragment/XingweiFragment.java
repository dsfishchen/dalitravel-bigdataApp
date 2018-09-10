package com.comedali.bigdata.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.comedali.bigdata.utils.MyMarkView;
import com.comedali.bigdata.utils.RadarMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 刘杨刚 on 2018/9/3.
 */
public class XingweiFragment extends Fragment {
    private TabLayout mTabLayout;
    private PieChart mPicChart;
    private ConstraintLayout xingwei_ConstraintLayout;
    private Button choose_button;
    private QMUIListPopup mListPopup;
    private String one;
    private String two;
    private TextureMapView mMapView;
    private TencentMap tencentMap;
    private BarChart mBarChart;
    private LinearLayout pianhao_linearLayout;
    private RadarChart mChart;//雷达图
    private RecyclerView jiudian_recycleView;
    private YoukelaiyuanAdapter adapter;
    private List<YoukelaiyuanEntity> youkedatas;
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private Polyline polyline;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.xingwei_er,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(getActivity());//状态栏字体颜色--黑色
        jiudian_recycleView=view.findViewById(R.id.jiudian_recycleView);
        initData();
        //Log.d("youkedatas", String.valueOf(youkedatas));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        jiudian_recycleView.setLayoutManager(layoutManager);
        adapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
        adapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
        adapter.isFirstOnly(false);//重复执行可设置
        //给RecyclerView设置适配器
        jiudian_recycleView.setAdapter(adapter);
        //添加Android自带的分割线
        jiudian_recycleView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(getActivity(),"你点击了"+position,Toast.LENGTH_SHORT).show();
            }
        });



        //雷达图
        mChart = view.findViewById(R.id.xingque_RadarChart);
        initviewmRadarChart();

        pianhao_linearLayout=view.findViewById(R.id.pianhao_linearLayout);
        pianhao_linearLayout.setVisibility(View.GONE);
        mBarChart = view.findViewById(R.id.tingliuTime_chart);
        mBarChart.setVisibility(View.GONE);
        initviewmBarChart();
        mMapView = view.findViewById(R.id.xingwei_map);
        tencentMap = mMapView.getMap();
        //设定中心点坐标
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(25.794945,100.184326), //新的中心点坐标
                        10.5f,  //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f)); //偏航角 0~360° (正北方为0)
        //tencentMap.animateCamera(cameraSigma);//改变地图状态
        tencentMap.moveCamera(cameraSigma);//移动地图

        //latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(25.646788,100.322685));
        latLngs.add(new LatLng(25.583866,100.230932));
        latLngs.add(new LatLng(25.609217,100.221362));
        latLngs.add(new LatLng(25.637812,100.206041));
        latLngs.add(new LatLng(25.704727,100.168877));
        latLngs.add(new LatLng(25.758465,100.139694));
        latLngs.add(new LatLng(25.845784,100.133343));
        polyline=tencentMap.addPolyline(new PolylineOptions().
                addAll(latLngs).color(0xff00ff00). width(5f));




        choose_button=view.findViewById(R.id.choose_button);
        choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });
        xingwei_ConstraintLayout=view.findViewById(R.id.xingwei_ConstraintLayout);
        mPicChart = view.findViewById(R.id.chuxing_chart);
        mPicChart.setVisibility(View.GONE);
        initviewmPicChart();
        mTabLayout=view.findViewById(R.id.xingwei_SlidingTabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("游客轨迹"));
        mTabLayout.addTab(mTabLayout.newTab().setText("出行方式"));
        mTabLayout.addTab(mTabLayout.newTab().setText("停留时间"));
        mTabLayout.addTab(mTabLayout.newTab().setText("偏好分析"));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //选中了tab的逻辑
                String name=tab.getText().toString();
                if (name=="游客轨迹"){
                    mPicChart.setVisibility(View.GONE);
                    xingwei_ConstraintLayout.setVisibility(View.VISIBLE);
                    mBarChart.setVisibility(View.GONE);
                    pianhao_linearLayout.setVisibility(View.GONE);
                }
                if (name=="出行方式"){
                    mPicChart.setVisibility(View.VISIBLE);
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    mBarChart.setVisibility(View.GONE);
                    pianhao_linearLayout.setVisibility(View.GONE);
                }
                if (name=="停留时间"){
                    mPicChart.setVisibility(View.GONE);
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    mBarChart.setVisibility(View.VISIBLE);
                    pianhao_linearLayout.setVisibility(View.GONE);
                }
                if (name=="偏好分析"){
                    mPicChart.setVisibility(View.GONE);
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    mBarChart.setVisibility(View.GONE);
                    pianhao_linearLayout.setVisibility(View.VISIBLE);
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
        for (int i = 0; i < 5; i++) {
            model=new YoukelaiyuanEntity();
            model.setId(i+1+"");
            model.setProvince("酒店"+i);
            model.setBaifenbi("4.4");
            youkedatas.add(model);
        }

    }

    private void initviewmRadarChart() {
        setDatamRadarChart();
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));

        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);
        mChart.setNoDataText("获取数据失败");
        //mChart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        MarkerView mv = new RadarMarkerView(getActivity(), R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"文化", "客栈", "美食", "旅游线路", "景点"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.GRAY);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setEnabled(false);//是否显示
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.GRAY);
    }

    private void setDatamRadarChart() {

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        //ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < 5; i++) {
            float val1 = (float) (Math.random() * 80) + 20;
            entries1.add(new RadarEntry(val1));

           /* float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));*/
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "兴趣前五名");
        set1.setColor(Color.rgb(121, 162, 175));
        set1.setFillColor(Color.rgb(121, 162, 175));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        /*RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);*/

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        //sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);//显示数值
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);
        mChart.invalidate();
    }

    private void initoneListPopupIfNeed() {
        if (mListPopup == null) {

            String[] listItems = new String[]{
                    "机场",
                    "火车站",
                    "高速路口"
            };
            List<String> data = new ArrayList<>();

            Collections.addAll(data, listItems);

            final ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data);

            mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 120), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    one=adapterView.getItemAtPosition(i).toString();
                    //Toast.makeText(getActivity(), one, Toast.LENGTH_SHORT).show();
                    if (one=="机场"){
                        latLngs.clear();
                        polyline.remove();
                        //latLngs = new ArrayList<LatLng>();
                        latLngs.add(new LatLng(25.646788,100.322685));
                        latLngs.add(new LatLng(25.583866,100.230932));
                        latLngs.add(new LatLng(25.609217,100.221362));
                        latLngs.add(new LatLng(25.637812,100.206041));
                        latLngs.add(new LatLng(25.704727,100.168877));
                        latLngs.add(new LatLng(25.758465,100.139694));
                        latLngs.add(new LatLng(25.845784,100.133343));
                        polyline=tencentMap.addPolyline(new PolylineOptions().
                                addAll(latLngs).color(0xff00ff00). width(5f));
                    }
                    if (one=="火车站"){
                        latLngs.clear();
                        polyline.remove();
                        //latLngs = new ArrayList<LatLng>();
                        latLngs.add(new LatLng(25.589440,100.251274));
                        latLngs.add(new LatLng(25.583866,100.230932));
                        latLngs.add(new LatLng(25.609217,100.221362));
                        latLngs.add(new LatLng(25.637812,100.206041));
                        latLngs.add(new LatLng(25.704727,100.168877));
                        latLngs.add(new LatLng(25.758465,100.139694));
                        latLngs.add(new LatLng(25.845784,100.133343));
                        polyline=tencentMap.addPolyline(new PolylineOptions().
                                addAll(latLngs).color(0xff00ff00). width(5f));
                    }
                    if (one=="高速路口"){
                        latLngs.clear();
                        polyline.remove();
                        //latLngs = new ArrayList<LatLng>();
                        latLngs.add(new LatLng(25.591762,100.253248));
                        latLngs.add(new LatLng(25.583866,100.230932));
                        latLngs.add(new LatLng(25.609217,100.221362));
                        latLngs.add(new LatLng(25.637812,100.206041));
                        latLngs.add(new LatLng(25.704727,100.168877));
                        latLngs.add(new LatLng(25.758465,100.139694));
                        latLngs.add(new LatLng(25.845784,100.133343));
                        polyline=tencentMap.addPolyline(new PolylineOptions().
                                addAll(latLngs).color(0xff00ff00). width(5f));
                    }
                    mListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (one!=null){
                        choose_button.setText(one);
                    }else {
                        choose_button.setText("机场");
                    }

                }
            });
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
        strings.add(new PieEntry(30f,"火车"));
        strings.add(new PieEntry(170f,"飞机"));
        strings.add(new PieEntry(60f,"自驾"));
        strings.add(new PieEntry(50f,"客车"));
        PieDataSet dataSet = new PieDataSet(strings,"出行方式");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.app_color_theme_8));
        colors.add(getResources().getColor(R.color.app_color_theme_7));
        colors.add(getResources().getColor(R.color.app_color_theme_9));
        colors.add(getResources().getColor(R.color.app_color_theme_5));
        colors.add(getResources().getColor(R.color.app_color_theme_4));
        colors.add(getResources().getColor(R.color.app_color_theme_3));

        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔

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

    /*
   条形图
    */
    private void initviewmBarChart() {
        setDatamBarChart();
        //修改图表的描述信息
        //mBarChart.setDescription("Android Java 薪资分析");
        //设置动画
        mBarChart.animateXY(1000,1000);
        mBarChart.setDrawBarShadow(false);//设置每个直方图阴影为false
        mBarChart.setDrawValueAboveBar(true);//这里设置为true每一个直方图的值就会显示在直方图的顶部

        mBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);
        mBarChart.animateY(1400);
        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);
        //mBarChart.setDrawGridBackground(false);//设置不显示网格
        mBarChart.setScaleEnabled(true);//设置是否可以缩放
        mBarChart.setTouchEnabled(true);//设置是否可以触摸
        mBarChart.setDragEnabled(true);//设置是否可以拖拽
        mBarChart.setNoDataText("数据获取失败");
        mBarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);//颜色数值
        //X轴
        //自定义设置横坐标
        //IAxisValueFormatter xValueFormatter = new ExamModelOneXValueFormatter(xListValue);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                if (m==0){
                    return "综合";
                }else if (m==1){
                    return "大理古城";
                }else if (m==2){
                    return "下关";
                }else if (m==3){
                    return "喜洲古镇";
                }else if (m==4){
                    return "双廊";
                }
                return "";
            }
        });
        //左边Y轴
        YAxis leftYAxis = mBarChart.getAxisLeft();
        leftYAxis.setDrawGridLines(true);//设置从Y轴左侧发出横线
        leftYAxis.setAxisMinimum(0.0f);
        leftYAxis.setEnabled(true);//设置显示左边Y坐标
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //右边Y轴
        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setEnabled(false);//右侧不显示Y轴
        rightAxis.setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙

        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);

        //自定义markView,点击显示更多信息
        /*MyMarkView markerView = new MyMarkView(getActivity(),R.layout.custom_marker_view);
        markerView.setChartView(mBarChart);
        mBarChart.setMarker(markerView);*/


        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d("mm", String.valueOf(e.getX()));
                Log.d("ww", String.valueOf(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setDatamBarChart() {

        //每一个柱状图的数据
        List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组

        for (int i = 0; i < 5; i++) {//添加数据源
            yVals.add(new BarEntry(i,(float) Math.random()*520 + 1));
        }
        BarDataSet dataSet = new BarDataSet(yVals, "停留时间条形统计图");//一组柱状图
        //dataSet.setColor(Color.LTGRAY);//设置第yi组数据颜色
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12);//修改一组柱状图的文字大小
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int mm=(int)entry.getY();
                return mm+"分钟";
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        mBarChart.setData(data);

    }





    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mMapView.onStop();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
    }
}
