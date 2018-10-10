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
public class NianMarkView extends MarkerView {
    private TextView tv_content;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public NianMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m = (int) e.getX();
        int renshu = (int) e.getY();
        String name = m+"年";

        tv_content.setText(name + "  人数:" + renshu+"人");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 30);
    }
}