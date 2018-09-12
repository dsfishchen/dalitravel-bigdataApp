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
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.comedali.bigdata.R;
import com.comedali.bigdata.activity.Quyu_renliuActivity;
import com.comedali.bigdata.activity.Youke_zhanbiActivity;
import com.comedali.bigdata.adapter.YoukelaiyuanAdapter;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.HeatDataNode;
import com.tencent.tencentmap.mapsdk.maps.model.HeatOverlayOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<YoukelaiyuanEntity> youkedatas;
    private RecyclerView shishi_recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.shishi_jiankong_er,container,false);
        quyu_qiehuan=view.findViewById(R.id.quyu_qiehuan);
        mMapView = view.findViewById(R.id.shishi_map);
        shishi_recyclerView=view.findViewById(R.id.shishi_recyclerView);
        tencentMap = mMapView.getMap();
        //设定中心点坐标
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(25.695369,100.163383), //新的中心点坐标
                        12,  //新的缩放级别
                        0f, //俯仰角 0~45° (垂直地图时为0)
                        0f)); //偏航角 0~360° (正北方为0)
        //tencentMap.animateCamera(cameraSigma);//改变地图状态
        tencentMap.moveCamera(cameraSigma);//移动地图
        initHeatMapOverlay();
        initdata();
        quyu_qiehuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initoneListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            }
        });


        initData();
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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(getActivity(),"你点击了"+position,Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private void initData() {
        youkedatas=new ArrayList<>();
        YoukelaiyuanEntity model;
        for (int i = 0; i < 100; i++) {
            model=new YoukelaiyuanEntity();
            model.setId("古城南门"+i);
            model.setProvince(i+"");
            model.setBaifenbi(i*10+"");
            youkedatas.add(model);
        }

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
                    quyu_qiehuan.setText(one);
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
                        quyu_qiehuan.setText("大理古城");
                    }
                }
            });
        }
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
    private void initHeatMapOverlay() {
        //HeatDataNode 是热力图热点，包括热点位置和热度值（HeatOverlay会根据传入的全部节点的热度值范围计算最终的颜色表现）
        ArrayList<HeatDataNode> nodes = new ArrayList<HeatDataNode>();
        nodes.add(new HeatDataNode(new LatLng(25.711610,100.130424), 3486));//大理高铁站
        nodes.add(new HeatDataNode(new LatLng(25.718725,00.188961), 1126));//飞机场
        nodes.add(new HeatDataNode(new LatLng(25.711224,100.135489), 2386));//三塔
        nodes.add(new HeatDataNode(new LatLng(25.685353,100.136690), 176));//海舌
        nodes.add(new HeatDataNode(new LatLng(25.681717,100.144243), 486));//双廊
        nodes.add(new HeatDataNode(new LatLng(25.685237,100.156903), 166));//挖色

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
                green = 119;
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
                .radius(26)// 半径，单位是像素，这个数值越大运算量越大，默认值为18，建议设置在18-30之间)
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
