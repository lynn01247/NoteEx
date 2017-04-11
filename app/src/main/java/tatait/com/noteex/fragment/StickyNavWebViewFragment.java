package tatait.com.noteex.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import tatait.com.noteex.R;
import tatait.com.noteex.ViewPagerActivity;

/**
 * 创建时间:15/9/27 下午12:53
 * 描述:
 */
public class StickyNavWebViewFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private WebView mContentWv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_webview_sticky_nav);
        mContentWv = getViewById(R.id.wv_webview_content);
    }

    @Override
    protected void setListener() {
        mContentWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ((ViewPagerActivity) getActivity()).endRefreshing();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContentWv.getSettings().setJavaScriptEnabled(true);
        mContentWv.loadUrl("https://github.com/bingoogolapple");
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mContentWv.reload();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        Log.i(TAG, "加载更多");
        return false;
    }
}