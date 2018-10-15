package com.comedali.bigdata.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Quyu_renliuActivity;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.MessageEvent;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.comedali.bigdata.utils.NetworkUtil;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.HeatDataNode;
import com.tencent.tencentmap.mapsdk.maps.model.HeatOverlayOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
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
 * Created by 刘杨刚 on 2018/9/6.
 */
public class ShishijiankongFragment extends Fragment {
    private TextureMapView mMapView;
    private TencentMap tencentMap;
    private Button quyu_qiehuan;
    private QMUIListPopup mListPopup;
    private String one;
    private String two;
    private YoukelaiyuanAdapter adapter;
    private List<YoukelaiyuanEntity> youkedatas=new ArrayList<>();
    private RecyclerView shishi_recyclerView;
    private OkHttpClient client;
    private TextView jdrs_1;
    private TextView jdrs_2;
    private TextView jdrs_3;
    private TextView jdrs_4;
    private TextView jdrs_5;
    private TextView jdrs_6;
    private String dizhi;
    private String[] listItems;
    private String[] listPlace_id;
    private String dizhi_m=null;
    private String id=null;
    private String name=null;
    private double[] latw;
    private double[] lonw;
    private double lat1;
    private double lon1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.shishi_jiankong_er,container,false);
        dizhi=getActivity().getIntent().getStringExtra("dizhi");
        initChoose();
        quyu_qiehuan=view.findViewById(R.id.quyu_qiehuan);
        mMapView = view.findViewById(R.id.shishi_map);
        shishi_recyclerView=view.findViewById(R.id.shishi_recyclerView);
        tencentMap = mMapView.getMap();
        //设定中心点坐标
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(lat1,lon1), //新的中心点坐标
                        12,  //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f)); //偏航角 0~360° (正北方为0)
        //tencentMap.animateCamera(cameraSigma);//改变地图状态
        tencentMap.moveCamera(cameraSigma);//移动地图
        UiSettings mapUiSettings = tencentMap.getUiSettings();
        mapUiSettings.setScaleViewEnabled(false);
        mapUiSettings.setLogoScale(-0.0f);

        //initHeatMapOverlay();
        initlatLngdata();

        quyu_qiehuan.setText(name);
        quyu_qiehuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });
        jdrs_1=view.findViewById(R.id.jdrs_1);
        jdrs_2=view.findViewById(R.id.jdrs_2);
        jdrs_3=view.findViewById(R.id.jdrs_3);
        jdrs_4=view.findViewById(R.id.jdrs_4);
        jdrs_5=view.findViewById(R.id.jdrs_5);
        jdrs_6=view.findViewById(R.id.jdrs_6);

        initData(id);
        initjdrs(id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shishi_recyclerView.setLayoutManager(layoutManager);
        adapter = new YoukelaiyuanAdapter(R.layout.youkelaiyuan_er_item, youkedatas);
        adapter.openLoadAnimation();//动画 默认提供5种方法（渐显、缩放、从下到上，从左到右、从右到左）
        adapter.isFirstOnly(false);//重复执行可设置

        //给RecyclerView设置适配器
        shishi_recyclerView.setAdapter(adapter);
        //添加Android自带的分割线
        shishi_recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
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
            if (!NetworkUtil.checkNet(getActivity())) {
                int offlineCacheTime = 60*60*24*7;//离线的时候的缓存的过期时间
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                        .build();
            }
            return chain.proceed(request);
        }
    };
    private void initData(String id) {

        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "okhttpCache2");
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
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/toursitenum?city="+dizhi_m+"&place_id="+id;
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
                                final String result=jsonData.getString("result");
                                Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String renshu=result;
                                        int m=Integer.parseInt(renshu);
                                        if (renshu.length()==6){
                                            jdrs_1.setText(renshu.charAt(0)+"");
                                            jdrs_2.setText(renshu.charAt(1)+"");
                                            jdrs_3.setText(renshu.charAt(2)+"");
                                            jdrs_4.setText(renshu.charAt(3)+"");
                                            jdrs_5.setText(renshu.charAt(4)+"");
                                            jdrs_6.setText(renshu.charAt(5)+"");
                                        }
                                        if (renshu.length()==5){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText(renshu.charAt(0)+"");
                                            jdrs_3.setText(renshu.charAt(1)+"");
                                            jdrs_4.setText(renshu.charAt(2)+"");
                                            jdrs_5.setText(renshu.charAt(3)+"");
                                            jdrs_6.setText(renshu.charAt(4)+"");
                                        }
                                        if (renshu.length()==4){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText("0");
                                            jdrs_3.setText(renshu.charAt(0)+"");
                                            jdrs_4.setText(renshu.charAt(1)+"");
                                            jdrs_5.setText(renshu.charAt(2)+"");
                                            jdrs_6.setText(renshu.charAt(3)+"");
                                        }
                                        if (renshu.length()==3){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText("0");
                                            jdrs_3.setText("0");
                                            jdrs_4.setText(renshu.charAt(0)+"");
                                            jdrs_5.setText(renshu.charAt(1)+"");
                                            jdrs_6.setText(renshu.charAt(2)+"");
                                        }
                                        if (renshu.length()==2){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText("0");
                                            jdrs_3.setText("0");
                                            jdrs_4.setText("0");
                                            jdrs_5.setText(renshu.charAt(0)+"");
                                            jdrs_6.setText(renshu.charAt(1)+"");
                                        }
                                        if (renshu.length()==1){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText("0");
                                            jdrs_3.setText("0");
                                            jdrs_4.setText("0");
                                            jdrs_5.setText("0");
                                            jdrs_6.setText(renshu.charAt(0)+"");
                                        }
                                        if (renshu.length()==0){
                                            jdrs_1.setText("0");
                                            jdrs_2.setText("0");
                                            jdrs_3.setText("0");
                                            jdrs_4.setText("0");
                                            jdrs_5.setText("0");
                                            jdrs_6.setText("0");
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

    private void initjdrs(String id) {

        //youkedatas=new ArrayList<>();

        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("获取数据中")
                .create();
        //tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        //二
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
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/toursitepointnum?city="+dizhi_m+"&place_id="+id;
        final Request request = new Request.Builder()
                .url(url)
                .build();
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
                        youkedatas.clear();
                        for (int i=0;i<num.length();i++){
                            JSONObject jsonObject=num.getJSONObject(i);
                            String location=jsonObject.getString("location");
                            int nums=jsonObject.getInt("nums");
                            String equipment_mac=jsonObject.getString("equipment_mac");
                            final YoukelaiyuanEntity model=new YoukelaiyuanEntity();
                            model.setId(i+1+"");
                            model.setProvince(location);
                            model.setBaifenbi(nums+"");
                            youkedatas.add(model);
                        }
                        Quyu_renliuActivity.getInstance().runOnUiThread(new Runnable() {
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
    private void initChoose(){
        if (dizhi.equals("大理州")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("大理市")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("洱源县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("宾川县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("弥渡县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("永平县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("南涧县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("巍山县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("鹤庆县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("云龙县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("剑川县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("祥云县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
        if (dizhi.equals("漾濞县")){
            dizhi_m="dali','eryuan','bingchuan','weishan','yongping','nanjian','heqing','yunlong','jianchuan','xiangyun','yangbi','midu";
            id="3";
            name="大理古城";
            lat1=25.695369;
            lon1=100.163383;
            initDizhiChoose(dizhi_m);
        }
    }
    private void initDizhiChoose(String dizhi_m){
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
        String url="http://home.comedali.com:8088/bigdataservice/flowmeter/spotnum1?city="+dizhi_m;
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
                        //String[] listItems = new String[num.length()];
                        listItems = new String[13];
                        listPlace_id=new String[13];
                        ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
                        latw=new double[13];
                        lonw=new double[13];
                        for (int i=0;i<num.length();i++){
                            JSONObject jsonObject=num.getJSONObject(i);
                            String place_name=jsonObject.getString("place_name");
                            String place_id=jsonObject.getString("place_id");
                            String latitude=jsonObject.getString("latitude");
                            String longitude=jsonObject.getString("longitude");
                            int nums=jsonObject.getInt("nums");
                            double lat= Double.parseDouble(latitude);
                            double long1= Double.parseDouble(longitude);
                            if (place_name.equals("大理古城")){
                                listItems[0]=place_name;
                                listPlace_id[0]=place_id;
                                latw[0]=lat;
                                lonw[0]=long1;
                            }
                            if (place_name.equals("蝴蝶泉")){
                                listItems[1]=place_name;
                                listPlace_id[1]=place_id;
                                latw[1]=lat;
                                lonw[1]=long1;
                            }
                            if (place_name.equals("感通索道")){
                                listItems[2]=place_name;
                                listPlace_id[2]=place_id;
                                latw[2]=lat;
                                lonw[2]=long1;
                            }
                            if (place_name.equals("崇圣寺三塔")){
                                listItems[3]=place_name;
                                listPlace_id[3]=place_id;
                                latw[3]=lat;
                                lonw[3]=long1;
                            }
                            if (place_name.equals("南诏风情岛")){
                                listItems[4]=place_name;
                                listPlace_id[4]=place_id;
                                latw[4]=lat;
                                lonw[4]=long1;
                            }
                            if (place_name.equals("天龙八部影视城")){
                                listItems[5]=place_name;
                                listPlace_id[5]=place_id;
                                latw[5]=lat;
                                lonw[5]=long1;
                            }
                            if (place_name.equals("鸡足山")){
                                listItems[6]=place_name;
                                listPlace_id[6]=place_id;
                                latw[6]=lat;
                                lonw[6]=long1;
                            }
                            if (place_name.equals("洗马潭大索道")){
                                listItems[7]=place_name;
                                listPlace_id[7]=place_id;
                                latw[7]=lat;
                                lonw[7]=long1;
                            }
                            if (place_name.equals("巍宝山")){
                                listItems[8]=place_name;
                                listPlace_id[8]=place_id;
                                latw[8]=lat;
                                lonw[8]=long1;
                            }
                            if (place_name.equals("新华村")){
                                listItems[9]=place_name;
                                listPlace_id[9]=place_id;
                                latw[9]=lat;
                                lonw[9]=long1;
                            }
                            if (place_name.equals("石宝山")){
                                listItems[10]=place_name;
                                listPlace_id[10]=place_id;
                                latw[10]=lat;
                                lonw[10]=long1;
                            }
                            if (place_name.equals("沙溪古镇")){
                                listItems[11]=place_name;
                                listPlace_id[11]=place_id;
                                latw[11]=lat;
                                lonw[11]=long1;
                            }
                            if (place_name.equals("海舌公园")){
                                listItems[12]=place_name;
                                listPlace_id[12]=place_id;
                                latw[12]=lat;
                                lonw[12]=long1;
                            }
                            nodes.add(new HeatDataNode(new LatLng(lat,long1), nums));
                        }
                        initHeatMapOverlay(nodes);
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

    private void initoneListPopupIfNeed() {
        if (mListPopup == null) {
            if (listItems==null){
                listItems=new String[]{
                        "获取失败"
                };
            }
            final String[] listItems1 = new String[]{
                    "大理古城",
                    "蝴蝶泉",//6
                    "感通索道",//8
                    "崇圣寺三塔",//10
                    "南诏风情岛",//11
                    "天龙八部影视城",//12
                    "鸡足山",//15
                    "洗马潭大索道",//17
                    "巍宝山",//36
                    "新华村",//44
                    "石宝山",//46
                    "沙溪古镇",//47
                    "海舌公园"//48
            };
            List<String> data = new ArrayList<>();
            Collections.addAll(data, listItems1);

            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data);

            mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            final QMUIListPopup finalMListPopup = mListPopup;
            mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 100), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getActivity(), "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                    one=adapterView.getItemAtPosition(i).toString();
                    quyu_qiehuan.setText(one);
                    if (one.equals(listItems1[i])){
                        EventBus.getDefault().post(new MessageEvent(one));
                        initData(listPlace_id[i]);
                        initjdrs(listPlace_id[i]);
                        double wei=latw[i];
                        double jing=lonw[i];
                        CameraUpdate cameraSigma =
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(wei,jing), //新的中心点坐标
                                        12,  //新的缩放级别
                                        0f, //俯仰角 0~45° (垂直地图时为0)
                                        0f)); //偏航角 0~360° (正北方为0)
                        //tencentMap.animateCamera(cameraSigma);//改变地图状态
                        tencentMap.moveCamera(cameraSigma);//移动地图
                    }
                    finalMListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //mActionButton2.setText(getContext().getResources().getString(R.string.popup_list_action_button_text_show));
                    if (one!=null){
                        quyu_qiehuan.setText(one);
                    }else {
                        quyu_qiehuan.setText(name);
                    }
                }
            });
        }
    }

    private void initlatLngdata() {
        //标注坐标
        /*LatLng latLng = new LatLng(25.695060,100.164413);
        final Marker marker = tencentMap.addMarker(new MarkerOptions().
                position(latLng).
                title("大理古城").
                snippet("游客人数 20150人\n 设备数量 152台"));
        tencentMap.addMarker(new MarkerOptions()
                .position(new LatLng(25.906058,100.099268))
                .title("蝴蝶泉").snippet("游客人数 10150人\n 设备数量 152台"));*/
        //创建图标
        //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
    }
    private void initHeatMapOverlay(ArrayList<HeatDataNode> nodes) {
        //HeatDataNode 是热力图热点，包括热点位置和热度值（HeatOverlay会根据传入的全部节点的热度值范围计算最终的颜色表现）
        /*ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
        nodes.add(new HeatDataNode(new LatLng(25.711610,100.130424), 3486));//大理高铁站
        nodes.add(new HeatDataNode(new LatLng(25.718725,00.188961), 1126));//飞机场
        nodes.add(new HeatDataNode(new LatLng(25.711224,100.135489), 2386));//三塔
        nodes.add(new HeatDataNode(new LatLng(25.685353,100.136690), 176));//海舌
        nodes.add(new HeatDataNode(new LatLng(25.681717,100.144243), 486));//双廊
        nodes.add(new HeatDataNode(new LatLng(25.685237,100.156903), 166));//挖色*/

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
                .radius(26)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
                //.colorMapper(new ColorMapper())
                .colorMapper(mm)
                .onHeatMapReadyListener(new HeatOverlayOptions.OnHeatMapReadyListener() {
                    @Override
                    public void onHeatMapReady() {
                        // TODO Auto-generated method stub

                    }
                });
        tencentMap.addHeatOverlay(heatOverlayOptions);
        //heatOverlay = tencentMap.addHeatOverlay(heatOverlayOptions);
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
