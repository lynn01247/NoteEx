package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;

public class GuideActivity extends BaseActivity {
    public ViewPager mViewPager;
    private int[] img_ids = {R.drawable.page1, R.drawable.page2, R.drawable.page3};
    private ArrayList<View> img_views;
    private LinearLayout ll_container;
    int oldIndex = 0;
    int curIndex = 0;


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide);
        initView();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //初始化
    private void initView() {
        //加载引导页
        mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        img_views = new ArrayList<View>();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < img_ids.length; i++) {
            ll_container.addView(inflater.inflate(R.layout.ad_bottom_item, null));
            View view = LayoutInflater.from(this).inflate(R.layout.guide_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.guide_item_img);
            imageView.setBackgroundResource(img_ids[i]);
            img_views.add(view);
            //最后一张的时候，增加点击事件，跳转主页面
            if (i == img_ids.length - 1) {
                ImageView textView = (ImageView) view.findViewById(R.id.guide_item_tv);
                ImageView textView2 = (ImageView) view.findViewById(R.id.guide_item_img);
                textView.setVisibility(View.GONE);
                textView2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.ISFIRST, false);
                        startActivity(new Intent(GuideActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        }
        ll_container.getChildAt(0).findViewById(R.id.ad_item_v).setBackgroundResource(R.drawable.dot_focused);
        //设置指示器
        mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mViewPager.setCurrentItem(0, false);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                curIndex = position;
                ll_container.getChildAt(curIndex).findViewById(R.id.ad_item_v)
                        .setBackgroundResource(R.drawable.dot_focused);
                ll_container.getChildAt(oldIndex).findViewById(R.id.ad_item_v)
                        .setBackgroundResource(R.drawable.dot_normal);
                oldIndex = curIndex;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(img_views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(img_views.get(position));
                return img_views.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return img_ids.length;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
