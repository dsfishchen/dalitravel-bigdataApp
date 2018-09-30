package com.comedali.bigdata.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Quyu_renliuActivity;
import com.comedali.bigdata.entity.MessageEvent;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.comedali.bigdata.utils.MyMarkView;
import com.comedali.bigdata.utils.NetworkUtil;
import com.comedali.bigdata.utils.NianMarkView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
 * Created by 刘杨刚 on 2018/9/6.
 */
public class AnniantoujiFragment extends Fragment {
    private BarChart mChart;
    private Button quyu_choose;
    private Button time_choose;
    private Button anri_chaxun;
    private QMUIListPopup mListPopup;
    private String one;
    private String two;
    private String year;
    private String month;
    private String day;
    private OkHttpClient client;
    private String dizhi;
    private String[] listItems;
    private String[] listPlace_id;
    private String dizhi_m=null;
    private String id=null;
    private String name=null;
    private String quyu_1;
    private String time_1;
    private String Place_id;
    private TextView zhushi_nian;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.annian_touji_er,container,false);
        dizhi=getActivity().getIntent().getStringExtra("dizhi");
        initChoose();
        mChart=view.findViewById(R.id.nian_lineChart);
        quyu_choose=view.findViewById(R.id.quyu_choose3);
        anri_chaxun=view.findViewById(R.id.anri_chaxun3);
        time_choose=view.findViewById(R.id.time_choose3);
        zhushi_nian=view.findViewById(R.id.zhushi_nian);
        quyu_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });
        quyu_choose.setText(name);
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        time_choose.setText(getTime(date));
        time_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();


                //正确设置方式 原因：注意事项有说明
                startDate.set(2016,0,1);
                //endDate.set(2018,11,31);

                TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        //Toast.makeText(getActivity(), getTime(date), Toast.LENGTH_SHORT).show();
                        time_choose.setText(getTime(date));
                    }
                })
                        .setType(new boolean[]{true, false, false, false, false, false})// 默认全部显示
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
        quyu_1=quyu_choose.getText().toString();
        time_1=time_choose.getText().toString();
        initNian(id,quyu_1,time_1);
        anri_chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quyu=quyu_choose.getText().toString();
                String time=time_choose.getText().toString();
                /*String newmonth = null;
                String newday=null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    Date date = sdf.parse(time);
                    year=getYear(date);
                    month=getMonth(date);
                    newmonth = month.replaceAll("^(0+)", "");
                    day=getDay(date);
                    newday = day.replaceAll("^(0+)", "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String times=year+"年";*/
                //Log.d("time", quyu+time);
                mChart.invalidate();
                mChart.animateX(1400);
                mChart.notifyDataSetChanged();
                if (Place_id==null){
                    Place_id="3";
                }
                initNian(Place_id,quyu,time);
            }
        });
        initview();
        return view;
    }
    private void initChoose(){
        if (dizhi.equals("大理州")){
            dizhi_m="dali";
            id="3";
            name="大理古城";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("大理市")){
            dizhi_m="dali";
            id="3";
            name="大理古城";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("洱源县")){
            dizhi_m="eryuan";
            id="32";
            name="洱源高速路口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("宾川县")){
            dizhi_m="binchuan";
            id="15";
            name="鸡足山";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("弥渡县")){
            dizhi_m="midu";
            id="43";
            name="弥渡入城口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("永平县")){
            dizhi_m="yongping";
            id="42";
            name="平高速路口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("南涧县")){
            dizhi_m="nanjian";
            id="41";
            name="南涧入城口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("巍山县")){
            dizhi_m="weishan";
            id="36";
            name="巍宝山";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("鹤庆县")){
            dizhi_m="heqing";
            id="44";
            name="新华村";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("云龙县")){
            dizhi_m="yunlong";
            id="39";
            name="云龙入城口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("剑川县")){
            dizhi_m="jianchuan";
            id="47";
            name="沙溪古镇";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("祥云县")){
            dizhi_m="xiangyun";
            id="27";
            name="云高速路口";
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("漾濞县")){
            dizhi_m="yangbi";
            id="40";
            name="漾濞高速路口";
            initDizhiChoose(dizhi_m);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        quyu_choose.setText(messageEvent.getMessage());
    }

    private void initDizhiChoose(String dizhi_m){
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache6");
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
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/spotnum?city="+dizhi_m;
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
                        Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
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
                                JSONArray num = new JSONArray(result);
                                //String[] listItems = new String[num.length()];
                                listItems = new String[num.length()];
                                listPlace_id=new String[num.length()];
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String place_name=jsonObject.getString("place_name");
                                    String place_id=jsonObject.getString("place_id");
                                    listItems[i]=place_name;
                                    listPlace_id[i]=place_id;
                                }
                                Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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
    private void initNian(String id,final String quyu,final String NIAN) {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache4");
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

        //final String NIAN=time_choose.getText().toString();
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/statistics?type=year&place_id="+id+"&year="+NIAN+"&month=00&day=00";
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
                        Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
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
                                JSONArray num = new JSONArray(result);
                                final List<BarEntry> entries = new ArrayList<BarEntry>();
                                int sums=0;
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    String c_nums=jsonObject.getString("c_nums");
                                    String c_month=jsonObject.getString("c_month");
                                    int yVal = Integer.parseInt(c_nums);
                                    int m=Integer.parseInt(c_month);
                                    entries.add(new BarEntry(m, yVal));
                                    sums+=yVal;
                                }
                                final int finalSums = sums;
                                Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (null==entries||entries.size()==0){
                                            mChart.clear();
                                            mChart.setNoDataText("当前选择的时间没有该区域数据  请重新选择时间");
                                            tipDialog.dismiss();
                                        }else {
                                            initdata3(entries,quyu,NIAN,finalSums);
                                            tipDialog.dismiss();
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

    //年月日
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
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
    //日
    private String getDay(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(date);
    }
    private void initoneListPopupIfNeed() {
        if (mListPopup == null) {
            if (listItems==null){
                listItems=new String[]{
                        "获取失败"
                };
            }
            /*String[] listItems = new String[]{
                    "蝴蝶泉",
                    "感通索道",
                    "崇圣寺三塔",
                    "南诏风情岛",
                    "天龙八部影视城",
                    "鸡足山",
                    "洗马潭大索道",
                    "巍宝山",
                    "新华村",
                    "石宝山",
                    "沙溪古镇",
                    "海舌公园"
            };*/
            List<String> data = new ArrayList<>();

            Collections.addAll(data, listItems);

            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data);

            mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            final QMUIListPopup finalMListPopup = mListPopup;
            mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 100), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getActivity(), "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                    one=adapterView.getItemAtPosition(i).toString();
                    quyu_choose.setText(one);
                    if (one.equals(listItems[i])){
                        Place_id=listPlace_id[i];
                    }
                    finalMListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //mActionButton2.setText(getContext().getResources().getString(R.string.popup_list_action_button_text_show));
                    if (one!=null){
                        quyu_choose.setText(one);
                    }else {
                        quyu_choose.setText(name);
                    }
                }
            });
        }
    }
    private void initview() {
        //initdata3(12,quyu_1,time_1);
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
        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        //自定义markView,点击显示更多信息
        NianMarkView markerView = new NianMarkView(getActivity(),R.layout.custom_marker_view);
        markerView.setChartView(mChart);
        mChart.setMarker(markerView);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.rgb(255,255,255));
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setLabelCount(13);
        xAxis.setLabelRotationAngle(-20);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int m=(int)value;
                if (m==0){
                    return "";
                }else {
                    return m+"月";
                }
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setInverted(true);//Y轴倒序
        xAxis.setAvoidFirstLastClipping(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.rgb(255,255,255));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setTextColor(Color.rgb(255,255,255));

        //设置限制线 12代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(2500000);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("超过250万人");
        //设置限制线为虚线
        //limitLine.enableDashedLine(10f, 10f, 0f);
        //左边Y轴添加限制线
        leftAxis.addLimitLine(limitLine);

    }

    private void initdata3(List<BarEntry> entries, String quyu, String time,int sums) {
        /*List<Entry> entries = new ArrayList<Entry>();

        for (int i = 1; i < count+1; i++) {
            //float xVal = (float) (Math.random() * 20);
            int yVal = (int) (Math.random() * 2500);

            entries.add(new Entry(i, yVal));
        }*/
        // sort by x-value
        // create a dataset and give it a type
        //时间转换设置
        String newmonth = null;
        String newday=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date date = sdf.parse(time);
            year=getYear(date);
            month=getMonth(date);
            newmonth = month.replaceAll("^(0+)", "");
            day=getDay(date);
            newday = day.replaceAll("^(0+)", "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String times=year+"年";


        BarDataSet dataSet = new BarDataSet(entries, times+quyu+"客流量年统计 单位：人");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12);//修改一组柱状图的文字大小
        dataSet.setValueTextColor(Color.rgb(255,255,255));
        //set1.setDrawValues(false);//不显示点数值
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int mm=(int)entry.getY();
                return String.valueOf(mm)+"人";
            }
        });
        // create a data object with the datasets
        BarData data2 = new BarData(dataSet);
        // set data
        mChart.setData(data2);
        mChart.invalidate();//重绘图表
        String html=year+"年"+quyu+"总客流量约<font color='#ff0000'><big>"+sums+"</big></font>人";
        zhushi_nian.setText(Html.fromHtml(html));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
