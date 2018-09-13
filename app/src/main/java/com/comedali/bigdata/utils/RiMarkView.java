package com.comedali.bigdata.utils;

import android.content.Context;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by 刘杨刚 on 2018/9/11.
 */
public class RiMarkView extends MarkerView {
    private TextView tv_content;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public RiMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m=(int)e.getX();
        int renshu=(int)e.getY();
        String name=null;
        if (m==1){
            name="1时";
        }else if(m==2){
            name="2时";
        }else if(m==3){
            name="3时";
        }else if(m==4){
            name="4时";
        }else if(m==5){
            name="5时";
        }else if(m==6){
            name="6时";
        }else if(m==7){
            name="7时";
        }else if(m==8){
            name="8时";
        }else if(m==9){
            name="9时";
        }else if(m==10){
            name="10时";
        }else if(m==11){
            name="11时";
        }else if(m==12){
            name="12时";
        }else if(m==13){
            name="13时";
        }else if(m==14){
            name="14时";
        }else if(m==15){
            name="15时";
        }else if(m==16){
            name="16时";
        }else if(m==17){
            name="17时";
        }else if(m==18){
            name="18时";
        }else if(m==19){
            name="19时";
        }else if(m==20){
            name="20时";
        }else if(m==21){
            name="21时";
        }else if(m==22){
            name="22时";
        }else if(m==23){
            name="23时";
        }else if(m==24){
            name="24时";
        }
        tv_content.setText(name+"  人数:"+renshu+"人");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight()-30);
    }
}