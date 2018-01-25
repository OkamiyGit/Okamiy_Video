package com.example.wangqing.okamiy_video.home;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseFragment;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class BlogFragment extends BaseFragment {
    private static final String TAG = "BlogFragment";
    private static WebView mWebView;
    private ProgressBar mProgressBar;
    private static final int MAX_VALUE = 100;
    private static final String BLOG_URL = "http://m.blog.csdn.net/blog/index?username=hejjunlin";

    //监听webview加载情况
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //加载过程中更新进度
            mProgressBar.setProgress(newProgress);
            if (newProgress == MAX_VALUE) {
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog;
    }

    @Override
    protected void initView() {
        mProgressBar = bindViewId(R.id.pb_progress);
        mWebView = bindViewId(R.id.webview);

        //用来设置webview属性
        WebSettings webSettings = mWebView.getSettings();
        //支持js脚本
        webSettings.setJavaScriptEnabled(true);
        //支持缩放
        webSettings.setSupportZoom(true);
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //多窗口
        webSettings.supportMultipleWindows();
        //当webview调用requestFocus时为webview设置节点
        webSettings.setNeedInitialFocus(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(false);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //优先使用缓存:
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //提高渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 开启H5(APPCache)缓存功能
        webSettings.setAppCacheEnabled(true);
        // 开启 DOM storage 功能
        webSettings.setDomStorageEnabled(true);
        // 应用可以有数据库
        webSettings.setDatabaseEnabled(true);
        // 可以读取文件缓存(manifest生效)
        webSettings.setAllowFileAccess(true);
        //设置Progress
        mProgressBar.setMax(MAX_VALUE);

        //加载网页
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(BLOG_URL);
    }

    @Override
    protected void initData() {

    }

    /**
     * 点击返回键，返回我们上级界面
     *
     * @param keyCode
     * @param event
     * @return
     */

    public static boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            Log.i(TAG, "BlogFragmrnt  -------  onKeyDown: ");
            mWebView.goBack();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除缓存
        mWebView.clearCache(true);
        //清除历史记录
        mWebView.clearHistory();
        mWebView.destroy();
    }
}
