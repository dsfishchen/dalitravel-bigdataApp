package com.comedali.bigdata.utils;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by 刘杨刚 on 2018/9/5.
 */
public class MyMarkView extends MarkerView {
    private TextView tv_content;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m=(int)e.getX();
        int renshu=(int)e.getY();
        String name=null;
        if (m==0){
            name="大理市";
        }else if(m==1){
            name="洱源县";
        }else if(m==2){
            name="云龙县";
        }else if(m==3){
            name="宾川县";
        }else if(m==4){
            name="南涧县";
        }else if(m==5){
            name="剑川县";
        }else if(m==6){
            name="永平县";
        }else if(m==7){
            name="鹤庆县";
        }else if(m==8){
            name="祥云县";
        }else if(m==9){
            name="漾濞县";
        }else if(m==10){
            name="巍山县";
        }else if(m==11){
            name="弥渡县";
        }
        tv_content.setText(name+"  人数:"+renshu);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight()-30);
    }
}
