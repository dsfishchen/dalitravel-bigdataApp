package com.comedali.bigdata.utils;

import android.content.Context;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by 刘杨刚 on 2018/9/19.
 */
public class Youke_zhanbiMarkerView extends MarkerView {
    private TextView tv_content;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public Youke_zhanbiMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m=(int)e.getX();
        double renshu=(double) e.getY();
        renshu=(double)Math.round(renshu*100)/100;
        String name=null;
        if (m==0){
            name="宾川县";
        }else if(m==1){
            name="大理市";
        }else if(m==2){
            name="洱源县";
        }else if(m==3){
            name="鹤庆县";
        }else if(m==4){
            name="剑川县";
        }else if(m==5){
            name="弥渡县";
        }else if(m==6){
            name="南涧彝族自治县";
        }else if(m==7){
            name="巍山彝族回族自治";
        }else if(m==8){
            name="祥云县";
        }else if(m==9){
            name="漾濞县";
        }else if(m==10){
            name="漾濞彝族自治县";
        }else if(m==11){
            name="云龙县";
        }
        tv_content.setText(name+"  占比:"+renshu+"%");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight()-30);
    }
}
