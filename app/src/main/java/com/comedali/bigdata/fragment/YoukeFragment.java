package com.comedali.bigdata.fragment;


import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
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
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private PieChart wPicChart;
    private RecyclerView youke_recyclerView;
    private TabLayout mTabLayout;
    private YoukelaiyuanAdapter Laiyuanadapter;
    private List<YoukelaiyuanEntity> youkedatas;
    private LinearLayout youke_linearlayout;
    private LinearLayout youke2_linearLayout;
    private OkHttpClient client;
    private TextView jinru_nan;
    private TextView jinru_nv;
    private TextView sousuo_nan;
    private TextView sousuo_nv;
    private String one;
    private TextView shengshi_choose;
    private QMUIListPopup mListPopup;
    private ScrollView age_scrollView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.youke_yi,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());//状态栏字体颜色--白色
        mTabLayout=view.findViewById(R.id.youke_SlidingTabLayout);
        youke_recyclerView=view.findViewById(R.id.youke_recyclerView);
        shengshi_choose=view.findViewById(R.id.shengshi_choose);
        mPicChart = view.findViewById(R.id.age_chart);
        wPicChart = view.findViewById(R.id.sousuo_chart);
        youke_linearlayout=view.findViewById(R.id.youke_linearlayout);
        youke_linearlayout.setVisibility(View.GONE);//隐藏数据
        youke2_linearLayout=view.findViewById(R.id.youke2_linearLayout);
        youke2_linearLayout.setVisibility(View.GONE);
        age_scrollView=view.findViewById(R.id.age_scrollView);
        mTabLayout.addTab(mTabLayout.newTab().setText("年龄结构"));
        mTabLayout.addTab(mTabLayout.newTab().setText("性别比例"));
        mTabLayout.addTab(mTabLayout.newTab().setText("游客来源"));
        initviewmPicChart();
        initviewmPicChart1();

        jinru_nan=view.findViewById(R.id.jinru_nan);
        jinru_nv=view.findViewById(R.id.jinru_nv);
        sousuo_nan=view.findViewById(R.id.sousuo_nan);
        sousuo_nv=view.findViewById(R.id.sousuo_nv);
        initData();
        initAge();
        initAge1();
        initJinruSex();
        initSousuoSex();
        //Log.d("youkedatas", String.valueOf(youkedatas));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        youke_recyclerView.setLayoutManager(layoutManager);
        Laiyuanadapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
        Laiyuanadapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
        Laiyuanadapter.isFirstOnly(false);//重复执行可设置
        //给RecyclerView设置适配器
        youke_recyclerView.setAdapter(Laiyuanadapter);
        youke_recyclerView.setVisibility(View.GONE);//隐藏控件
        //添加Android自带的分割线
        youke_recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //选中了tab的逻辑
                String name=tab.getText().toString();
                if (name=="年龄结构"){
                    age_scrollView.setVisibility(View.VISIBLE);
                    youke_recyclerView.setVisibility(View.GONE);
                    youke_linearlayout.setVisibility(View.GONE);
                    youke2_linearLayout.setVisibility(View.GONE);
                    mPicChart.animateY(1400);//设置Y轴动画
                    wPicChart.animateY(1400);//设置Y轴动画
                }
                if (name=="性别比例"){
                    age_scrollView.setVisibility(View.GONE);
                    youke_recyclerView.setVisibility(View.GONE);
                    youke_linearlayout.setVisibility(View.GONE);
                    youke2_linearLayout.setVisibility(View.VISIBLE);

                }
                if (name=="游客来源"){
                    age_scrollView.setVisibility(View.GONE);
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
        shengshi_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });
        return view;
    }
    private void initoneListPopupIfNeed() {
        if (mListPopup == null) {

            String[] listItems = new String[]{
                    "省份",
                    "城市"
            };
            List<String> data = new ArrayList<>();

            Collections.addAll(data, listItems);

            final ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data);

            mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 120), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    one=adapterView.getItemAtPosition(i).toString();
                    if (one.equals("省份")){
                        initData();
                    }
                    if (one.equals("城市")){
                        initData1();
                    }
                    mListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (one!=null){
                        shengshi_choose.setText(one);
                    }else {
                        shengshi_choose.setText("省市");
                    }

                }
            });
        }
    }


    /**
     * 有网时候的缓存
     */
    final Interceptor NetCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            int onlineCacheTime = 60*3;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/agedistribution";
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
                                float m1 = 0;
                                float m2 = 0;
                                float m3 = 0;
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String age_name=jsonObject.getString("age_name");
                                    String nums=jsonObject.getString("nums");
                                    float age= Float.parseFloat(nums);
                                    if (age_name.equals("60-70岁")){
                                        m1=age;
                                    }
                                    if (age_name.equals("70-80岁")){
                                        m2=age;
                                    }
                                    if (age_name.equals("80-90岁")){
                                        m3=age;
                                    }
                                    if(i<4){
                                        strings.add(new PieEntry(age,age_name));
                                    }
                                }
                                if (null==strings||strings.size()==0){
                                    mPicChart.clear();
                                    mPicChart.setNoDataText("超时，请重试");
                                }else {
                                    strings.add(new PieEntry(m1+m2+m3,"其它"));
                                    setDatamPicChart(strings);
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
    private void initAge1() {
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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/ageofsearch";
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
                                    String percent=jsonObject.getString("percent");
                                    float age= Float.parseFloat(percent);
                                    strings.add(new PieEntry(age,age_name));

                                }
                                if (null==strings||strings.size()==0){
                                    wPicChart.clear();
                                    wPicChart.setNoDataText("超时，请重试");
                                }else {
                                    setDatamPicChart1(strings);
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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/sexdistribution";
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
                                    m[i]=percent+"";
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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/sexoffind";
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
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        youkedatas=new ArrayList<>();
        if (null == youkedatas || youkedatas.size() ==0 ){
            //为空的情况
        }else {
            youkedatas.clear();
        }
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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/touristsource";
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
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tipDialog.dismiss();
                            }
                        });
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
                                    String user_province=jsonObject.getString("user_province");
                                    String percent=jsonObject.getString("percent");
                                    String nums=jsonObject.getString("nums");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(user_province);
                                    model.setBaifenbi(percent);
                                    youkedatas.add(model);
                                }
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Laiyuanadapter.notifyDataSetChanged();
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        youke_recyclerView.setLayoutManager(layoutManager);
                                        Laiyuanadapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
                                        Laiyuanadapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
                                        Laiyuanadapter.isFirstOnly(false);//重复执行可设置
                                        //给RecyclerView设置适配器
                                        youke_recyclerView.setAdapter(Laiyuanadapter);
                                        //添加Android自带的分割线
                                        youke_recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                                        tipDialog.dismiss();
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
    private void initData1() {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        youkedatas=new ArrayList<>();
        if (null == youkedatas || youkedatas.size() ==0 ){
            //为空的情况
        }else {
            youkedatas.clear();
        }

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
        String url="http://home.comedali.com:8088/bigdataservice/touristproperty/touristcity";
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
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tipDialog.dismiss();
                            }
                        });
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
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String user_city=jsonObject.getString("user_city");
                                    String percent=jsonObject.getString("percent");
                                    String nums=jsonObject.getString("nums");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(user_city);
                                    model.setBaifenbi(percent);
                                    youkedatas.add(model);
                                }
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Laiyuanadapter.notifyDataSetChanged();
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                        youke_recyclerView.setLayoutManager(layoutManager);
                                        Laiyuanadapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
                                        Laiyuanadapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
                                        Laiyuanadapter.isFirstOnly(false);//重复执行可设置
                                        //给RecyclerView设置适配器
                                        youke_recyclerView.setAdapter(Laiyuanadapter);
                                        //添加Android自带的分割线
                                        youke_recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                                        tipDialog.dismiss();
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
        //mPicChart.setExtraOffsets(0, 10, 0, 0);
        mPicChart.animateY(1400);//设置Y轴动画
        mPicChart.setRotationAngle(10);
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = mPicChart.getLegend();
        l.setEnabled(true);
        //l.setTypeface(Typeface.DEFAULT_BOLD);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);*/
        l.setFormSize(10f);//设置图例的大小
        l.setTextColor(Color.rgb(255,255,255));
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
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
        colors.add(getResources().getColor(R.color.age5));
        colors.add(getResources().getColor(R.color.age4));
        colors.add(getResources().getColor(R.color.age3));
        colors.add(getResources().getColor(R.color.age2));
        colors.add(getResources().getColor(R.color.age1));
        colors.add(getResources().getColor(R.color.age6));
        colors.add(getResources().getColor(R.color.app_color_theme_7));
        dataSet.setColors(colors);
        //dataSet.setSliceSpace(4f);//设置饼块之间的间隔

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
    private void initviewmPicChart1() {
        //setDatamPicChart1();
        Description description = new Description();
        description.setText("");
        wPicChart.setDescription(description);//右下角字,添加图表描述
        //mPicChart.setCenterText("客流量占比");//中心字
        //mPicChart.setCenterTextSize(16);//中心字大小
        wPicChart.setHoleRadius(0f);//设置圆孔半径
        wPicChart.setTransparentCircleRadius(0f);//设置半透明圈的宽度
        wPicChart.setNoDataText("正在获取数据...");//设置饼图没有数据时显示的文本
        wPicChart.setNoDataTextColor(Color.WHITE);
        wPicChart.setUsePercentValues(true); //Boolean类型  设置图表是否使用百分比
        //picChart.setTransparentCircleColor(R.color.qingse);//设置环形图与中间空心圆之间的环形的颜色
        wPicChart.setHighlightPerTapEnabled(true);//设置点击Item高亮是否可用
        //mPicChart.setExtraOffsets(0, 10, 0, 0);
        wPicChart.animateY(1400);//设置Y轴动画
        wPicChart.setRotationAngle(10);
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = wPicChart.getLegend();
        l.setEnabled(true);
        //l.setTypeface(Typeface.DEFAULT_BOLD);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);*/
        l.setFormSize(10f);//设置图例的大小
        l.setTextColor(Color.rgb(255,255,255));
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        //结束
        wPicChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
    private void setDatamPicChart1(List<PieEntry> strings) {
        /*List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(3f,"19岁及以下"));
        strings.add(new PieEntry(12f,"20-29岁"));
        strings.add(new PieEntry(51f,"30-39岁"));
        strings.add(new PieEntry(30f,"40-49岁"));
        strings.add(new PieEntry(4f,"50岁以上"));*/
        PieDataSet dataSet = new PieDataSet(strings,"年龄占比");

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.age5));
        colors.add(getResources().getColor(R.color.age4));
        colors.add(getResources().getColor(R.color.age3));
        colors.add(getResources().getColor(R.color.age2));
        colors.add(getResources().getColor(R.color.age1));
        colors.add(getResources().getColor(R.color.age6));
        colors.add(getResources().getColor(R.color.app_color_theme_7));
        dataSet.setColors(colors);
        //dataSet.setSliceSpace(4f);//设置饼块之间的间隔

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

        wPicChart.highlightValues(null);
        wPicChart.setData(pieData);//设置数据
        wPicChart.invalidate();//重绘图表
    }
}
