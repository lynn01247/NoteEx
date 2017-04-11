package tatait.com.noteex;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import solid.ren.skinlibrary.SkinLoaderListener;
import solid.ren.skinlibrary.loader.SkinManager;
import solid.ren.skinlibrary.utils.SkinFileUtils;
import tatait.com.noteex.util.SharedPreferencesUtils;

/**
 * 主题
 */
public class ThemeActivity extends BaseActivity {
    private TextView theme_default_tv, theme_red_tv, theme_gray_tv;
    private LinearLayout title_layout_back;
    private String theme_red, theme_gray;
    private MaterialDialog dialog;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_theme);
        title_layout_back = getViewById(R.id.title_layout_back);
        theme_default_tv = getViewById(R.id.theme_default_tv);
        theme_red_tv = getViewById(R.id.theme_red_tv);
        theme_gray_tv = getViewById(R.id.theme_gray_tv);
        JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getApplicationContext());
        try {
            theme_red = json.getString("theme_red");
            theme_gray = json.getString("theme_gray");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = new MaterialDialog.Builder(this)
                .title("换肤中")
                .content("请耐心等待")
                .canceledOnTouchOutside(false)
                .progress(false, 100, true).build();
    }

    @Override
    protected void setListener() {
        title_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        theme_default_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theme_red_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "theme_red", "0");
                String theme_gray_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "theme_gray", "0");
                if ("1".equals(theme_red_sp)) {
                    theme_red_tv.setText("已下载");
                    theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                } else {
                    theme_red_tv.setText("未下载");
                    theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                if ("1".equals(theme_gray_sp)) {
                    theme_gray_tv.setText("已下载");
                    theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                } else {
                    theme_gray_tv.setText("未下载");
                    theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                theme_default_tv.setText("使用中");
                theme_default_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                SharedPreferencesUtils.setParam(getApplicationContext(), "theme_choose", "0");
                SkinManager.getInstance().restoreDefaultTheme();
            }
        });
        theme_red_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkinManager.getInstance().loadSkinFromUrl(theme_red, new SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("SkinLoaderListener", "正在切换中");
                        dialog.setContent("正在从网络下载皮肤文件");
                        dialog.show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("SkinLoaderListener", "切换成功");
                        theme_default_tv.setText("未使用");
                        theme_red_tv.setText("使用中");
                        theme_default_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

                        String theme_gray_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "theme_gray", "0");
                        if ("1".equals(theme_gray_sp)) {
                            theme_gray_tv.setText("已下载");
                            theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            theme_gray_tv.setText("未下载");
                            theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        SharedPreferencesUtils.setParam(getApplicationContext(), "theme_choose", "1");
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("SkinLoaderListener", "切换失败:" + errMsg);
                        dialog.setContent("换肤失败:" + errMsg);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i("SkinLoaderListener", "皮肤文件下载中:" + progress);
                        dialog.setProgress(progress);
                    }
                });
            }
        });
        theme_gray_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkinManager.getInstance().loadSkinFromUrl(theme_gray, new SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("SkinLoaderListener", "正在切换中");
                        dialog.setContent("正在从网络下载皮肤文件");
                        dialog.show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("SkinLoaderListener", "切换成功");
                        theme_default_tv.setText("未使用");
                        theme_gray_tv.setText("使用中");
                        theme_default_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

                        String theme_red_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "theme_red", "0");
                        if ("1".equals(theme_red_sp)) {
                            theme_red_tv.setText("已下载");
                            theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            theme_red_tv.setText("未下载");
                            theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        SharedPreferencesUtils.setParam(getApplicationContext(), "theme_choose", "2");
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("SkinLoaderListener", "切换失败:" + errMsg);
                        dialog.setContent("换肤失败:" + errMsg);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i("SkinLoaderListener", "皮肤文件下载中:" + progress);
                        dialog.setProgress(progress);
                    }
                });
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        String skinPath = SkinFileUtils.getSkinDir(getApplicationContext());
        //红色主题
        String skin_red_Name = theme_red.substring(theme_red.lastIndexOf("/") + 1);
        String skin_red_FullName = skinPath + File.separator + skin_red_Name;
        File skin_red_File = new File(skin_red_FullName);
        if (skin_red_File.exists()) {
            SharedPreferencesUtils.setParam(getApplicationContext(), "theme_red", "1");
            theme_red_tv.setText("已下载");
            theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), "theme_red", "0");
            theme_red_tv.setText("未下载");
            theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }
        //灰色主题
        String skin_gray_Name = theme_gray.substring(theme_gray.lastIndexOf("/") + 1);
        String skin_gray_FullName = skinPath + File.separator + skin_gray_Name;
        File skin_gray_File = new File(skin_gray_FullName);
        if (skin_gray_File.exists()) {
            SharedPreferencesUtils.setParam(getApplicationContext(), "theme_gray", "1");
            theme_gray_tv.setText("已下载");
            theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), "theme_gray", "0");
            theme_gray_tv.setText("未下载");
            theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }

        //初始化状态
        String theme_choose = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "theme_choose", "0");
        if ("0".equals(theme_choose)) {
            theme_default_tv.setText("使用中");
            theme_default_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        } else if ("1".equals(theme_choose)) {
            theme_red_tv.setText("使用中");
            theme_red_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        } else if ("2".equals(theme_choose)) {
            theme_gray_tv.setText("使用中");
            theme_gray_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        }
    }
}
