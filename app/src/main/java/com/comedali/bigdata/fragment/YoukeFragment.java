package com.comedali.bigdata.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.charts.BarChart;

/**
 * Created by 刘杨刚 on 2018/9/3.
 */
public class YoukeFragment extends Fragment {
    protected BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.youke_yi,container,false);
        return view;
    }

}
