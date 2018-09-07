package com.comedali.bigdata.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.comedali.bigdata.R;
import com.comedali.bigdata.entity.YoukelaiyuanEntity;

import java.util.List;

/**
 * Created by 刘杨刚 on 2018/9/7.
 */
public class YoukelaiyuanAdapter extends BaseQuickAdapter<YoukelaiyuanEntity, BaseViewHolder> {
    public YoukelaiyuanAdapter(int layoutResId, @Nullable List<YoukelaiyuanEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, YoukelaiyuanEntity item) {
        helper.setText(R.id.paihang_id,item.getId())
                .setText(R.id.province_name,item.getProvince())
                .setText(R.id.baifen_bi,item.getBaifenbi());
        /*helper.setText(R.id.kezhan_name,item.getKezhan_name())
                .setText(R.id.kezhan_shenhe,item.getKezhan_zhuangtai());
        Glide.with(mContext).load(item.getImg_url()).into((ImageView) helper.getView(R.id.kezhan_imgurl));*/
    }
}