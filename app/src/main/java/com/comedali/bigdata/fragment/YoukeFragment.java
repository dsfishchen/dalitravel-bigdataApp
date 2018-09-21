package com.comedali.bigdata.fragment;


import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.comedali.bigdata.utils.NetworkUtil;
import com.comedali.bigdata.utils.ZTLanUtils;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

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
    private OkHttpClient client;
    private TextView jinru_nan;
    private TextView jinru_nv;
    private TextView sousuo_nan;
    private TextView sousuo_nv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.youke_yi,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());//状态栏字体颜色--白色
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

        jinru_nan=view.findViewById(R.id.jinru_nan);
        jinru_nv=view.findViewById(R.id.jinru_nv);
        sousuo_nan=view.findViewById(R.id.sousuo_nan);
        sousuo_nv=view.findViewById(R.id.sousuo_nv);

        initData();
        initAge();
        initJinruSex();
        initSousuoSex();
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
                    mPicChart.animateY(1400);//设置Y轴动画
                }
                if (name=="性别比例"){
                    mPicChart.setVisibility(View.GONE);
                    youke_recyclerView.setVisibility(View.GONE);
                    youke_linearlayout.setVisibility(View.GONE);
                    youke2_linearLayout.setVisibility(View.VISIBLE);

                }
                if (name=="游客来源"){
                    mPicChart.setVisibility(View.GONE);
                    youke_recyclerView.setVisibility(View.VISIBLE);
                    youke_linearlayout.setVisibility(View.VISIBLE);
                    youke2_linearLayout.setVisibility(View.GONE);

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



    /**
     * 有网时候的缓存
     */
    final Interceptor NetCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            int onlineCacheTime = 60;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
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
            if (!NetworkUtil.checkNet(getActivity())) {
                int offlineCacheTime = 60*60*24*7;//离线的时候的缓存的过期时间
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                        .build();
            }
            return chain.proceed(request);
        }
    };
    private void initAge() {
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache3");
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
        String url="http://192.168.190.119:8080/touristproperty/agedistribution";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("数据请求", "失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            //Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            final String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                final JSONArray num = new JSONArray(result);
                                final List<PieEntry> strings=new ArrayList<>();
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String age_name=jsonObject.getString("age_name");
                                    String nums=jsonObject.getString("percent");
                                    float age= Float.parseFloat(nums);
                                    strings.add(new PieEntry(age,age_name));
                                }
                                setDatamPicChart(strings);
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
    private void initJinruSex() {

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache3");
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
        String url="http://192.168.190.119:8080/touristproperty/sexdistribution";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("数据请求", "失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            //Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            final String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                final JSONArray num = new JSONArray(result);
                                final List<PieEntry> strings=new ArrayList<>();
                                final String[] m=new String[num.length()];
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String percent=jsonObject.getString("percent");
                                    m[i]=percent+"%";
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        jinru_nan.setText(m[0]);
                                        jinru_nv.setText(m[1]);
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
    private void initSousuoSex() {

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache3");
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
        String url="http://192.168.190.119:8080/touristproperty/sexoffind";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("数据请求", "失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            //Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            final String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                final JSONArray num = new JSONArray(result);
                                final List<PieEntry> strings=new ArrayList<>();
                                final String[] m=new String[num.length()];
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String percent=jsonObject.getString("percent");
                                    m[i]=percent+"%";
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sousuo_nan.setText(m[0]);
                                        sousuo_nv.setText(m[1]);
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
    private void initData() {
        youkedatas=new ArrayList<>();


        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache3");
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
        String url="http://192.168.190.119:8080/touristproperty/touristsource";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d("数据请求", "失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            //Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            final String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                final JSONArray num = new JSONArray(result);
                                final List<PieEntry> strings=new ArrayList<>();
                                final String[] m=new String[num.length()];
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String user_province=jsonObject.getString("user_province");
                                    String percent=jsonObject.getString("percent");
                                    String nums=jsonObject.getString("nums");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(user_province);
                                    model.setBaifenbi(percent);
                                    youkedatas.add(model);
                                }

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
        饼图
         */
    private void initviewmPicChart() {
        //setDatamPicChart();
        Description description = new Description();
        description.setText("");
        mPicChart.setDescription(description);//右下角字,添加图表描述
        //mPicChart.setCenterText("客流量占比");//中心字
        //mPicChart.setCenterTextSize(16);//中心字大小
        mPicChart.setHoleRadius(0f);//设置圆孔半径
        mPicChart.setTransparentCircleRadius(0f);//设置半透明圈的宽度
        mPicChart.setNoDataText("正在获取数据...");//设置饼图没有数据时显示的文本
        mPicChart.setNoDataTextColor(Color.WHITE);
        mPicChart.setUsePercentValues(true); //Boolean类型  设置图表是否使用百分比
        //picChart.setTransparentCircleColor(R.color.qingse);//设置环形图与中间空心圆之间的环形的颜色
        mPicChart.setHighlightPerTapEnabled(true);//设置点击Item高亮是否可用
        //mPicChart.setExtraOffsets(5, 0, 5, 5);
        mPicChart.animateY(1400);//设置Y轴动画
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = mPicChart.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setFormSize(10f);//设置图例的大小
        l.setTextColor(Color.rgb(255,255,255));
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
        /*List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(30f,"19岁及以下"));
        strings.add(new PieEntry(170f,"20-29岁"));
        strings.add(new PieEntry(60f,"30-39岁"));
        strings.add(new PieEntry(50f,"40-49岁"));
        strings.add(new PieEntry(30f,"50岁以上"));*/
        PieDataSet dataSet = new PieDataSet(strings,"年龄占比");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        //colors.add(getResources().getColor(R.color.app_color_theme_8));
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
        pieData.setValueTextColor(Color.DKGRAY);

        dataSet.setValueLinePart1OffsetPercentage(50.f);
        dataSet.setValueLinePart1Length(0.1f);//设置连接线的长度
        dataSet.setValueLinePart2Length(1.0f);
        dataSet.setValueLineColor(Color.rgb(255,255,255));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        mPicChart.highlightValues(null);
        mPicChart.setData(pieData);//设置数据
        mPicChart.invalidate();//重绘图表
    }
}
