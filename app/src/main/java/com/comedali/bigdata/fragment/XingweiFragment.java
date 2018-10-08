package com.comedali.bigdata.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.comedali.bigdata.utils.MyMarkView;
import com.comedali.bigdata.utils.NetworkUtil;
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
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
public class XingweiFragment extends Fragment {
    private TabLayout mTabLayout;
    private PieChart mPicChart;
    private ConstraintLayout xingwei_ConstraintLayout;
    private Button choose_button;
    private Button choose_button1;
    private Button choose_yes;
    private QMUIListPopup mListPopup;
    private QMUIListPopup mListPopup1;
    private TextView pingfen;
    private String one;
    private String two;
    private TextureMapView mMapView;
    private TencentMap tencentMap;
    private BarChart mBarChart;
    private ConstraintLayout pianhao_ConstraintLayout;
    private RadarChart mRadarChart;//雷达图
    private RecyclerView jiudian_recycleView;
    private YoukelaiyuanAdapter adapter;
    private List<YoukelaiyuanEntity> youkedatas= new ArrayList<>();
    private List<LatLng> latLngs1;
    private List<LatLng> latLngs2;
    private List<LatLng> latLngs3;
    private List<LatLng> latLngs4;
    private List<LatLng> latLngs5;
    private Polyline polyline1;
    private Polyline polyline2;
    private Polyline polyline3;
    private Polyline polyline4;
    private Polyline polyline5;
    private Button pianhao_choose;
    private TextView top5_title;
    private TextView top_name;
    private OkHttpClient client;
    private Marker marker1;
    private Marker marker2;
    private LinearLayout wudi_lin;
    private ConstraintLayout wudi_linear;
    private Button chuxing_time;
    private Button chuxing_cx;
    private TextView chuxing_text;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.xingwei_er,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());//状态栏字体颜色--白色
        top_name=view.findViewById(R.id.top_name);
        top5_title=view.findViewById(R.id.top5_title);
        pianhao_choose=view.findViewById(R.id.pianhao_choose);
        wudi_lin=view.findViewById(R.id.wudi_lin);
        wudi_linear=view.findViewById(R.id.wudi_linear);
        chuxing_time=view.findViewById(R.id.chuxing_time);
        chuxing_cx=view.findViewById(R.id.chuxing_cx);
        chuxing_text=view.findViewById(R.id.chuxing_text);
        wudi_linear.setVisibility(View.GONE);
        wudi_lin.setVisibility(View.GONE);
        jiudian_recycleView=view.findViewById(R.id.jiudian_recycleView);
        pingfen=view.findViewById(R.id.pingfen);
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

        //偏好选择
        pianhao_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"客栈", "美食", "景区"};
                int checkedIndex = 0;
                String mm=pianhao_choose.getText().toString();
                if (mm=="客栈"){
                    checkedIndex = 0;
                }
                if (mm=="美食"){
                    checkedIndex = 1;
                }
                if (mm=="景区"){
                    checkedIndex = 2;
                }
                final QMUIDialog.CheckableDialogBuilder builder= new QMUIDialog.CheckableDialogBuilder(getActivity())
                        .setTitle("请选择偏好分析")
                        .setCheckedIndex(checkedIndex)
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity(), "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                                //dialog.dismiss();

                            }
                        });
                builder.addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                });
                builder.addAction("提交", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //Toast.makeText(getActivity(), builder.getCheckedIndex()+"", Toast.LENGTH_SHORT).show();
                        int index1=builder.getCheckedIndex();
                        pianhao_choose.setText(items[index1]);

                        if (index1==0){
                            initKezhanData();
                            top5_title.setText(items[index1]+"热度前五名");
                            top_name.setText(items[index1]+"");
                        }
                        if (index1==1){
                            initMeishi();
                            top5_title.setText(items[index1]+"热度前五名");
                            top_name.setText(items[index1]+"");
                    }
                        if (index1==2){
                            initJingqu();
                            pingfen.setText("人数");
                            top5_title.setText(items[index1]+"热度前五名");
                            top_name.setText(items[index1]+"");
                        }


                        dialog.dismiss();
                    }
                });

                builder.create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        });

        //雷达图
        mRadarChart = view.findViewById(R.id.xingque_RadarChart);
        initviewmRadarChart();

        pianhao_ConstraintLayout=view.findViewById(R.id.pianhao_ConstraintLayout);
        pianhao_ConstraintLayout.setVisibility(View.GONE);
        mBarChart = view.findViewById(R.id.tingliuTime_chart);
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
        UiSettings mapUiSettings = tencentMap.getUiSettings();
        mapUiSettings.setScaleViewEnabled(false);
        mapUiSettings.setLogoScale(-0.0f);

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
        choose_button1=view.findViewById(R.id.choose_button1);
        choose_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inittwoListPopupIfNeed();
                mListPopup1.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup1.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup1.show(view);
            }
        });

        xingwei_ConstraintLayout=view.findViewById(R.id.xingwei_ConstraintLayout);
        mPicChart = view.findViewById(R.id.chuxing_chart);
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
                    xingwei_ConstraintLayout.setVisibility(View.VISIBLE);
                    pianhao_ConstraintLayout.setVisibility(View.GONE);
                    wudi_lin.setVisibility(View.GONE);
                    wudi_linear.setVisibility(View.GONE);
                }
                if (name=="出行方式"){
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    pianhao_ConstraintLayout.setVisibility(View.GONE);
                    mPicChart.animateY(1400);//设置Y轴动画
                    wudi_lin.setVisibility(View.GONE);
                    wudi_linear.setVisibility(View.VISIBLE);
                }
                if (name=="停留时间"){
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    pianhao_ConstraintLayout.setVisibility(View.GONE);
                    mBarChart.animateY(1400);
                    wudi_lin.setVisibility(View.VISIBLE);
                    wudi_linear.setVisibility(View.GONE);
                }
                if (name=="偏好分析"){
                    xingwei_ConstraintLayout.setVisibility(View.GONE);
                    pianhao_ConstraintLayout.setVisibility(View.VISIBLE);
                    mRadarChart.animateXY(1400,1400);//雷达图动画
                    wudi_lin.setVisibility(View.GONE);
                    wudi_linear.setVisibility(View.GONE);
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
        chuxing_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                //正确设置方式 原因：注意事项有说明
                startDate.set(2017,0,1);
                //endDate.set(2018,11,31);

                TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        //Toast.makeText(getActivity(), getTime(date), Toast.LENGTH_SHORT).show();
                        chuxing_time.setText(getTime(date));
                    }
                })
                        .setType(new boolean[]{true, true, false, false, false, false})// 默认全部显示
                        .setCancelText("取消")//取消按钮文字
                        .setSubmitText("确定")//确认按钮文字
                        .setContentTextSize(18)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
                        .setTitleText("请选择时间")//标题文字
                        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(false)//是否循环滚动
                        .setTitleColor(Color.BLACK)//标题文字颜色
                        .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                        .setCancelColor(Color.BLUE)//取消按钮文字颜色
                        //.setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                        //.setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                        .setRangDate(startDate,endDate)//起始终止年月日设定
                        .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(false)//是否显示为对话框样式
                        .build();
                pvTime.show();
            }
        });
        chuxing_cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time_1=chuxing_time.getText().toString();
                String year_1 = null;
                String month_1 = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    Date date1 = sdf.parse(time_1);
                    year_1=getYear(date1);
                    month_1=getMonth(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                initChuxing(year_1,month_1);
            }
        });
        //获取当前时间和设置时间
        Date date = new Date(System.currentTimeMillis());
        chuxing_time.setText(getTime(date));
        initXianlu("机场","大理古城方向","jichang",1);
        String time=chuxing_time.getText().toString();
        String year = null,month = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date date1 = sdf.parse(time);
            year=getYear(date1);
            month=getMonth(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initChuxing(year,month);
        initTingliu();
        initPiaohao();
        initKezhanData();
        //initMeishi();
        //initJingqu();
        return view;
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }
    //年
    private String getYear(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }
    //月
    private String getMonth(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(date);
    }
    private void init(){
        latLngs1.clear();
        latLngs2.clear();
        latLngs3.clear();
        latLngs4.clear();
        latLngs5.clear();
        polyline1.remove();
        polyline2.remove();
        polyline3.remove();
        polyline4.remove();
        polyline5.remove();
        marker1.remove();
        marker2.remove();
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
                    if (one.equals("机场")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"1","jichang",1);

                    }
                    if (one.equals("火车站")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"2","huochezhan",1);
                    }
                    if (one.equals("高速路口")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"3","gaosuchukou",1);
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

    private void inittwoListPopupIfNeed(){
        if (mListPopup1 == null) {

            String[] listItems = new String[]{
                    "全部方向",
                    "大理古城",
                    "双廊古镇",
                    "喜洲古镇",
                    "环海西路",
                    "环海东路"
            };
            List<String> data1 = new ArrayList<>();

            Collections.addAll(data1, listItems);

            final ArrayAdapter adapter1 = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data1);

            mListPopup1 = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter1);
            mListPopup1.create(QMUIDisplayHelper.dp2px(getContext(), 120), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    two=adapterView.getItemAtPosition(i).toString();
                    String chufadian=choose_button.getText().toString();
                    if (chufadian.equals("机场")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"1","jichang",1);

                    }
                    if (chufadian.equals("火车站")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"2","huochezhan",1);
                    }
                    if (chufadian.equals("高速路口")){
                        tencentMap.clear();
                        init();
                        initXianlu(one,"3","gaosuchukou",1);
                    }
                    mListPopup1.dismiss();
                }
            });
            mListPopup1.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (two!=null){
                        choose_button1.setText(two);
                    }else {
                        choose_button1.setText("大理古城");
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
    private void initChuxing(final String year, final String month) {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        tipDialog.show();
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/trafficbymonth?year="+year+"&month="+month;
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
                                    String traffic_type=jsonObject.getString("traffic_type");
                                    int nums=jsonObject.getInt("nums");
                                    float renshu= nums;
                                    strings.add(new PieEntry(renshu,traffic_type));
                                }
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (null==strings||strings.size()==0){
                                            mPicChart.clear();
                                            mPicChart.setNoDataText("当前的时间没有数据  请重新选择时间");
                                            tipDialog.dismiss();
                                        }else {
                                            setDatamPicChart(strings);
                                            tipDialog.dismiss();
                                            mPicChart.animateY(1400);//设置Y轴动画
                                            chuxing_text.setText(year+"年"+month+"月游客出行方式占比");
                                        }

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

    private void initTingliu() {
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/stay";
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
                                final List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String time=jsonObject.getString("time");
                                    int yVal = Integer.parseInt(time);
                                    yVals.add(new BarEntry(i,yVal));
                                }
                                setDatamBarChart(yVals);
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
    private void initPiaohao() {
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/predilection?type=interest";
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
                                final List<RadarEntry> entries1 = new ArrayList<RadarEntry>();
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String interest_name=jsonObject.getString("interest_name");
                                    String percent=jsonObject.getString("percent");
                                    String ww= percent.substring(0,percent.length() - 1);
                                    float val1 = Float.parseFloat(ww)*2;
                                    entries1.add(new RadarEntry(val1));
                                }
                                setDatamRadarChart(entries1);

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

    private void initXianlu(final String chufa, String jieshu, final String place, final int id) {
        latLngs1 = new ArrayList<LatLng>();
        latLngs2 = new ArrayList<LatLng>();
        latLngs3 = new ArrayList<LatLng>();
        latLngs4 = new ArrayList<LatLng>();
        latLngs5 = new ArrayList<LatLng>();
        /*latLngs.add(new LatLng(25.646788,100.322685));
        latLngs.add(new LatLng(25.583866,100.230932));
        latLngs.add(new LatLng(25.609217,100.221362));
        latLngs.add(new LatLng(25.637812,100.206041));
        latLngs.add(new LatLng(25.704727,100.168877));
        latLngs.add(new LatLng(25.758465,100.139694));
        latLngs.add(new LatLng(25.845784,100.133343));*/

        /*polyline=tencentMap.addPolyline(new PolylineOptions().
                addAll(latLngs).color(0xff00ff00). width(5f));*/

            /*latLngs1.clear();
            latLngs2.clear();
            latLngs3.clear();
            latLngs4.clear();
            latLngs5.clear();
            polyline1.remove();*/
            if (latLngs1.size()!=0){
                latLngs1.clear();
                latLngs2.clear();
                latLngs3.clear();
                latLngs4.clear();
                latLngs5.clear();
            }


        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/locus?startplace="+place;
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
                                final String[] all_nums=new String[num.length()];
                                final String[] all_zhanbi=new String[num.length()];
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String trackpath=jsonObject.getString("trackpath");
                                    String destination=jsonObject.getString("destination");//终点
                                    JSONArray trackpath1=new JSONArray(trackpath);
                                    for (int w=0;w<trackpath1.length();w++){
                                        JSONObject json=trackpath1.getJSONObject(w);
                                        String track_id=json.getString("track_id");
                                        String latitude=json.getString("latitude");
                                        String longitude=json.getString("longitude");


                                        String nums=jsonObject.getString("nums");//总人数
                                        String percent=jsonObject.getString("percent");//总人数
                                        if (destination.equals("大理古城")){
                                            all_nums[0]=nums;
                                            all_zhanbi[0]=percent;
                                        }
                                        if (destination.equals("双廊")){
                                            all_nums[1]=nums;
                                            all_zhanbi[1]=percent;
                                        }
                                        if (destination.equals("喜洲古镇")){
                                            all_nums[2]=nums;
                                            all_zhanbi[2]=percent;
                                        }
                                        if (destination.equals("环海西路")){
                                            all_nums[3]=nums;
                                            all_zhanbi[3]=percent;
                                        }
                                        if (destination.equals("环海东路")){
                                            all_nums[4]=nums;
                                            all_zhanbi[4]=percent;
                                        }


                                        if (track_id.equals("1")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs1.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("2")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs2.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("3")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs3.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("4")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs4.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("13")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs5.add(new LatLng(lat,long1));
                                        }
                                        //2
                                        if (track_id.equals("5")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs1.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("6")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs2.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("7")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs3.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("8")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs4.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("14")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs5.add(new LatLng(lat,long1));
                                        }
                                        //3
                                        if (track_id.equals("9")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs1.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("12")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs2.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("11")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs3.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("10")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs4.add(new LatLng(lat,long1));
                                        }
                                        if (track_id.equals("15")){
                                            double lat= Double.parseDouble(latitude);
                                            double long1= Double.parseDouble(longitude);
                                            latLngs5.add(new LatLng(lat,long1));
                                        }

                                    }
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String chufadian=choose_button.getText().toString();
                                        String mudidi=choose_button1.getText().toString();

                                        //1
                                        if (mudidi.equals("大理古城")){
                                            polyline1=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs1).alpha(1f).color(Color.WHITE). width(20f));
                                        }else {
                                            polyline1=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs1).color(Color.rgb(29, 105, 180)). width(10f));
                                        }
                                        //标注坐标
                                        marker1 = tencentMap.addMarker(new MarkerOptions().
                                                position(latLngs1.get(0)).
                                                title("起点").
                                                snippet(chufadian));
                                        marker2=tencentMap.addMarker(new MarkerOptions()
                                                .position(latLngs1.get(latLngs1.size()-1))
                                                .title("终点").snippet("大理古城"+all_nums[0]+"人\n占比："+all_zhanbi[0]));
                                        //创建图标
                                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));

                                        //2
                                        if (mudidi.equals("双廊古镇")){
                                            polyline2=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs2).color(Color.WHITE). width(20f));
                                        }else {
                                            polyline2=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs2).alpha(0.5f).color(Color.rgb(29, 105, 180)). width(10f));
                                        }

                                        //标注坐标
                                        marker1 = tencentMap.addMarker(new MarkerOptions().
                                                position(latLngs2.get(0)).
                                                title("起点").
                                                snippet(chufadian));
                                        marker2=tencentMap.addMarker(new MarkerOptions()
                                                .position(latLngs2.get(latLngs2.size()-1))
                                                .title("终点").snippet("双廊古镇"+all_nums[1]+"人\n占比："+all_zhanbi[1]));
                                        //创建图标
                                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));


                                        //3
                                        if (mudidi.equals("喜洲古镇")){
                                            polyline3=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs3).color(Color.WHITE). width(20f));
                                        }else {
                                            polyline3=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs3).alpha(0.5f).color(Color.rgb(29, 105, 180)). width(10f));
                                        }

                                        //标注坐标
                                        marker1 = tencentMap.addMarker(new MarkerOptions().
                                                position(latLngs3.get(0)).
                                                title("起点").
                                                snippet(chufadian));
                                        marker2=tencentMap.addMarker(new MarkerOptions()
                                                .position(latLngs3.get(latLngs3.size()-1))
                                                .title("终点").snippet("喜洲古镇"+all_nums[2]+"人\n占比："+all_zhanbi[2]));
                                        //创建图标
                                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));

                                        //4
                                        if (mudidi.equals("环海西路")){
                                            polyline4=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs4).color(Color.WHITE). width(20f));
                                        }else {
                                            polyline4=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs4).alpha(0.5f).color(Color.rgb(29, 105, 180)). width(10f));
                                        }

                                        //标注坐标
                                        marker1 = tencentMap.addMarker(new MarkerOptions().
                                                position(latLngs4.get(0)).
                                                title("起点").
                                                snippet(chufadian));
                                        marker2=tencentMap.addMarker(new MarkerOptions()
                                                .position(latLngs4.get(latLngs4.size()-1))
                                                .title("终点").snippet("环海西路"+all_nums[3]+"人\n占比："+all_zhanbi[3]));
                                        //创建图标
                                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));

                                        //5
                                        if (mudidi.equals("环海东路")){
                                            polyline5=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs5).color(Color.WHITE). width(20f));
                                        }else {
                                            polyline5=tencentMap.addPolyline(new PolylineOptions().
                                                    addAll(latLngs5).alpha(0.5f).color(Color.rgb(29, 105, 180)). width(10f));
                                        }

                                        //标注坐标
                                        marker1 = tencentMap.addMarker(new MarkerOptions().
                                                position(latLngs5.get(0)).
                                                title("起点").
                                                snippet(chufadian));
                                        marker2=tencentMap.addMarker(new MarkerOptions()
                                                .position(latLngs5.get(latLngs5.size()-1))
                                                .title("终点").snippet("环海东路"+all_nums[4]+"人\n占比："+all_zhanbi[4]));
                                        //创建图标
                                        marker1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
                                        marker2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));
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

    private void initKezhanData() {
        //youkedatas=new ArrayList<>();

        /*if (youkedatas.isEmpty()){
            youkedatas.clear();
            YoukelaiyuanEntity model;
            for (int i = 0; i < 5; i++) {
                model=new YoukelaiyuanEntity();
                model.setId(i+1+"");
                model.setProvince("客栈"+i);
                model.setBaifenbi("l4.l4");
                youkedatas.add(model);
            }
        }else {
            YoukelaiyuanEntity model;
            for (int i = 0; i < 5; i++) {
                model=new YoukelaiyuanEntity();
                model.setId(i+1+"");
                model.setProvince("客栈"+i);
                model.setBaifenbi("l4.l4");
                youkedatas.add(model);
            }
        }*/

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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/predilection?type=hotel";
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
                                youkedatas.clear();
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String name=jsonObject.getString("name");
                                    String score=jsonObject.getString("score");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(name);
                                    model.setBaifenbi(score);
                                    youkedatas.add(model);
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
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
    private void initMeishi() {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/predilection?type=food";
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
                                youkedatas.clear();
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String name=jsonObject.getString("name");
                                    double score=jsonObject.getDouble("score");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(name);
                                    model.setBaifenbi(score+"");
                                    youkedatas.add(model);
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
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
    private void initJingqu() {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
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
        String url="http://home.comedali.com:8088/bigdataservice/behavior/predilection?type=scenicspot";
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
                                youkedatas.clear();
                                for (int i=0;i<5;i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String place_name=jsonObject.getString("place_name");
                                    int nums=jsonObject.getInt("nums");
                                    YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                                    model.setId(i+1+"");
                                    model.setProvince(place_name);
                                    model.setBaifenbi(nums+"");
                                    youkedatas.add(model);
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
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

    private void initviewmRadarChart() {
        //setDatamRadarChart();
        mRadarChart.setBackgroundColor(Color.rgb(29, 105, 180));
        mRadarChart.getDescription().setEnabled(false);
        //mRadarChart.setBackgroundColor(Color.rgb(43,189,243));
        mRadarChart.setWebLineWidth(1f);
        mRadarChart.setWebColor(Color.LTGRAY);
        mRadarChart.setWebLineWidthInner(1f);
        mRadarChart.setWebColorInner(Color.LTGRAY);
        mRadarChart.setWebAlpha(100);
        mRadarChart.setNoDataText("正在获取数据...");
        mRadarChart.setNoDataTextColor(Color.WHITE);
        //mRadarChart.animateXY(2400,2400);
        //mChart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        MarkerView mv = new RadarMarkerView(getActivity(), R.layout.radar_markerview);
        mv.setChartView(mRadarChart); // For bounds control
        mRadarChart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = mRadarChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"文化", "客栈", "美食", "旅游线路", "景点"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mRadarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mRadarChart.getLegend();
        l.setEnabled(false);//是否显示
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.WHITE);
    }

    private void setDatamRadarChart(List<RadarEntry> entries1) {

        //ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        //ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        /*for (int i = 0; i < 5; i++) {
            float val1 = (float) (Math.random() * 80) + 20;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
        }*/

        RadarDataSet set1 = new RadarDataSet(entries1, "兴趣前五名");
        set1.setColor(Color.rgb(255, 255, 255));
        set1.setFillColor(Color.rgb(255, 255, 250));
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

        mRadarChart.setData(data);
        mRadarChart.invalidate();
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
        //mPicChart.animateY(1400);//设置Y轴动画
        //picChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//颜色数值
        //取消颜色数值
        Legend l = mPicChart.getLegend();
        l.setEnabled(true);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);*/
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setFormSize(10);//设置图例的大小
        l.setDrawInside(false);
        l.setTextColor(Color.WHITE);

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
        strings.add(new PieEntry(30f,"火车"));
        strings.add(new PieEntry(170f,"飞机"));
        strings.add(new PieEntry(60f,"自驾"));
        strings.add(new PieEntry(50f,"客车"));*/
        PieDataSet dataSet = new PieDataSet(strings,"出行方式");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.age5));
        colors.add(getResources().getColor(R.color.wudi));
        colors.add(getResources().getColor(R.color.age4));
        colors.add(getResources().getColor(R.color.age6));
        colors.add(getResources().getColor(R.color.app_color_theme_4));
        colors.add(getResources().getColor(R.color.app_color_theme_3));
        colors.add(getResources().getColor(R.color.app_color_theme_9));
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());//转化百分比
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.DKGRAY);
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
        //setDatamBarChart();
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
       //mBarChart.animateY(1400);
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
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
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
        leftYAxis.setTextColor(Color.WHITE);
        leftYAxis.setAxisLineColor(Color.WHITE);
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
        l.setTextColor(Color.WHITE);
        //自定义markView,点击显示更多信息
        /*MyMarkView markerView = new MyMarkView(getActivity(),R.layout.custom_marker_view);
        markerView.setChartView(mBarChart);
        mBarChart.setMarker(markerView);*/


        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.d("mm", String.valueOf(e.getX()));
                //Log.d("ww", String.valueOf(e.getY()));
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setDatamBarChart(List<BarEntry> yVals) {

        //每一个柱状图的数据
        /*List<BarEntry> yVals = new ArrayList<>();//Y轴方向第一组数组

        for (int i = 0; i < 5; i++) {//添加数据源
            //yVals.add(new BarEntry(i,(float) Math.random()*520 + 1));
            int yVal = (int) (Math.random()*520 + 1);
            yVals.add(new BarEntry(i,yVal));
        }*/
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
        dataSet.setValueTextColor(Color.WHITE);
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
