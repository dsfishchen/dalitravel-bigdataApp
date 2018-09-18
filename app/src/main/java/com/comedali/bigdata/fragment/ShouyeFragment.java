package com.comedali.bigdata.fragment;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Quyu_renliuActivity;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.utils.NetworkUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
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
    private Activity activity=getActivity();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.shouye_yi,container,false);
        mMapView = view.findViewById(R.id.map);
        shouye_tianqi=view.findViewById(R.id.shouye_tianqi);
        nice_button1=view.findViewById(R.id.nice_button1);
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

        renshu1=view.findViewById(R.id.renshu_1);
        renshu2=view.findViewById(R.id.renshu_2);
        renshu3=view.findViewById(R.id.renshu_3);
        renshu4=view.findViewById(R.id.renshu_4);
        renshu5=view.findViewById(R.id.renshu_5);
        renshu6=view.findViewById(R.id.renshu_6);

        dizhi=nice_button1.getText().toString();

        QMUIStatusBarHelper.translucent(getActivity());// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());//状态栏字体颜色--黑色
        //设定中心点坐标
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(25.781344,100.207672), //新的中心点坐标
                        10.8f,  //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f)); //偏航角 0~360° (正北方为0)
        //tencentMap.animateCamera(cameraSigma);//改变地图状态
        tencentMap.moveCamera(cameraSigma);//移动地图
        initHeatMapOverlayDali();
        initdata();
        initmm();
        initrenshu();
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
            if (activity==null){

            }else {
                if (!NetworkUtil.checkNet(activity)) {
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
        //setup cache
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=all";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //每30秒获取实时人数
        /*new Timer().schedule(new TimerTask(){

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
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                final String result = jsonData.getString("result");
                                //Log.d("result", result);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String renshu=result;
                                        int m=Integer.parseInt(renshu);

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
        },0,5000);*/

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
                            JSONObject jsonData = new JSONObject(str);
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                final String result = jsonData.getString("result");
                                //Log.d("result", result);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String renshu=result;
                                        int m=Integer.parseInt(renshu);

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

    private void initdata() {
        //标注坐标
        LatLng latLng = new LatLng(25.695060,100.164413);
        final Marker marker = tencentMap.addMarker(new MarkerOptions().
                position(latLng).
                title("大理古城").
                snippet("游客人数 20150人\n 设备数量 152台"));
        tencentMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.906058,100.099268))
                .title("蝴蝶泉").snippet("游客人数 10150人\n 设备数量 152台"));
        //创建图标
        //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
    }

    private void initoneListPopupIfNeed() {
        if (mListPopup == null) {

            String[] listItems = new String[]{
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
                        //数据获取
                        initHeatMapOverlayDali();
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
                        //数据获取
                        initeryuan();
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
                        //数据获取
                        initbinchuan();
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
                        //数据获取
                        inityongping();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initnanjian();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initweishan();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initheqing();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        inityunlong();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initjianchuan();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initxiangyun();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        inityangbi();
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
                        tencentMap.moveCamera(cameraSigma);//移动地图
                        initmidu();
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
                        nice_button1.setText("大理市");
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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
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
                        getActivity().runOnUiThread(new Runnable() {
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    shouye_tianqi.setText(text+" "+temperature+"°");

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

    private void initeryuan(){
        //获取数据洱源
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=eryuan";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initbinchuan(){
        //获取数据宾川县
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=binchuan";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initnanjian(){
        //获取数据南涧
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=nanjian";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void inityongping(){
        //获取数据永平
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=yongping";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initweishan(){
        //获取数据巍山
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=weishan";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initheqing(){
        //获取数据鹤庆
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=heqing";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void inityunlong(){
        //获取数据云龙
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=yunlong";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initjianchuan(){
        //获取数据剑川县
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=jianchuan";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initxiangyun(){
        //获取数据祥云县
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=xiangyun";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void inityangbi(){
        //获取数据漾濞县
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=yangbi";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initmidu(){
        //获取数据弥渡县
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=midu";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));//弥渡入城口
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                        tencentMap.addHeatOverlay(heatOverlayOptions);
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
    private void initHeatMapOverlayDali() {

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache");
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
        String url="http://192.168.190.119:8080/flowmeter/num?city=dali";
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
                            String resultStr = jsonData.getString("success");
                            if (resultStr.equals("true")){
                                String result=jsonData.getString("result");
                                JSONArray nums = new JSONArray(result);
                                final ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                                for (int i=0;i<nums.length();i++){
                                    JSONObject jsonObject=nums.getJSONObject(i);
                                    int redu=jsonObject.getInt("nums");
                                    String latitude=jsonObject.getString("latitude");
                                    String longitude=jsonObject.getString("longitude");
                                    double lat= Double.parseDouble(latitude);
                                    double longi= Double.parseDouble(longitude);
                                    nodes.add(new HeatDataNode(new LatLng(lat,longi), redu));
                                }

                                getActivity().runOnUiThread(new Runnable() {
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
                                                green = 19;
                                                blue = 3;
                                                if (arg0 > 0.7) {
                                                    green = 78;
                                                    blue = 1;
                                                }
                                                if (arg0 > 0.6) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.7, 3) + 240);
                                                } else if (arg0 > 0.4) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.5, 3) + 200);
                                                } else if (arg0 > 0.2) {
                                                    alpha = (int) (a * Math.pow(arg0 - 0.3, 3) + 160);
                                                } else {
                                                    alpha = (int) (700 * arg0);
                                                }
                                                if (alpha > 255) {
                                                    alpha = 255;
                                                }
                                                return Color.argb(alpha, red, green, blue);
                                            }
                                        };
                                        HeatOverlayOptions heatOverlayOptions = new HeatOverlayOptions();
                                        heatOverlayOptions.nodes(nodes)
                                                .radius(30)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                                                //.colorMapper(new ColorMapper())
                                                .colorMapper(mm)
                                                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                                                    @Override
                                                    public void onHeatMapReady() {
                                                        // TODO Auto-generated method stub
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Toast.makeText(MainActivity.this, "热力图数据准备完毕", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                    });
                                            tencentMap.addHeatOverlay(heatOverlayOptions);
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
                    "区域人流量"
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
                    if (two=="游客人数占比"){
                        Intent intent=new Intent(getActivity(), Youke_zhanbiActivity.class);
                        startActivity(intent);
                    }
                    if (two=="区域人流量"){
                        Intent intent=new Intent(getActivity(), Quyu_renliuActivity.class);
                        intent.putExtra("dizhi",dizhi);
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
