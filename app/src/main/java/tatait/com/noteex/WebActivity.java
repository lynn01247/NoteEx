package tatait.com.noteex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import tatait.com.noteex.util.StringUtils;

/**
 * 加载网页的Activity
 *
 * @author 农民伯伯
 * @seehttp: \/\/www.cnblogs.com\/over140/archive\/2013/03/07/2947721.html
 */
public class WebActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "WebActivity";
    private WebView webview;
    private String url;
    private TextView title;
    private ProgressBar myProgressBar;
    private ImageView title_layout_op_iv;
    private final String mPageName = "WebActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web);
        title = (TextView) findViewById(R.id.web_activity_title_layout)
                .findViewById(R.id.title_layout_title);
        webview = (WebView) findViewById(R.id.webview);
        title_layout_op_iv = (ImageView) findViewById(R.id.title_layout_op_iv);
        title_layout_op_iv.setVisibility(View.VISIBLE);
        myProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
    }

    @Override
    protected void setListener() {
        findViewById(R.id.web_activity_title_layout).findViewById(R.id.title_layout_back).setOnClickListener(this);
        title_layout_op_iv.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // ~~~ 获取参数
        url = StringUtils.getObjString(getIntent().getStringExtra("url"));
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);// 关键点
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240) {
            webSettings.setDefaultZoom(ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            webSettings.setDefaultZoom(ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            webSettings.setDefaultZoom(ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            webSettings.setDefaultZoom(ZoomDensity.FAR);
        } else {
            webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
        }
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == myProgressBar.getVisibility()) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                    myProgressBar.setProgress(newProgress);
                }
                Log.i(TAG, "newProgress: " + newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                WebActivity.this.title.setText(title);
                super.onReceivedTitle(view, title);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

        });
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        webview.loadUrl(url);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webview.reload();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back:
                finish();
            case R.id.title_layout_op_iv:
                webview.reload();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按下的是后退键，并且webview可以后退，则webview后退
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return true;
    }
}