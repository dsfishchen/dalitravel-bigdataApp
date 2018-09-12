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
public class YueMarkView extends MarkerView {
    private TextView tv_content;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public YueMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m = (int) e.getX();
        int renshu = (int) e.getY();
        String name = null;
        if (m == 1) {
            name = "1日";
        } else if (m == 2) {
            name = "2日";
        } else if (m == 3) {
            name = "3日";
        } else if (m == 4) {
            name = "4日";
        } else if (m == 5) {
            name = "5日";
        } else if (m == 6) {
            name = "6日";
        } else if (m == 7) {
            name = "7日";
        } else if (m == 8) {
            name = "8日";
        } else if (m == 9) {
            name = "9日";
        } else if (m == 10) {
            name = "10日";
        } else if (m == 11) {
            name = "11日";
        } else if (m == 12) {
            name = "12日";
        } else if (m == 13) {
            name = "13日";
        } else if (m == 14) {
            name = "14日";
        } else if (m == 15) {
            name = "15日";
        } else if (m == 16) {
            name = "16日";
        } else if (m == 17) {
            name = "17日";
        } else if (m == 18) {
            name = "18日";
        } else if (m == 19) {
            name = "19日";
        } else if (m == 20) {
            name = "20日";
        } else if (m == 21) {
            name = "21日";
        } else if (m == 22) {
            name = "22日";
        } else if (m == 23) {
            name = "23日";
        } else if (m == 24) {
            name = "24日";
        }else if (m == 25) {
            name = "25日";
        } else if (m == 26) {
            name = "26日";
        }else if (m == 27) {
            name = "27日";
        }else if (m == 28) {
            name = "28日";
        }else if (m == 29) {
            name = "29日";
        }else if (m == 30) {
            name = "30日";
        }else if (m == 31) {
            name = "31日";
        }
        tv_content.setText(name + "  人数:" + renshu);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 30);
    }
}