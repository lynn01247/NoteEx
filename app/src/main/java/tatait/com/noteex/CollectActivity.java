package tatait.com.noteex;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tatait.com.noteex.fragment.CollectFragment;
import tatait.com.noteex.slide.SwipeBackHelper;

public class CollectActivity extends BaseActivity {
    ViewPager viewPager;
    LinearLayout back;
    TextView title_top, back_tv;
    ImageView op_iv, op_iv2;
    private ArrayList<Fragment> fragmentList;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_note_group);

        back = (LinearLayout) findViewById(R.id.note_group_top_bar).findViewById(R.id.title_layout_back);
        back_tv = (TextView) findViewById(R.id.note_group_top_bar).findViewById(R.id.title_layout_back_tv);
        title_top = (TextView) findViewById(R.id.note_group_top_bar).findViewById(R.id.title_layout_title);
        op_iv = (ImageView) findViewById(R.id.note_group_top_bar).findViewById(R.id.title_layout_op_iv);
        op_iv2 = (ImageView) findViewById(R.id.note_group_top_bar).findViewById(R.id.title_layout_op_iv2);
        viewPager = getViewById(R.id.note_group_viewPager);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(this);
        op_iv.setOnClickListener(this);
        op_iv2.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeEdgePercent(0.5f)
                .setSwipeSensitivity(0.5f)
                .setClosePercent(0.5f)
                .setSwipeRelateEnable(true).setSwipeSensitivity(1);
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            // 设置退出的动画
            Fade fade = new Fade();
            fade.setDuration(300);
            getWindow().setExitTransition(fade);
        }

        Bundle bundle = getIntent().getExtras();
        title_top.setText("收藏");
        back_tv.setText("返回");
        fragmentList = new ArrayList<>();
        Fragment recyclerFragment = new CollectFragment();
        recyclerFragment.setArguments(bundle);
        fragmentList.add(recyclerFragment);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);

        op_iv.setVisibility(View.VISIBLE);
        op_iv.setImageResource(R.drawable.refresh_top);
        op_iv2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_layout_back:
                finish();
                break;
            case R.id.title_layout_op_iv: // refresh
                ((CollectFragment) fragmentList.get(0)).refreshGetData(true);
                break;
            case R.id.title_layout_op_iv2:// more
                ((CollectFragment) fragmentList.get(0)).MenuMoreClick();
                break;
        }
    }

    public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        private TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }
}