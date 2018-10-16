package com.comedali.bigdata.fragment;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Quyu_renliuActivity;
import com.comedali.bigdata.activity.ShipingActivity;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.utils.Dialog_Util;
import com.comedali.bigdata.utils.Jingwei_Util;
import com.comedali.bigdata.utils.NetworkUtil;
import com.github.mikephil.charting.data.Entry;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapOptions;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.HeatDataNode;
import com.tencent.tencentmap.mapsdk.maps.model.HeatOverlay;
import com.tencent.tencentmap.mapsdk.maps.model.HeatOverlayOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.tencentmap.mapsdk.maps.TencentMap.MAP_TYPE_NIGHT;

/**
 * Created by 刘杨刚 on 2018/9/3.
 */
public class ShouyeFragment extends Fragment {
    private TextureMapView mMapView;
    private HeatOverlay heatOverlay;
    private TencentMap tencentMap;
    private OkHttpClient client;
    private TextView shouye_tianqi;
    private Button nice_button1;
    private ImageButton nice_button2;
    private QMUIListPopup mListPopup;
    private QMUIListPopup mListPopup1;
    private ImageView tianqi_imageView;
    private String one;
    private String two;
    private TextView renshu1;
    private TextView renshu2;
    private TextView renshu3;
    private TextView renshu4;
    private TextView renshu5;
    private TextView renshu6;
    private String dizhi;
    private Timer timer;
    private CheckBox heat_choose;
    private TextView true_false;
    private List<Marker> mm;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.shouye_yi,container,false);
        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());//状态栏字体颜色--黑色
        mMapView = view.findViewById(R.id.map);
        tianqi_imageView=view.findViewById(R.id.tianqi_imageView);
        shouye_tianqi=view.findViewById(R.id.shouye_tianqi);
        nice_button1=view.findViewById(R.id.nice_button1);
        heat_choose=view.findViewById(R.id.heat_choose);
        true_false=view.findViewById(R.id.true_false);
        nice_button2=view.findViewById(R.id.nice_button2);
        nice_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });
        nice_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inittwoListPopupIfNeed();
                mListPopup1.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup1.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup1.show(view);
            }
        });

        tencentMap = mMapView.getMap();
        //tencentMap.setMapStyle(MAP_TYPE_NIGHT);//样式
        //tencentMap.setOnTapMapViewInfoWindowHidden(true);//点击其他地方让气泡信息消失
        tencentMap.enableMultipleInfowindow(true);
        UiSettings mapUiSettings = tencentMap.getUiSettings();
        mapUiSettings.setScaleViewEnabled(false);
        mapUiSettings.setLogoScale(-0.0f);
        renshu1=view.findViewById(R.id.renshu_1);
        renshu2=view.findViewById(R.id.renshu_2);
        renshu3=view.findViewById(R.id.renshu_3);
        renshu4=view.findViewById(R.id.renshu_4);
        renshu5=view.findViewById(R.id.renshu_5);
        renshu6=view.findViewById(R.id.renshu_6);

        dizhi=nice_button1.getText().toString();
        //设定中心点坐标
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(25.990144,100.148621), //新的中心点坐标
                        8f,  //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f)); //偏航角 0~360° (正北方为0)
        //tencentMap.animateCamera(cameraSigma);//改变地图状态
        tencentMap.moveCamera(cameraSigma);//移动地图

        //加载热力图
        //initHeatMapOverlay("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");
        initdata("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");//标注点
        initmm();
        initrenshu();
        heat_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    heat_choose.setTextColor(Color.rgb(43,189,243));
                    true_false.setText("true");
                    tencentMap.clear();
                    initHeatMapOverlay("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");
                }else {
                    heat_choose.setTextColor(Color.rgb(255,255,255));
                    true_false.setText("false");
                    heatOverlay.remove();
                    initdata("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");//标注点
                }
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
            if (getActivity()==null){

            }else {
                if (!NetworkUtil.checkNet(getActivity())) {
                    int offlineCacheTime = 60*60*24*7;//离线的时候的缓存的过期时间
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                            .build();
                }
            }

            return chain.proceed(request);
        }
    };
    private void initrenshu() {
        final OkHttpClient client1=new OkHttpClient();

        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/num?city=all";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //每60秒获取实时人数
        new Timer().schedule(new TimerTask(){

            @Override
            public void run() {
                client1.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("数据请求", "失败");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            String str = response.body().string();
                            Log.d("数据请求", "成功"+str);
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                final String result = jsonData.getString("result");
                                //Log.d("result", result);
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String wudi=nice_button1.getText().toString();
                                        String renshu=result;
                                        int m=Integer.parseInt(renshu);
                                        if (wudi.equals("大理州")){
                                            if (renshu.length()==6){
                                                renshu1.setText(renshu.charAt(0)+"");
                                                renshu2.setText(renshu.charAt(1)+"");
                                                renshu3.setText(renshu.charAt(2)+"");
                                                renshu4.setText(renshu.charAt(3)+"");
                                                renshu5.setText(renshu.charAt(4)+"");
                                                renshu6.setText(renshu.charAt(5)+"");
                                            }
                                            if (renshu.length()==5){
                                                renshu1.setText("0");
                                                renshu2.setText(renshu.charAt(0)+"");
                                                renshu3.setText(renshu.charAt(1)+"");
                                                renshu4.setText(renshu.charAt(2)+"");
                                                renshu5.setText(renshu.charAt(3)+"");
                                                renshu6.setText(renshu.charAt(4)+"");
                                            }
                                            if (renshu.length()==4){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText(renshu.charAt(0)+"");
                                                renshu4.setText(renshu.charAt(1)+"");
                                                renshu5.setText(renshu.charAt(2)+"");
                                                renshu6.setText(renshu.charAt(3)+"");
                                            }
                                            if (renshu.length()==3){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText(renshu.charAt(0)+"");
                                                renshu5.setText(renshu.charAt(1)+"");
                                                renshu6.setText(renshu.charAt(2)+"");
                                            }
                                            if (renshu.length()==2){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText(renshu.charAt(0)+"");
                                                renshu6.setText(renshu.charAt(1)+"");
                                            }
                                            if (renshu.length()==1){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText("0");
                                                renshu6.setText(renshu.charAt(0)+"");
                                            }
                                            if (renshu.length()==0){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText("0");
                                                renshu6.setText("0");
                                            }
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
        },0,20000);

    }
    private void initrenshu1(final String city) {
        final OkHttpClient client1=new OkHttpClient();
        //setup cache
        /*File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        client=mBuilder
                .addNetworkInterceptor(NetCacheInterceptor)
                .addInterceptor(OfflineCacheInterceptor)
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();*/
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/scale";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client1.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("数据请求", "失败");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        try {
                            String str = response.body().string();
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                final String result = jsonData.getString("result");
                                final JSONArray num = new JSONArray(result);
                                int m = 0;
                                for (int i=0;i<num.length();i++){
                                    JSONObject jsonObject=num.getJSONObject(i);
                                    int nums=jsonObject.getInt("nums");
                                    String area_type=jsonObject.getString("area_type");
                                    if (area_type.equals(city)){
                                        m=nums;
                                    }
                                }
                                final String renshu= String.valueOf(m);
                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String wudi=nice_button1.getText().toString();
                                        Log.d("数据请求", "成功"+renshu);
                                            if (renshu.length()==6){
                                                renshu1.setText(renshu.charAt(0)+"");
                                                renshu2.setText(renshu.charAt(1)+"");
                                                renshu3.setText(renshu.charAt(2)+"");
                                                renshu4.setText(renshu.charAt(3)+"");
                                                renshu5.setText(renshu.charAt(4)+"");
                                                renshu6.setText(renshu.charAt(5)+"");
                                            }
                                            if (renshu.length()==5){
                                                renshu1.setText("0");
                                                renshu2.setText(renshu.charAt(0)+"");
                                                renshu3.setText(renshu.charAt(1)+"");
                                                renshu4.setText(renshu.charAt(2)+"");
                                                renshu5.setText(renshu.charAt(3)+"");
                                                renshu6.setText(renshu.charAt(4)+"");
                                            }
                                            if (renshu.length()==4){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText(renshu.charAt(0)+"");
                                                renshu4.setText(renshu.charAt(1)+"");
                                                renshu5.setText(renshu.charAt(2)+"");
                                                renshu6.setText(renshu.charAt(3)+"");
                                            }
                                            if (renshu.length()==3){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText(renshu.charAt(0)+"");
                                                renshu5.setText(renshu.charAt(1)+"");
                                                renshu6.setText(renshu.charAt(2)+"");
                                            }
                                            if (renshu.length()==2){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText(renshu.charAt(0)+"");
                                                renshu6.setText(renshu.charAt(1)+"");
                                            }
                                            if (renshu.length()==1){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText("0");
                                                renshu6.setText(renshu.charAt(0)+"");
                                            }
                                            if (renshu.length()==0){
                                                renshu1.setText("0");
                                                renshu2.setText("0");
                                                renshu3.setText("0");
                                                renshu4.setText("0");
                                                renshu5.setText("0");
                                                renshu6.setText("0");
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
    private void initdata(String dizhi_m) {
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
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/spotnum?city="+dizhi_m;
        final Request request = new Request.Builder()
                .url(url)
                .build();
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
                        JSONArray num = new JSONArray(result);
                        final List<List<Entry>> entrie = new ArrayList<>();

                        final List<LatLng> jingwei=new ArrayList<>();
                        final String[] renshu=new String[num.length()];
                        final String[] dizhi_name=new String[num.length()];
                        final String[] place_id1=new String[num.length()];
                        final String[] place_type1=new String[num.length()];
                        for (int i=0;i<num.length();i++){
                            JSONObject jsonObject=num.getJSONObject(i);
                            String place_name=jsonObject.getString("place_name");
                            String latitude=jsonObject.getString("latitude");
                            String longitude=jsonObject.getString("longitude");
                            String place_type=jsonObject.getString("place_type");
                            String place_id=jsonObject.getString("place_id");
                            int nums=jsonObject.getInt("nums");
                            double lat= Double.parseDouble(latitude);
                            double long1= Double.parseDouble(longitude);


                            //**

                            double x_pi=3.14159265358979324;
                            double x = long1 - 0.0065, y = lat - 0.006;
                            double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
                            double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
                            double tx_lon = z * Math.cos(theta);
                            double tx_lat = z * Math.sin(theta);


                            //**

                            jingwei.add(new LatLng(tx_lat,tx_lon));
                            String renshu_nums= String.valueOf(nums);
                            renshu[i]=renshu_nums;
                            dizhi_name[i]=place_name;
                            place_type1[i]=place_type;
                            place_id1[i]=place_id;

                            List<Entry> entries = new ArrayList<Entry>();
                            String hourtouristlist=jsonObject.getString("hourtouristlist");
                            JSONArray result1 = new JSONArray(hourtouristlist);
                            for (int w=0;w<result1.length();w++){
                                JSONObject jsonObject1=result1.getJSONObject(w);
                                String c_hour=jsonObject1.getString("c_hour");
                                String c_nums=jsonObject1.getString("c_nums");
                                int c_hour1= Integer.parseInt(c_hour);
                                int c_nums1= Integer.parseInt(c_nums);
                                entries.add(new Entry(c_hour1, c_nums1));
                            }
                            entrie.add(entries);
                        }
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mm=new ArrayList<>();
                                if (null == mm || mm.size() ==0 ){
                                    //为空的情况
                                }else {
                                    mm.clear();
                                }
                                    for (int i=0;i<jingwei.size();i++){
                                        //标注坐标

                                        mm.add(tencentMap.addMarker(new MarkerOptions()
                                                .alpha(0.5f)
                                                .position(jingwei.get(i))
                                                .title(dizhi_name[i])));
                                        mm.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.red));
                                        mm.get(i).showInfoWindow();
                                    }
                                //创建图标
                                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
                               tencentMap.setOnMarkerClickListener(new TencentMap.OnMarkerClickListener() {
                                   @Override
                                   public boolean onMarkerClick(Marker marker) {
                                       String ren_nums = null;
                                       List<Entry> wodetian=new ArrayList<>();
                                       String dizhi_id=null;
                                       String dizhi_type=null;
                                       for (int i=0;i<jingwei.size();i++){
                                           if (marker.getTitle().equals(dizhi_name[i])){
                                               if (renshu[i].equals("0")){
                                                   ren_nums=renshu[i]+"";
                                                   wodetian.addAll(entrie.get(i));//设备离线
                                                   dizhi_id=place_id1[i];
                                                   dizhi_type=place_type1[i];
                                               }else {
                                                   ren_nums=renshu[i]+"";
                                                   wodetian.addAll(entrie.get(i));//当前点的人数统计图数据
                                                   dizhi_id=place_id1[i];
                                                   dizhi_type=place_type1[i];
                                               }
                                           }
                                       }

                                       /*new QMUIDialog.MessageDialogBuilder(getActivity())
                                               .setTitle(marker.getTitle())
                                               .setMessage(ren_nums)
                                               .addAction("取消", new QMUIDialogAction.ActionListener() {
                                                   @Override
                                                   public void onClick(QMUIDialog dialog, int index) {
                                                       dialog.dismiss();
                                                   }
                                               })
                                               .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();*/
                                       inityouke(dizhi_id,dizhi_type,wodetian,marker);
                                       /*String html;
                                       if (ren_nums.equals("0")){
                                            html="游客累积：<font color='#ff0000'>"+ren_nums+"</font>人   (设备离线)";
                                       }else {
                                            html="游客累积：<font color='#ff0000'>"+ren_nums+"</font>人";
                                       }*/
                                       /*final Dialog_Util dialog=new Dialog_Util(getActivity());
                                       dialog.setEntries(wodetian);
                                                dialog.setTitle(marker.getTitle())
                                                .setMessage(Html.fromHtml(html))
                                                .setOnClickBottomListener(new Dialog_Util.OnClickBottomListener() {
                                                    @Override
                                                    public void onNegtiveClick() {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();*/


                                       return true;
                                   }
                               });


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

    private void inityouke(final String dizhi_id, final String dizhi_type, final List<Entry> wodetian, final Marker marker) {
        final OkHttpClient client1=new OkHttpClient();
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        tipDialog.show();
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/ecdemicnum?place_id="+dizhi_id+"&place_type="+dizhi_type;
        final Request request = new Request.Builder()
                .url(url)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client1.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"请检查您的网络是否开启",Toast.LENGTH_LONG).show();
                                tipDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                        String str = response.body().string();
                        final JSONObject jsonData = new JSONObject(str);
                        final String result = jsonData.getString("result");
                        final String html;
                        if (result.equals("0")) {
                            html ="游客累计：<font color='#ff0000'>"+result+"</font>人   (设备离线)";
                        }else {
                            html ="游客累计：<font color='#ff0000'>"+result+"</font>人";
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Dialog_Util dialog=new Dialog_Util(getActivity());
                                dialog.setEntries(wodetian);
                                dialog.setTitle(marker.getTitle())
                                        .setMessage(Html.fromHtml(html))
                                        .setOnClickBottomListener(new Dialog_Util.OnClickBottomListener() {
                                            @Override
                                            public void onNegtiveClick() {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                tipDialog.dismiss();
                            }
                        });
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

    private void initoneListPopupIfNeed() {
        final boolean m = heat_choose.isChecked();
        if (mListPopup == null) {

            String[] listItems = new String[]{
                    "大理州",
                    "大理市",
                    "洱源县",
                    "宾川县",
                    "永平县",
                    "南涧县",
                    "巍山县",
                    "鹤庆县",
                    "云龙县",
                    "剑川县",
                    "祥云县",
                    "漾濞县",
                    "弥渡县"
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
                    if (one=="大理州"){
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.990144,100.148621), //新的中心点坐标
                                        8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        dizhi="大理市";
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");//标注点
                        }else {
                            //数据热力获取
                            tencentMap.clear();
                            initHeatMapOverlay("dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu");
                        }
                        initrenshu();
                    }
                    if (one=="大理市"){
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.781344,100.207672), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        dizhi="大理市";
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();

                            initdata("dali");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("dali");
                        }
                        initrenshu1("dali");

                    }
                    if (one=="洱源县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(26.080221,100.000992), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        dizhi="洱源县";
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("eryuan");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("eryuan");
                        }
                        initrenshu1("eryuan");
                    }
                    if (one=="宾川县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.901233,100.509796), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        dizhi="宾川县";
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("binchuan");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("binchuan");
                        }
                        initrenshu1("binchuan");
                    }
                    if (one=="永平县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.460867,99.528322), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        dizhi="永平县";
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("yongping");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("yongping");
                        }
                        initrenshu1("yongping");
                    }
                    if (one=="南涧县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.040815,100.518551), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="南涧县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("nanjian");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("nanjian");
                        }
                        initrenshu1("nanjian");
                    }
                    if (one=="巍山县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.233200,100.309660), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="巍山县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("weishan");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("weishan");
                        }
                        initrenshu1("weishan");
                    }
                    if (one=="鹤庆县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(26.560234,100.176498), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="鹤庆县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("heqing");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("heqing");
                        }
                        initrenshu1("heqing");
                    }
                    if (one=="云龙县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.885596,99.371121), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="云龙县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("yunlong");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("yunlong");
                        }
                        initrenshu1("yunlong");
                    }
                    if (one=="剑川县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(26.537033,99.905559), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="剑川县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("jianchuan");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("jianchuan");
                        }
                        initrenshu1("jianchuan");
                    }
                    if (one=="祥云县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.483847,100.550947), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="祥云县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("xiangyun");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("xiangyun");
                        }
                        initrenshu1("xiangyun");
                    }
                    if (one=="漾濞县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.669920,99.958140), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="漾濞县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("yangbi");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("yangbi");
                        }
                        initrenshu1("yangbi");
                    }
                    if (one=="弥渡县"){
                        //设定中心点坐标
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(25.343802,100.490991), //新的中心点坐标
                                        10.8f,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        dizhi="弥渡县";
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        if (true_false.getText().toString().equals("false")){
                            tencentMap.clear();
                            initdata("midu");
                        }else {
                            tencentMap.clear();
                            //数据获取
                            initHeatMapOverlay("midu");
                        }
                        initrenshu1("midu");
                    }
                    mListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (one!=null){
                        nice_button1.setText(one);
                    }else {
                        nice_button1.setText("大理州");
                    }

                }
            });
        }
    }
    private void initmm() {
        //setup cache
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        client=mBuilder
                .addNetworkInterceptor(NetCacheInterceptor)
                .addInterceptor(OfflineCacheInterceptor)
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        String url="https://api.seniverse.com/v3/weather/now.json?key=q00e27l9lrdc59mq&location=ip&language=zh-Hans&unit=c";
        final Request request = new Request.Builder()
                .url(url)
                .build();

            //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"请检查您的网络是否开启",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            Log.d("数据请求", "成功"+str);
                            //Log.d("缓存", String.valueOf(response.cacheResponse()));
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("results");
                            JSONArray jsonData1 = new JSONArray(resultStr);
                            JSONObject location=jsonData1.getJSONObject(0);
                            Log.d("name", String.valueOf(location));
                            String location1 = location.getString("location");//
                            Log.d("name", String.valueOf(location1));
                            String now = location.getString("now");//
                            JSONObject now1=new JSONObject(now);
                            final String temperature=now1.getString("temperature");
                            final String text=now1.getString("text");
                            final String code=now1.getString("code");
                            final int co= Integer.parseInt(code);
                            MainActivity.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    shouye_tianqi.setText(text+" "+temperature+"°");
                                    if (co==0|co==1|co==2|co==3|co==38){//晴
                                        tianqi_imageView.setImageResource(R.mipmap.m_1);
                                    }
                                    if (co==4|co==5|co==6|co==7|co==8){//多云
                                        tianqi_imageView.setImageResource(R.mipmap.yun);
                                    }
                                    if (co==9){//阴
                                        tianqi_imageView.setImageResource(R.mipmap.ying);
                                    }
                                    if (co==10|co==11|co==12|co==37){//雨
                                        tianqi_imageView.setImageResource(R.mipmap.yu);
                                    }
                                    if (co==13){//小雨
                                        tianqi_imageView.setImageResource(R.mipmap.yu);
                                    }
                                    if (co==14){//中雨
                                        tianqi_imageView.setImageResource(R.mipmap.yu);
                                    }
                                    if (co==15|co==16|co==17|co==18){//暴雨
                                        tianqi_imageView.setImageResource(R.mipmap.yu);
                                    }
                                    if (co==19|co==20){//雨夹雪
                                        tianqi_imageView.setImageResource(R.mipmap.xue);
                                    }
                                    if (co==21|co==22|co==23|co==24|co==25){//雪
                                        tianqi_imageView.setImageResource(R.mipmap.xue);
                                    }
                                    if (co==26|co==27|co==28|co==29){//沙尘暴
                                        tianqi_imageView.setImageResource(R.mipmap.feng);
                                    }
                                    if (co==30|co==31){//雾霾
                                        tianqi_imageView.setImageResource(R.mipmap.mai);
                                    }
                                    if (co==32|co==33|co==34|co==35|co==36){//风
                                        tianqi_imageView.setImageResource(R.mipmap.feng);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            response.body().close();
                        }
                    }
                });
            }
        }).start();
    }

    private void initHeatMapOverlay(String city) {
        final List<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
        if (null == nodes || nodes.size() ==0 ){
            //为空的情况
        }else {
            nodes.clear();
        }
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        client=mBuilder
                .addNetworkInterceptor(NetCacheInterceptor)
                .addInterceptor(OfflineCacheInterceptor)
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/spotnum?city="+city;
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
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tipDialog.dismiss();
                                Toast.makeText(getActivity(),"数据获取失败",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String str = response.body().string();
                            Log.d("数据请求", "成功"+str);
                            final JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);

                                    //**

                                    double x_pi=3.14159265358979324;
                                    double x = longi - 0.0065, y = lat - 0.006;
                                    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
                                    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
                                    double tx_lon = z * Math.cos(theta);
                                    double tx_lat = z * Math.sin(theta);


                                    //**

                                    nodes.add(new HeatDataNode(new LatLng(tx_lat,tx_lon), redu));
                                }

                                MainActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //HeatDataNode 是热力图热点，包括热点位置和热度值（HeatOverlay会根据传入的全部节点的热度值范围计算最终的颜色表现）
                                        HeatOverlayOptions.IColorMapper mm=new HeatOverlayOptions.IColorMapper() {
                                            @Override
                                            public int colorForValue(double arg0) {
                                                // TODO Auto-generated method stub
                                                int alpha, red, green, blue;
                                                if (arg0 > 1) {
                                                    arg0 = 1;
                                                }
                                                arg0 = Math.sqrt(arg0);
                                                float a = 20000;
                                                red = 255;
                                                green = 0;
                                                blue = 0;
                                                if (arg0 > 0.7) {
                                                    red=255;
                                                    green = 0;
                                                    blue = 0;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                    red=255;
                                                    green = 0;
                                                    blue = 0;
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                    red=255;
                                                    green = 255;
                                                    blue = 0;
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                    red=0;
                                                    green = 255;
                                                    blue = 0;
                                                } else {
                                                    alpha = (int) (1700 * arg0);
                                                    red=0;
                                                    green = 0;
                                                    blue = 255;
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(25)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub


                                                    }
                                                    });
                                            heatOverlay=tencentMap.addHeatOverlay(heatOverlayOptions);
                                            tipDialog.dismiss();
                                        //heatOverlay = tencentMap.addHeatOverlay(heatOverlayOptions);*/
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }
    private void inittwoListPopupIfNeed() {
        if (mListPopup1 == null) {

            String[] listItems = new String[]{
                    "游客人数占比",
                    "区域人流量",
                    "视频监控"
            };
            List<String> data1 = new ArrayList<>();

            Collections.addAll(data1, listItems);

            final ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data1);
            mListPopup1 = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            mListPopup1.create(QMUIDisplayHelper.dp2px(getContext(), 150), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    two=adapterView.getItemAtPosition(i).toString();
                    //Toast.makeText(getActivity(), two, Toast.LENGTH_SHORT).show();
                    if (two.equals("游客人数占比")){
                        Intent intent=new Intent(getActivity(), Youke_zhanbiActivity.class);
                        startActivity(intent);
                    }
                    if (two.equals("区域人流量")){
                        Intent intent=new Intent(getActivity(), Quyu_renliuActivity.class);
                        intent.putExtra("dizhi",dizhi);
                        startActivity(intent);
                    }
                    if (two.equals("视频监控")){
                        Intent intent=new Intent(getActivity(), ShipingActivity.class);
                        startActivity(intent);
                    }

                    mListPopup1.dismiss();
                }
            });
            mListPopup1.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }
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
