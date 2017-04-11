package tatait.com.noteex;

import android.app.ActivityOptions;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.BaseStickyHeaderAnimator;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;
import solid.ren.skinlibrary.loader.SkinManager;
import tatait.com.noteex.db.EmInviteMessage;
import tatait.com.noteex.db.EmInviteMessage.InviteMesageStatus;
import tatait.com.noteex.db.EmInviteMessgeDao;
import tatait.com.noteex.db.EmUser;
import tatait.com.noteex.db.EmUserDao;
import tatait.com.noteex.fragment.EmTalkFragment;
import tatait.com.noteex.fragment.FileTagFragment;
import tatait.com.noteex.fragment.MainFragment;
import tatait.com.noteex.fragment.SharedFragment;
import tatait.com.noteex.slide.SwipeBackHelper;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.AssetFileUtils;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.DensityUtil;
import tatait.com.noteex.util.LoginSharedUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;
import tatait.com.noteex.widget.App;

public class MainActivity extends BaseActivity {
    /**
     * 选中的控件
     */
    private static final int[] selView = {R.id.page1_0, R.id.page2_0, R.id.page3_0, R.id.page4_0};
    private static final int[] selTextView = {R.id.page1_tv, R.id.page2_tv, R.id.page3_tv, R.id.page4_tv};
    private static final long DELAY = 500;
    ViewPager viewPager;
    View page1, page2, page3, page4;
    RelativeLayout page1_rl, page2_rl, page3_rl, page4_rl;
    LinearLayout nav_main_collect, nav_main_notice, nav_main_setting,nav_main_lab,nav_main_fuli, nav_main_them, nav_main_advice, nav_main_pingjia, nav_main_debug,
            nav_main_about, nav_main_share, nav_main_logout;
    Toolbar toolbar, nav_toolbar;
    TextView header_name;
    ScrollView scrollView;
    HTextView toolbar_tv;
    CircleImageView header_image;
    DrawerLayout drawer;
    NavigationView navigationView;
    public static final int LOADING_DURATION = 2000;
    public boolean isClick = false;
    private FloatingActionButton new_note;
    private ArrayList<Fragment> fragmentList;
    private MenuItem refresh_item, new_item, more_item;

    private EmInviteMessgeDao emInviteMessgeDao;
    private EmUserDao emUserDao;

    private CircleImageView unreand_msg;
    private final String mPageName = "MainActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_tv = (HTextView) findViewById(R.id.toolbar_tv);

        setSupportActionBar(toolbar);
        //支持库的控件和自定义控件的换肤需要动态添加
        dynamicAddView(toolbar, "background", R.color.colorPrimaryDark);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        header_image = getViewById(R.id.header_image);
        new_note = getViewById(R.id.bottom_main_new_note);
        navigationView = getViewById(R.id.nav_view);

        scrollView = getViewById(R.id.scrollView);
        nav_main_collect = getViewById(R.id.nav_main_collect);
        nav_main_notice = getViewById(R.id.nav_main_notice);
        nav_main_setting = getViewById(R.id.nav_main_setting);
        nav_main_lab = getViewById(R.id.nav_main_lab);
        nav_main_fuli = getViewById(R.id.nav_main_fuli);
        nav_main_them = getViewById(R.id.nav_main_them);
        nav_main_advice = getViewById(R.id.nav_main_advice);
        nav_main_pingjia = getViewById(R.id.nav_main_pingjia);
        nav_main_debug = getViewById(R.id.nav_main_debug);
        nav_main_about = getViewById(R.id.nav_main_about);
        nav_main_share = getViewById(R.id.nav_main_share);
        nav_main_logout = getViewById(R.id.nav_main_logout);

        nav_toolbar = getViewById(R.id.nav_toolbar);
        header_name = getViewById(R.id.header_name);
        viewPager = getViewById(R.id.viewPager);

        page1_rl = (RelativeLayout) findViewById(R.id.page1_rl);
        page2_rl = (RelativeLayout) findViewById(R.id.page2_rl);
        page3_rl = (RelativeLayout) findViewById(R.id.page3_rl);
        page4_rl = (RelativeLayout) findViewById(R.id.page4_rl);
        page1 = findViewById(selView[0]);
        page2 = findViewById(selView[1]);
        page3 = findViewById(selView[2]);
        page4 = findViewById(selView[3]);

