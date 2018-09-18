package com.comedali.bigdata.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.comedali.bigdata.R;
import com.comedali.bigdata.utils.MyMarkView;
import com.comedali.bigdata.utils.NetworkUtil;
import com.comedali.bigdata.utils.RiMarkView;
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
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

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
public class AnritoujiFragment extends Fragment{
    private LineChart mChart;
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
    private List<Entry> entries;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.anri_touji_er,container,false);
        mChart=view.findViewById(R.id.ri_lineChart);
        quyu_choose=view.findViewById(R.id.quyu_choose1);
        anri_chaxun=view.findViewById(R.id.anri_chaxun1);
        time_choose=view.findViewById(R.id.time_choose1);
        quyu_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });

        Date now = new Date(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(calendar.DATE, -1);
        final Date date = calendar.getTime();
        time_choose.setText(getTime(date));//设置当前时间

        time_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar selectedDate = Calendar.getInstance();
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();


                //正确设置方式 原因：注意事项有说明
                startDate.set(2016,10,1);
                int y= Integer.parseInt(getYear(date));
                int m= Integer.parseInt(getMonth(date));
                int d= Integer.parseInt(getDay(date));
                endDate.set(y,m-1,d);

                TimePickerView  pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date,View v) {//选中事件回调
                        //Toast.makeText(getActivity(), getTime(date), Toast.LENGTH_SHORT).show();
                        time_choose.setText(getTime(date));
                    }
                })
                        .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
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
                        .setDate(calendar)// 如果不设置的话，默认是系统时间*/
                        .setRangDate(startDate,endDate)//起始终止年月日设定
                        .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(false)//是否显示为对话框样式
                        .build();
                pvTime.show();
            }
        });
        String quyu_1=quyu_choose.getText().toString();
        final String time_1=time_choose.getText().toString();
        initRi(quyu_1,time_1,"2018","06",time_1);
        anri_chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quyu=quyu_choose.getText().toString();
                String time=time_choose.getText().toString();
               /* String newmonth = null;
                String newday=null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(time);
                    year=getYear(date);
                    month=getMonth(date);
                    newmonth = month.replaceAll("^(0+)", "");
                    day=getDay(date);
                    newday = day.replaceAll("^(0+)", "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String times=year+"年"+newmonth+"月"+newday+"日";*/
                //Log.d("time", quyu+time);
                mChart.invalidate();
                mChart.animateX(1400);
                mChart.notifyDataSetChanged();
                initRi(quyu,time,"2018","06",time);

            }
        });
        initview();
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
            if (!NetworkUtil.checkNet(getActivity())) {
                int offlineCacheTime = 60*60*24*7;//离线的时候的缓存的过期时间
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                        .build();
            }
            return chain.proceed(request);
        }
    };
    private void initRi(final String quyu, final String time_1, String NIAN, String Yue,String Ri) {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();
        tipDialog.show();

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache5");
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

        String id="3";
        //final String NIAN=time_choose.getText().toString();
        String url="http://192.168.190.119:8080/flowmeter/statistics?type=day&place_id="+id+"&year="+NIAN+"&month="+Yue+"&day="+Ri;
        final Request request = new Request.Builder()
                .url(url)
                .build();
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
                        JSONArray num = new JSONArray(result);
                        //final List<Entry> entries = new ArrayList<Entry>();
                        entries = new ArrayList<Entry>();
                        for (int i=0;i<num.length();i++){
                            JSONObject jsonObject=num.getJSONObject(i);
                            String c_nums=jsonObject.getString("c_nums");

                            String c_hour=jsonObject.getString("c_hour");

                            int yVal = Integer.parseInt(c_nums);
                            int m=Integer.parseInt(c_hour);
                            entries.add(new Entry(m, yVal));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //String quyu_1=quyu_choose.getText().toString();
                                initdata1(entries,quyu,time_1);
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

    //年月日
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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

            String[] listItems = new String[]{
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
            };
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
                        quyu_choose.setText("大理古城");
                    }
                }
            });
        }
    }

    private void initview() {
        //initdata1(24,quyu_1,time_1);//设置图表数据
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
        mChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//颜色数值
        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        //自定义markView,点击显示更多信息
        RiMarkView markerView = new RiMarkView(getActivity(),R.layout.custom_marker_view);
        markerView.setChartView(mChart);
        mChart.setMarker(markerView);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setTextColor(Color.rgb(255,255,255));
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
        leftAxis.setTextColor(Color.rgb(255,255,255));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setTextColor(Color.rgb(255,255,255));

        //设置限制线 12代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(15000);
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

    private void initdata1(List<Entry> entries,String quyu,String time) {
        /*ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int i = 1; i < count+1; i++) {
            //float xVal = (float) (Math.random() * 20);
            int yVal = (int) (Math.random() * 2500);

            entries.add(new Entry(i, yVal));
        }*/
        // sort by x-value
        Collections.sort(entries, new EntryXComparator());
        // create a dataset and give it a type

        //时间转换
        String newmonth = null;
        String newday=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            year=getYear(date);
            month=getMonth(date);
            newmonth = month.replaceAll("^(0+)", "");
            day=getDay(date);
            newday = day.replaceAll("^(0+)", "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String times=year+"年"+newmonth+"月"+newday+"日";

        LineDataSet set1 = new LineDataSet(entries, times+quyu+"客流量日统计 单位：人");
        set1.setLineWidth(1.5f);
        set1.setCircleRadius(4f);
        set1.setValueTextSize(12);
        set1.setDrawCircleHole(false);//设置是否在数据点中间显示一个孔
        set1.setValueTextColor(Color.rgb(255,255,255));
        set1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int mm=(int)entry.getY();
                return String.valueOf(mm)+"人";
            }
        });
        // create a data object with the datasets
        LineData data = new LineData(set1);
        // set data
        mChart.setData(data);
    }
}
