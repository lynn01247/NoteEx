package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Request;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgaindicator.BGAFixedIndicator;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import tatait.com.noteex.fragment.ShareRecyclerGemFragment;
import tatait.com.noteex.fragment.ShareRecyclerGoodFragment;
import tatait.com.noteex.fragment.ShareRecyclerNewFragment;
import tatait.com.noteex.model.BannerModel;
import tatait.com.noteex.model.GsonVersionModel;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;

public class ViewPagerActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {
    private BGARefreshLayout mRefreshLayout;
    private BGABanner mBanner;
    private BGAFixedIndicator mIndicator;
    private ViewPager mContentVp;

    private Fragment[] mFragments;
    private String[] mTitles;
    private ShareRecyclerGemFragment gemFragement;
    private ShareRecyclerGoodFragment goodFragement;
    private ShareRecyclerNewFragment newFragement;

    private LinearLayout title_layout_back;
    private TextView title_layout_title;
    private ImageView title_layout_op_iv, title_layout_op_iv2;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_viewpager);
        title_layout_op_iv = getViewById(R.id.title_layout_op_iv);
        title_layout_op_iv2 = getViewById(R.id.title_layout_op_iv2);
        mRefreshLayout = getViewById(R.id.refreshLayout);
        mBanner = getViewById(R.id.banner);
        mIndicator = getViewById(R.id.indicator);
        //支持库的控件和自定义控件的换肤需要动态添加
        dynamicAddView(mIndicator, "background", R.color.colorPrimaryDark);
        mContentVp = getViewById(R.id.vp_viewpager_content);
        title_layout_back = getViewById(R.id.title_layout_back);
        title_layout_title = getViewById(R.id.title_layout_title);
    }

    @Override
    protected void setListener() {
        mRefreshLayout.setDelegate(this);

        title_layout_back.setOnClickListener(this);
        title_layout_op_iv.setOnClickListener(this);
        title_layout_op_iv2.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initBanner();

        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mApp, true));

        mFragments = new Fragment[3];
        mFragments[0] = gemFragement = new ShareRecyclerGemFragment();
        mFragments[1] = goodFragement = new ShareRecyclerGoodFragment();
        mFragments[2] = newFragement = new ShareRecyclerNewFragment();

        mTitles = new String[3];
        mTitles[0] = "精华置顶";
        mTitles[1] = "好评分享";
        mTitles[2] = "最新分享";
        mContentVp.setAdapter(new ContentViewPagerAdapter(getSupportFragmentManager()));
        mIndicator.initData(0, mContentVp);

        title_layout_title.setText("独乐乐不如众乐乐");
        title_layout_op_iv.setVisibility(View.VISIBLE);
        title_layout_op_iv.setImageResource(R.drawable.refresh_top);
        title_layout_op_iv2.setVisibility(View.VISIBLE);
    }

    private void initBanner() {
        mBanner.setAdapter(new BGABanner.Adapter() {
            @Override
            public void fillBannerItem(BGABanner banner, View view, Object model, int position) {
                Glide.with(banner.getContext()).load(model).placeholder(R.mipmap.holder).error(R.mipmap.holder).dontAnimate().thumbnail(0.1f).into((ImageView) view);
            }
        });
        mBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getApplicationContext());
                String link1 = "";
                String link2 = "";
                String link3 = "";
                try {
                    link1 = json.getString("link1");
                    link2 = json.getString("link2");
                    link3 = json.getString("link3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (position) {
                    case 0:
                        Intent intentLink1 = new Intent(ViewPagerActivity.this, WebActivity.class);
                        intentLink1.putExtra("url", link1);
                        startActivity(intentLink1);
                        break;
                    case 1:
                        Intent intentLink2 = new Intent(ViewPagerActivity.this, WebActivity.class);
                        intentLink2.putExtra("url", link2);
                        startActivity(intentLink2);
                        break;
                    case 2:
                        Intent intentLink3 = new Intent(ViewPagerActivity.this, WebActivity.class);
                        intentLink3.putExtra("url", link3);
                        startActivity(intentLink3);
                        break;
                }
            }
        });
        showLoadingDialog();
        String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_LINK_IMG;
        OkHttpClientManager.getAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<GsonVersionModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dismissLoadingDialog();
            }

            @Override
            public void onResponse(GsonVersionModel gsonModel) {
                dismissLoadingDialog();
                if (gsonModel != null && gsonModel.getData() != null & gsonModel.getData().size() > 0) {
                    BannerModel bannerModel = new BannerModel();
                    bannerModel.imgs = new ArrayList<>();
                    bannerModel.tips = new ArrayList<>();
                    for (int i = 0; i < gsonModel.getData().size(); i++) {
                        bannerModel.imgs.add(gsonModel.getData().get(i).getPath());
                        bannerModel.tips.add(gsonModel.getData().get(i).getTitle());
                    }
                    mBanner.setData(R.layout.view_image, bannerModel.imgs, bannerModel.tips);
                }
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        switch (mContentVp.getCurrentItem()) {
            case 0:
                gemFragement.onBGARefreshLayoutBeginRefreshing(refreshLayout);
                break;
            case 1:
                goodFragement.onBGARefreshLayoutBeginRefreshing(refreshLayout);
                break;
            case 2:
                newFragement.onBGARefreshLayoutBeginRefreshing(refreshLayout);
                break;
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        switch (mContentVp.getCurrentItem()) {
            case 0:
                return gemFragement.onBGARefreshLayoutBeginLoadingMore(refreshLayout);
            case 1:
                return goodFragement.onBGARefreshLayoutBeginLoadingMore(refreshLayout);
            case 2:
                return newFragement.onBGARefreshLayoutBeginLoadingMore(refreshLayout);
            default:
                return false;
        }
    }

    public void endRefreshing() {
        mRefreshLayout.endRefreshing();
    }

    public void endLoadingMore() {
        mRefreshLayout.endLoadingMore();
    }

    private class ContentViewPagerAdapter extends FragmentPagerAdapter {

        private ContentViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back:
                if (CommonUtil.setCurViewPagerHandler != null) {
                    Message message = new Message();
                    message.what = CommonUtil.setCurViewPagerCode;
                    CommonUtil.setCurViewPagerHandler.sendMessage(message);
                }
                finish();
                break;
            case R.id.title_layout_op_iv:
                switch (mContentVp.getCurrentItem()) {
                    case 0:
                        gemFragement.refreshGetData(true);
                        break;
                    case 1:
                        goodFragement.refreshGetData(true);
                        break;
                    case 2:
                        newFragement.refreshGetData(true);
                        break;
                }
                break;
            case R.id.title_layout_op_iv2:
                switch (mContentVp.getCurrentItem()) {
                    case 0:
                        gemFragement.MenuMoreClick();
                        break;
                    case 1:
                        goodFragement.MenuMoreClick();
                        break;
                    case 2:
                        newFragement.MenuMoreClick();
                        break;
                }
                break;
        }
    }

    /**
     * 退出事件
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (CommonUtil.setCurViewPagerHandler != null) {
                Message message = new Message();
                message.what = CommonUtil.setCurViewPagerCode;
                CommonUtil.setCurViewPagerHandler.sendMessage(message);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}