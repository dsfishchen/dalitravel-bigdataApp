package com.comedali.bigdata.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 刘杨刚 on 2018/8/23.
 */
public class NetworkUtil {
    public static boolean checkNet(Context context){
        boolean wifiConnected = isWIFIConnected(context);
        boolean mobileConnected = isMOBILEConnected(context);
        boolean youxian = isYouXian(context);
        if(wifiConnected==false && mobileConnected== false&&youxian==false){
            //如果都没有连接，提示用户当前没有网络
            return false;
        }
        return true;
    }
    private static boolean isYouXian(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isMOBILEConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private static boolean isWIFIConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo !=null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    // 新方法
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
}