        fragmentList = new ArrayList<>();
        Fragment recyclerFragment = new MainFragment();
        Fragment contactsFragment = new FileTagFragment();
        Fragment musicFragment = new EmTalkFragment();
        Fragment shareFragment = new SharedFragment();

        fragmentList.add(recyclerFragment);
        fragmentList.add(contactsFragment);
        fragmentList.add(musicFragment);
        fragmentList.add(shareFragment);

        CommonUtil.unReadNumHandler = mHandler;
        unreand_msg = getViewById(R.id.unreand_msg);
    }

    @Override
    protected void setListener() {
        page1_rl.setOnClickListener(this);
        page2_rl.setOnClickListener(this);
        page3_rl.setOnClickListener(this);
        page4_rl.setOnClickListener(this);
        new_note.setOnClickListener(this);
        header_image.setOnClickListener(this);

        nav_main_collect.setOnClickListener(this);
        nav_main_notice.setOnClickListener(this);
        nav_main_setting.setOnClickListener(this);
        nav_main_lab.setOnClickListener(this);
        nav_main_fuli.setOnClickListener(this);
        nav_main_them.setOnClickListener(this);
        nav_main_advice.setOnClickListener(this);
        nav_main_pingjia.setOnClickListener(this);
        nav_main_debug.setOnClickListener(this);
        nav_main_about.setOnClickListener(this);
        nav_main_share.setOnClickListener(this);
        nav_main_logout.setOnClickListener(this);

        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);//设置当前显示标签页为第一页
        SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.CURVIEWPAGER, 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offY, int positionOffsetPixels) {
                if (!isClick) {
                    if (position == 0) {//1->0||1->0
                        ViewHelper.setAlpha(page1, 1 - offY);
                        ViewHelper.setAlpha(page2, offY);
                    } else if (position == 1) {//1->2||2->1
                        ViewHelper.setAlpha(page2, 1 - offY);
                        ViewHelper.setAlpha(page3, offY);
                    } else if (position == 2) {//2->3||3->2
                        ViewHelper.setAlpha(page3, 1 - offY);
                        ViewHelper.setAlpha(page4, offY);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < selView.length; i++) {
                    View view = findViewById(selView[i]);
                    TextView textView = (TextView) findViewById(selTextView[i]);
                    if (position == i) {//被选中的
                        ViewHelper.setAlpha(view, 1.0f);
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        dynamicAddView(textView, "textColor", R.color.colorAccent);
                    } else {//其他的
                        ViewHelper.setAlpha(view, 0.0f);
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    }
                }
                if (position == 0) {
                    refresh_item.setVisible(true);
                    new_item.setVisible(false);
                    more_item.setVisible(true);
                    toolbar_tv.animateText("个人笔记");
                } else if (position == 1) {
                    refresh_item.setVisible(true);
                    new_item.setVisible(true);
                    more_item.setVisible(true);
                    if ((int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.FUNCTION, 1) == 1) {
                        toolbar_tv.animateText("文 件 夹");
                    } else {
                        toolbar_tv.animateText("标    签");
                    }
                } else if (position == 2) {
                    refresh_item.setVisible(true);
                    new_item.setVisible(true);
                    more_item.setVisible(false);
                    if (!(Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
                        toolbar_tv.animateText("站内信(未登录)");
                    } else {
                        toolbar_tv.animateText("站 内 信");
                    }
                } else if (position == 3) {
                    refresh_item.setVisible(true);
                    new_item.setVisible(true);
                    more_item.setVisible(false);
                    toolbar_tv.animateText("跳转共享");
                }
                if (position != 3) {
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.CURVIEWPAGER, position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    isClick = false;
                }
            }
        });

        int minHeight = 110;
