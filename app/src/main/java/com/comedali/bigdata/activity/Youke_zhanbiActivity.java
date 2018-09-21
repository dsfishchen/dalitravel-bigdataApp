package com.comedali.bigdata.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.utils.MyMarkView;
import com.comedali.bigdata.utils.NetworkUtil;
import com.comedali.bigdata.utils.Youke_zhanbiMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tencent.tencentmap.mapsdk.maps.model.HeatDataNode;
import com.tencent.tencentmap.mapsdk.maps.model.HeatOverlayOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 刘杨刚 on 2018/9/l4.
 */
public class Youke_zhanbiActivity extends AppCompatActivity{
    private CommonTitleBar commonTitleBar;
    private PieChart mPicChart;
    private BarChart mBarChart;
    private ScrollView scrollView;
    private OkHttpClient client;
    private BarChart chart_mYi;
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
        //mPicChart = findViewById(R.id.pie_chart);
        mBarChart = findViewById(R.id.chart1);
        chart_mYi=findViewById(R.id.chart_mYi);
        scrollView=findViewById(R.id.mm_scrollView);
        initdata();
        initviewmYi();
        //initviewmPicChart();
        initviewmBarChart();
    }
    /**
     * 有网时候的缓存
     */
    final Interceptor NetCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            int onlineCacheTime = 0;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age="+onlineCacheTime)
                    .removeHeader("Pragma")
                    .build();
        }
    };
    /**
     * 没有网时候的缓存
     */
    final Interceptor OfflineCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.checkNet(Youke_zhanbiActivity.this)) {
                int offlineCacheTime = 60*60*24*7;//离线的时候的缓存的过期时间
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                        .build();
            }
            return chain.proceed(request);
        }
    };
    private void initdata() {
        File httpCacheDirectory = new File(this.getExternalCacheDir(), "okhttpCache1");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        client=mBuilder
                .addNetworkInterceptor(NetCacheInterceptor)
                .addInterceptor(OfflineCacheInterceptor)
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        String url="http://192.168.190.119:8080/flowmeter/scale";
        final Request request = new Request.Builder()
                .url(url)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("数据请求", "失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            final String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray result1 = new JSONArray(result);
                                final List<PieEntry> strings = new ArrayList<>();
                                final List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组
                                final List<BarEntry> Yi = new ArrayList<>();//Y轴方向第一组数组
                                for (int i=0;i<result1.length();i++){
                                    JSONObject jsonObject=result1.getJSONObject(i);
                                    String area_name=jsonObject.getString("area_name");
                                    String scale=jsonObject.getString("scale");
                                    float w= Float.parseFloat(scale.substring(0,scale.length() - 1));
                                    int nums=jsonObject.getInt("nums");
                                    strings.add(new PieEntry(nums,area_name));//饼图数据添加
                                    yVals.add(new BarEntry(i,nums));//柱状图数据添加
                                    Yi.add(new BarEntry(i,w));
                                }
                                //setDatamPicChart(strings);
                                //setDatamBarChart(yVals);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setDatamBarChart(yVals);
                                        //setDatamPicChart(strings);
                                        setDataYi(Yi);
                                        //mPicChart.animateY(1400);//设置Y轴动画
                                        chart_mYi.animateY(1400);
                                        mBarChart.animateY(1400);
                                    }
                                });
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
    }


    /*
    条形图
     */
    private void initviewmYi() {
        //setDatamBarChart(yVals);
        //修改图表的描述信息
        //mBarChart.setDescription("Android Java 薪资分析");
        //设置动画
        chart_mYi.animateXY(1000,1000);
        chart_mYi.setDrawBarShadow(false);//设置每个直方图阴影为false
        chart_mYi.setDrawValueAboveBar(true);//这里设置为true每一个直方图的值就会显示在直方图的顶部

        chart_mYi.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart_mYi.setMaxVisibleValueCount(60);
        chart_mYi.animateY(1400);
        // scaling can now only be done on x- and y-axis separately
        chart_mYi.setPinchZoom(false);
        //mBarChart.setDrawGridBackground(false);//设置不显示网格
        chart_mYi.setScaleEnabled(true);//设置是否可以缩放
        chart_mYi.setTouchEnabled(true);//设置是否可以触摸
        chart_mYi.setDragEnabled(true);//设置是否可以拖拽
        chart_mYi.setNoDataText("正在获取数据...");
        chart_mYi.setNoDataTextColor(Color.WHITE);
        chart_mYi.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);//颜色数值
        //X轴
        //自定义设置横坐标
        //IAxisValueFormatter xValueFormatter = new ExamModelOneXValueFormatter(xListValue);
        XAxis xAxis = chart_mYi.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.rgb(255,255,255));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                if (m==0){
                    return "宾川县";
                }else if (m==1){
                    return "大理市";
                }else if (m==2){
                    return "洱源县";
                }else if (m==3){
                    return "鹤庆县";
                }else if (m==4){
                    return "剑川县";
                }else if (m==5){
                    return "弥渡县";
                }else if (m==6){
                    return "南涧彝族自治县";
                }else if (m==7){
                    return "巍山彝族回族自治";
                }else if (m==8){
                    return "祥云县";
                }else if (m==9){
                    return "漾濞彝族自治县";
                }else if (m==10){
                    return "永平县";
                }else if (m==11){
                    return "云龙县";
                }
                return "";
            }
        });
        //左边Y轴
        YAxis leftYAxis = chart_mYi.getAxisLeft();
        leftYAxis.setDrawGridLines(true);//设置从Y轴左侧发出横线
        leftYAxis.setAxisMinimum(0.0f);
        leftYAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        leftYAxis.setEnabled(true);//设置显示左边Y坐标
        leftYAxis.setTextColor(Color.rgb(255,255,255));
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                return m+"%";
            }
        });

        //右边Y轴
        YAxis rightAxis = chart_mYi.getAxisRight();
        rightAxis.setEnabled(false);//右侧不显示Y轴
        rightAxis.setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙

        Legend l = chart_mYi.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setTextColor(Color.rgb(255,255,255));
        l.setXEntrySpace(4f);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);

        //自定义markView,点击显示更多信息
        Youke_zhanbiMarkerView markerView = new Youke_zhanbiMarkerView(Youke_zhanbiActivity.this,R.layout.custom_marker_view);
        markerView.setChartView(chart_mYi);
        chart_mYi.setMarker(markerView);



        chart_mYi.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.d("mm", String.valueOf(e.getX()));
                // Log.d("ww", String.valueOf(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setDataYi(List<BarEntry> mm) {

        //每一个柱状图的数据
        //List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组

        /*for (int i = 0; i < 12; i++) {//添加数据源
            //yVals.add(new BarEntry(i,(float) Math.random()*520 + 1));
            int yVal = (int) (Math.random()*520 + 1);
            yVals.add(new BarEntry(i,yVal));
        }*/
        BarDataSet dataSet = new BarDataSet(mm, "十二县市游客数量条形统计图");//一组柱状图
        //dataSet.setColor(Color.LTGRAY);//设置第yi组数据颜色
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12);//修改一组柱状图的文字大小
        dataSet.setValueTextColor(Color.rgb(255,255,255));
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                double mm=(double) entry.getY();
                mm=(double)Math.round(mm*100)/100;
                return mm+"%";
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        chart_mYi.setData(data);
        chart_mYi.invalidate();//重绘图表
    }




    /*
    条形图
     */
    private void initviewmBarChart() {
        //setDatamBarChart(yVals);
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
        mBarChart.setNoDataText("正在获取数据...");
        mBarChart.setNoDataTextColor(Color.WHITE);
        mBarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);//颜色数值
        //X轴
        //自定义设置横坐标
        //IAxisValueFormatter xValueFormatter = new ExamModelOneXValueFormatter(xListValue);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.rgb(255,255,255));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                if (m==0){
                    return "宾川县";
                }else if (m==1){
                    return "大理市";
                }else if (m==2){
                    return "洱源县";
                }else if (m==3){
                    return "鹤庆县";
                }else if (m==4){
                    return "剑川县";
                }else if (m==5){
                    return "弥渡县";
                }else if (m==6){
                    return "南涧彝族自治县";
                }else if (m==7){
                    return "巍山彝族回族自治";
                }else if (m==8){
                    return "祥云县";
                }else if (m==9){
                    return "漾濞彝族自治县";
                }else if (m==10){
                    return "永平县";
                }else if (m==11){
                    return "云龙县";
                }
                return "";
            }
        });
        //左边Y轴
        YAxis leftYAxis = mBarChart.getAxisLeft();
        leftYAxis.setDrawGridLines(true);//设置从Y轴左侧发出横线
        leftYAxis.setAxisMinimum(0.0f);
        leftYAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        leftYAxis.setEnabled(true);//设置显示左边Y坐标
        leftYAxis.setTextColor(Color.rgb(255,255,255));
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
        l.setTextColor(Color.rgb(255,255,255));
        l.setXEntrySpace(4f);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);

        //自定义markView,点击显示更多信息
        MyMarkView markerView = new MyMarkView(Youke_zhanbiActivity.this,R.layout.custom_marker_view);
        markerView.setChartView(mBarChart);
        mBarChart.setMarker(markerView);



        //解决滑动冲突问题
        mBarChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });



        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.d("mm", String.valueOf(e.getX()));
               // Log.d("ww", String.valueOf(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setDatamBarChart(List<BarEntry> mm) {

        //每一个柱状图的数据
        //List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组

        /*for (int i = 0; i < 12; i++) {//添加数据源
            //yVals.add(new BarEntry(i,(float) Math.random()*520 + 1));
            int yVal = (int) (Math.random()*520 + 1);
            yVals.add(new BarEntry(i,yVal));
        }*/
        BarDataSet dataSet = new BarDataSet(mm, "十二县市游客数量条形统计图 单位：人");//一组柱状图
        //dataSet.setColor(Color.LTGRAY);//设置第yi组数据颜色
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12);//修改一组柱状图的文字大小
        dataSet.setValueTextColor(Color.rgb(255,255,255));
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int mm=(int)entry.getY();
                return mm+"";
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        mBarChart.setData(data);
        mBarChart.invalidate();//重绘图表
    }

    /*
    饼图
     */
    /*private void initviewmPicChart() {

        //setDatamPicChart(strings);

        Description description = new Description();
        description.setText("");
        mPicChart.setDescription(description);//右下角字,添加图表描述
        mPicChart.setCenterText("客流量占比");//中心字
        mPicChart.setCenterTextSize(16);//中心字大小
        mPicChart.setHoleRadius(38f);//设置圆孔半径
        mPicChart.setTransparentCircleRadius(42f);//设置半透明圈的宽度
        mPicChart.setNoDataText("正在获取数据...");//设置饼图没有数据时显示的文本
        mPicChart.setUsePercentValues(true); //Boolean类型  设置图表是否使用百分比
        //picChart.setTransparentCircleColor(R.color.qingse);//设置环形图与中间空心圆之间的环形的颜色
        mPicChart.setHighlightPerTapEnabled(true);//设置点击Item高亮是否可用
        mPicChart.setExtraOffsets(5, 0, 5, 5);
        mPicChart.animateY(1400);//设置Y轴动画
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = mPicChart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
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

    private void setDatamPicChart(List<PieEntry> strings) {
        *//*List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(30f,"云龙县"));
        strings.add(new PieEntry(170f,"大理市"));
        strings.add(new PieEntry(60f,"宾川县"));
        strings.add(new PieEntry(50f,"南涧县"));
        strings.add(new PieEntry(30f,"剑川县"));
        strings.add(new PieEntry(20f,"永平县"));
        strings.add(new PieEntry(45f,"鹤庆县"));
        strings.add(new PieEntry(32f,"洱源县"));
        strings.add(new PieEntry(45f,"祥云县"));
        strings.add(new PieEntry(56f,"漾濞县"));
        strings.add(new PieEntry(24f,"巍山县"));
        strings.add(new PieEntry(35f,"弥渡县"));*//*
        PieDataSet dataSet = new PieDataSet(strings,"代表");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.app_color_theme_1));
        colors.add(getResources().getColor(R.color.app_color_theme_2));
        colors.add(getResources().getColor(R.color.monsoon));
        colors.add(getResources().getColor(R.color.app_color_theme_9));
        colors.add(getResources().getColor(R.color.app_color_theme_8));
        colors.add(getResources().getColor(R.color.app_color_theme_7));
        colors.add(getResources().getColor(R.color.app_color_theme_6));
        colors.add(getResources().getColor(R.color.app_color_theme_5));
        colors.add(getResources().getColor(R.color.app_color_theme_4));
        colors.add(getResources().getColor(R.color.app_color_theme_3));

        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());//转化百分比
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.rgb(255,255,255));

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.3f);//设置连接线的长度
        dataSet.setValueLinePart2Length(0.6f);
        dataSet.setValueLineColor(Color.rgb(255,255,255));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        mPicChart.highlightValues(null);
        mPicChart.setData(pieData);//设置数据
        mPicChart.invalidate();//重绘图表
    }*/


}
