package com.comedali.bigdata.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comedali.bigdata.MainActivity;
import com.comedali.bigdata.R;
import com.comedali.bigdata.utils.NetworkUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

/**
 * Created by 刘杨刚 on 2018/9/29.
 */
public class ShipingActivity extends AppCompatActivity {
    private CommonTitleBar commonTitleBar;
    private WebView shiping_webview;
    private ProgressBar progressBar;
    private LinearLayout web_root;
    private FrameLayout fullVideo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shiping_er);
        //顶部导航栏按钮事件
        commonTitleBar=findViewById(R.id.shiping_back);
        progressBar= findViewById(R.id.progressbar);//进度条
        shiping_webview=findViewById(R.id.shiping_webview);
        web_root=findViewById(R.id.web_root);
        fullVideo=findViewById(R.id.full_video);
        commonTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action==commonTitleBar.ACTION_LEFT_BUTTON){
                    if (shiping_webview.canGoBack()){
                        shiping_webview.goBack();
                    }else {
                        ShipingActivity.this.finish();
                    }

                }
            }
        });
        shiping_webview.loadUrl("http://bike.yuncaibang.com/ckplayer/videolist.html");
        shiping_webview.setWebChromeClient(webChromeClient);
        shiping_webview.setWebViewClient(webViewClient);
        WebSettings webSettings=shiping_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setPluginState(WebSettings.PluginState.ON);//告诉webview 是否使用插件
        webSettings.setLoadWithOverviewMode(true);//q全屏
        if (NetworkUtil.checkNet(this)){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//不使用缓存，只从网络获取数据.
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//不使用缓存，只从网络获取数据.
        }
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
    }
    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Log.i("ansen","拦截url:"+url);

            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        private CustomViewCallback customViewCallback;
        private View nVideoView = null;
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //Log.i("ansen","网页标题:"+title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }

      /*  @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {//进入全屏
            super.onShowCustomView(view, callback);
            if (nVideoView != null) {
                callback.onCustomViewHidden();
                return;
            }
            nVideoView = view;
            nVideoView.setVisibility(View.VISIBLE);
            customViewCallback = callback;
            fullVideo.addView(nVideoView);
            fullVideo.setVisibility(View.VISIBLE);
            fullVideo.bringToFront();
            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //设置全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        @Override
        public void onHideCustomView() {//退出全屏
            if (nVideoView == null) {
                return;
            }
            try {
                customViewCallback.onCustomViewHidden();
            } catch (Exception e) {
            }
            nVideoView.setVisibility(View.GONE);
            fullVideo.removeView(nVideoView);
            nVideoView = null;
            fullVideo.setVisibility(View.GONE);
            // 设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // 取消全屏
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.i("ansen","是否有上一个页面:"+shiping_webview.canGoBack());
        if (shiping_webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
            shiping_webview.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    @JavascriptInterface //仍然必不可少
    public void  getClient(String str){
        //Log.i("ansen","html调用客户端:"+str);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        shiping_webview.onPause();
        shiping_webview.freeMemory();
        shiping_webview.removeAllViews();
        shiping_webview.destroy();
        shiping_webview = null;
    }
}