//        int minHeight = getResources().getDimensionPixelSize(R.dimen.margin_size_110dp);
        if (nav_toolbar.getHeight() != 0) {
            minHeight = (nav_toolbar.getHeight() * 2);
        }
        StikkyHeaderBuilder.stickTo(scrollView)
                .setHeader(R.id.header, navigationView)
                .minHeightHeader(DensityUtil.dip2px(getApplicationContext(), minHeight))
                .animator(animator)
                .build();
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

        if (Build.VERSION.SDK_INT >= 21) {
            // 设置退出的动画
            Fade fade = new Fade();
            fade.setDuration(300);
            getWindow().setExitTransition(fade);
        }

        toolbar_tv.setTextColor(Color.WHITE);

        String front_choose = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front_choose", "");
        if ("".equals(front_choose)) {
            SkinManager.getInstance().loadFont(null);
        } else {
            Typeface tf = AssetFileUtils.createTypeface(getApplicationContext(), front_choose);
            toolbar_tv.setTypeface(tf);
        }
        toolbar_tv.setAnimateType(HTextViewType.FALL);
        toolbar_tv.animateText("个人笔记");

        refreshUI();

        CommonUtil.mainLoginHandler = mHandler;

        emInviteMessgeDao = new EmInviteMessgeDao(getApplicationContext());
        emUserDao = new EmUserDao(getApplicationContext());
        //注册联系人变动监听
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
        if((boolean)SharedPreferencesUtils.getParam(getApplicationContext(),"allow_1024",false)){
            nav_main_fuli.setVisibility(View.VISIBLE);
        }else {
            nav_main_fuli.setVisibility(View.GONE);
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.mainLoginCode:
                    refreshUI();
                    ((MainFragment) fragmentList.get(0)).refreshGetData(true);
                    ((FileTagFragment) fragmentList.get(1)).initDatas();
                    break;
                case CommonUtil.logoutCode:
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.ISFIRST, true);
                    break;
                case CommonUtil.unReadNumCode:
                    if (msg.obj != null && Integer.parseInt(msg.obj.toString()) > 0) {
                        unreand_msg.setVisibility(View.VISIBLE);
                    } else {
                        unreand_msg.setVisibility(View.GONE);
                    }
                    break;
                case CommonUtil.readNumCode:
                    unreand_msg.setVisibility(View.GONE);
                    break;
                case CommonUtil.fuliCode:
                    if((boolean)SharedPreferencesUtils.getParam(getApplicationContext(),"allow_1024",false)){
                        nav_main_fuli.setVisibility(View.VISIBLE);
                    }else {
                        nav_main_fuli.setVisibility(View.GONE);
                    }
                    break;
                case CommonUtil.loadFrontAnimationsCode:
                    String front_animations = (String) msg.obj;
                    if ("SCALE".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.SCALE);
                    } else if ("EVAPORATE".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.EVAPORATE);
                    } else if ("FALL".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.FALL);
                    } else if ("ANVIL".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.ANVIL);
                    } else if ("LINE".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.LINE);
                    } else if ("TYPER".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.TYPER);
                    } else if ("RAINBOW".equals(front_animations)) {
                        toolbar_tv.setAnimateType(HTextViewType.RAINBOW);
                    }
                    break;
            }
        }
    };

    /**
     * 点击菜单里面的更多选项按钮
     *
     * @param view
     */
    public void MenuMoreClick(View view) {
        if (viewPager.getCurrentItem() == 0) {
            ((MainFragment) fragmentList.get(0)).MenuMoreClick();
        } else if (viewPager.getCurrentItem() == 1) {
            ((FileTagFragment) fragmentList.get(1)).MenuMoreClick();
        }
    }

    /**
     * 点击菜单里面的刷新选项按钮
     *
     * @param view
     */
    public void MenuRefreshClick(View view) {
        if (viewPager.getCurrentItem() == 0) {
            ((MainFragment) fragmentList.get(0)).refreshGetData(true);
        } else if (viewPager.getCurrentItem() == 1) {
            ((FileTagFragment) fragmentList.get(1)).refreshGetData();
        } else if (viewPager.getCurrentItem() == 2) {
            ((EmTalkFragment) fragmentList.get(2)).refreshGetData(MainActivity.this, false);
        }
    }

    /**
     * 点击菜单里面的新增选项按钮
     *
     * @param view
     */
    public void MenuNewClick(View view) {
        if (viewPager.getCurrentItem() == 0) {
            //do sth.
        } else if (viewPager.getCurrentItem() == 1) {
            if (!(Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
                ToastUtil.show("还未登录, 请先登录再进行操作");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                ((FileTagFragment) fragmentList.get(1)).newData(MainActivity.this);
            }
        } else if (viewPager.getCurrentItem() == 2) {
            ((EmTalkFragment) fragmentList.get(2)).MenuNewClick();
        }
    }

    public void refreshUI() {
        //设置用户
        header_name.setText(SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.NAME, "未登录").toString());
//        String imgUrl = "http://q.qlogo.cn/qqapp/1105942981/1325832F148D963EEFF3ACA2360B43C6/100";
        String imgUrl = SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.IMGURL, "").toString();
        if (!StringUtils.isEmpty2(imgUrl)) {
            Picasso.with(this).load(imgUrl).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(header_image);
            nav_main_logout.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(this).load(R.drawable.icon_user_img).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(header_image);
            nav_main_logout.setVisibility(View.GONE);
        }
        if (!(Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
            if (toolbar_tv != null && viewPager != null && viewPager.getCurrentItem() == 2) {
                toolbar_tv.animateText("站内信(未登录)");
                ((EmTalkFragment) fragmentList.get(2)).refreshGetData(MainActivity.this, false);
            }
        } else {
            if (toolbar_tv != null && viewPager != null && viewPager.getCurrentItem() == 2) {
                toolbar_tv.animateText("站 内 信");
                ((EmTalkFragment) fragmentList.get(2)).refreshGetData(MainActivity.this, false);
            }
        }
    }

    public void setCurViewPager() {
        int position = (int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.CURVIEWPAGER, 0);
        if (viewPager.getCurrentItem() != position) {
            viewPager.setCurrentItem(position);
        }
    }

    /**
     * 更改文本
     *
     * @param res
     */
    public void changeAnimateText(int res) {
        if (res == 1) {
            toolbar_tv.animateText("文 件 夹");
        } else {
            toolbar_tv.animateText("标    签");
        }
    }

    /***
     * 好友变化listener
     *
     */
    private class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(final String username) {
            // 保存增加的联系人
            Map<String, EmUser> localUsers = App.getInstance().getContactList();
            Map<String, EmUser> toAddUsers = new HashMap<String, EmUser>();
            EmUser user = new EmUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                emUserDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show("增加联系人：+" + username);
                }
            });
        }

        @Override
        public void onContactDeleted(final String username) {
            // 被删除
            Map<String, EmUser> localUsers = App.getInstance().getContactList();
            localUsers.remove(username);
            emUserDao.deleteContact(username);
            emInviteMessgeDao.deleteMessage(username);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show("删除联系人：+" + username);
                }
            });
        }

        @Override
        public void onContactInvited(final String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<EmInviteMessage> msgs = emInviteMessgeDao.getMessagesList();
            for (EmInviteMessage emInviteMessage : msgs) {
                if (emInviteMessage.getGroupId() == null && emInviteMessage.getFrom().equals(username)) {
                    emInviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            EmInviteMessage msg = new EmInviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show("收到好友申请：+" + username);
                }
            });
        }

        @Override
        public void onContactAgreed(final String username) {
            List<EmInviteMessage> msgs = emInviteMessgeDao.getMessagesList();
            for (EmInviteMessage emInviteMessage : msgs) {
                if (emInviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            EmInviteMessage msg = new EmInviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ToastUtil.show("好友申请同意：+" + username);
                }
            });
        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }
    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(EmInviteMessage msg) {
        if (emInviteMessgeDao == null) {
            emInviteMessgeDao = new EmInviteMessgeDao(getApplicationContext());
        }
        emInviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        emInviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
    }

    private BaseStickyHeaderAnimator animator = new HeaderStikkyAnimator() {
        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View imgViewToAnimate = getHeader().findViewById(R.id.header_image);
            View nameViewToAnimate = getHeader().findViewById(R.id.header_name);
            int right = (nav_toolbar.getWidth() / nav_toolbar.getHeight()) * (nav_toolbar.getHeight() / 3);
            int circle = (int) (nav_toolbar.getHeight() * 0.75);
            if (nameViewToAnimate.getWidth() != 0) {
                right = (nameViewToAnimate.getWidth() / nameViewToAnimate.getHeight()) * (nav_toolbar.getHeight() / 3);
            }
            final Rect imgSquareSizeToolbar = new Rect(0, 0, circle, circle);
            final Rect nameSquareSizeToolbar = new Rect(0, 0, right, nav_toolbar.getHeight() / 3);
            AnimatorBuilder animatorBuilder = AnimatorBuilder.create()
                    .applyScale(imgViewToAnimate, imgSquareSizeToolbar)
                    .applyScale(nameViewToAnimate, nameSquareSizeToolbar)
                    .applyTranslation(imgViewToAnimate, new Point(DensityUtil.dip2px(getApplicationContext(), 40), nav_toolbar.getHeight()))
                    .applyTranslation(nameViewToAnimate, new Point(nav_toolbar.getHeight() * 2, (int) (nav_toolbar.getHeight() * 1.25)));
            return animatorBuilder;
        }

    };

    /**
     * fragment 分页
     */
    private static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        refresh_item = menu.findItem(R.id.action_refresh);
        new_item = menu.findItem(R.id.action_new);
        more_item = menu.findItem(R.id.action_more);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.page1_rl:
                isClick = true;
                viewPager.setCurrentItem(0);
                break;
            case R.id.page2_rl:
                isClick = true;

                viewPager.setCurrentItem(1);
                break;
            case R.id.page3_rl:
                isClick = true;
                viewPager.setCurrentItem(2);
                break;
            case R.id.page4_rl:
                isClick = true;
                viewPager.setCurrentItem(3);
                break;
            case R.id.bottom_main_new_note:
                CommonUtil.map = null;
                //判断是否登录
                if ((Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
                    if (android.os.Build.VERSION.SDK_INT > 20) {
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, new_note, new_note.getTransitionName()).toBundle();
                        startActivityForResult(new Intent(MainActivity.this, NewNoteActivity.class), CommonUtil.addRequestCode, bundle);
                    } else {
                        startActivityForResult(new Intent(MainActivity.this, NewNoteActivity.class), CommonUtil.addRequestCode);
                    }
                } else {
                    ToastUtil.show("还未登录, 请先登录再进行操作");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                break;
            case R.id.header_image:
                //判断是否登录
                if ((Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
                    startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            case R.id.nav_main_collect://点击收藏
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, CollectActivity.class));
                    }
                }, DELAY);
                break;
            case R.id.nav_main_notice://点击通知
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, NoticeActivity.class));
                    }
                }, DELAY);
                break;
            case R.id.nav_main_setting://点击设置
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    }
                }, DELAY);
                break;
            case R.id.nav_main_lab://点击实验室
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, LabActivity.class));
                    }
                }, DELAY);
                break;
            case R.id.nav_main_fuli://点击福利
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, FuliActivity.class));
//                        ToastUtil.show("下一版即将推出，敬请期待");
                    }
                }, DELAY);
                break;
            case R.id.nav_main_them://点击主题
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(MainActivity.this, ThemeActivity.class));
                    }
                }, DELAY);
                break;
            case R.id.nav_main_advice://点击建议和反馈
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intentFeek = new Intent(MainActivity.this, FeekBackActivity.class);
                        startActivity(intentFeek);
                    }
                }, DELAY);
                break;
            case R.id.nav_main_pingjia://点击评价
                drawer.closeDrawer(GravityCompat.START);
                final String url = "market://details?id=" + this.getPackageName();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Uri uri = Uri.parse(url);
                        Intent intentPingjia = new Intent(Intent.ACTION_VIEW, uri);
                        intentPingjia.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentPingjia);
                    }
                }, DELAY);
                break;
            case R.id.nav_main_debug://查看调试信息
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intentDebug = new Intent(MainActivity.this, LogInfoActivity.class);
                        startActivity(intentDebug);
                    }
                }, DELAY);
                break;
            case R.id.nav_main_about://查看关于
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intentAbout = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(intentAbout);
                    }
                }, DELAY);
                break;
            case R.id.nav_main_share://查看分享
                drawer.closeDrawer(GravityCompat.START);
                final String apkUrl = " http://sj.qq.com/myapp/detail.htm?apkName=" + this.getPackageName();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // 从API11开始android推荐使用android.content.ClipboardManager
                        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText("");
                        LoginSharedUtil.shredAction(MainActivity.this, "这款应用最近火爆应用市场,快来体验下吧!", apkUrl);
                    }
                }, DELAY);
                break;
            case R.id.nav_main_logout://退出登录
                drawer.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        AlertDialog dialog = new AlertDialog(MainActivity.this);
                        dialog.builder().setMsg("是否退出登录?").setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).setPositiveButton("退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginActivity.logoff(MainActivity.this, SHARE_MEDIA.SINA);
                                CommonUtil.logoutHandler = mHandler;
                                SharedPreferencesUtils.delShared(getApplicationContext());//清空缓存
                                refreshUI();
                                ((MainFragment) fragmentList.get(0)).refreshGetData(true);
                                ((FileTagFragment) fragmentList.get(1)).initDatas();
                            }
                        }).show();
                    }
                }, DELAY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CommonUtil.addSucResultCode:
                ((MainFragment) fragmentList.get(0)).onBGARefreshLayoutBeginRefreshing(new BGARefreshLayout(getApplicationContext()));
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    /**
     * 退出事件
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.show("再按一次退出应用程序");
                exitTime = System.currentTimeMillis();
            } else {
                // 程序退出
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}