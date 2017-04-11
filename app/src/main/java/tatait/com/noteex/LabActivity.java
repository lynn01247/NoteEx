package tatait.com.noteex;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import solid.ren.skinlibrary.SkinLoaderListener;
import solid.ren.skinlibrary.loader.SkinManager;
import solid.ren.skinlibrary.loader.TextViewRepository;
import tatait.com.noteex.util.AssetFileUtils;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;

/**
 * 主题
 */
public class LabActivity extends BaseActivity {
    private String[] htextviewType = {
            "SCALE",
            "EVAPORATE",
            "FALL",
            "ANVIL",
            "LINE",
            "TYPER",
            "RAINBOW"
    };
    private int[] selView = {R.id.lab_scale_tv, R.id.lab_evaporate_tv, R.id.lab_fall_tv,
            R.id.lab_anvil_tv, R.id.lab_line_tv, R.id.lab_typer_tv, R.id.lab_rainbow_tv};
    private TextView lab_scale_tv, lab_evaporate_tv, lab_fall_tv, lab_anvil_tv, lab_line_tv, lab_typer_tv, lab_rainbow_tv,
            front_tv, front1_tv, front2_tv, front3_tv;
    private LinearLayout title_layout_back;
    private String front1, front2, front3;
    private MaterialDialog dialog1;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_lab);
        title_layout_back = getViewById(R.id.title_layout_back);
        lab_scale_tv = getViewById(R.id.lab_scale_tv);
        lab_evaporate_tv = getViewById(R.id.lab_evaporate_tv);
        lab_fall_tv = getViewById(R.id.lab_fall_tv);
        lab_anvil_tv = getViewById(R.id.lab_anvil_tv);
        lab_line_tv = getViewById(R.id.lab_line_tv);
        lab_typer_tv = getViewById(R.id.lab_typer_tv);
        lab_rainbow_tv = getViewById(R.id.lab_rainbow_tv);
        front_tv = getViewById(R.id.front_tv);
        front1_tv = getViewById(R.id.front1_tv);
        front2_tv = getViewById(R.id.front2_tv);
        front3_tv = getViewById(R.id.front3_tv);
        JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getApplicationContext());
        try {
            front1 = json.getString("front1");
            front2 = json.getString("front2");
            front3 = json.getString("front3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog1 = new MaterialDialog.Builder(this)
                .title("更换字体中")
                .content("请耐心等待")
                .canceledOnTouchOutside(false)
                .progress(false, 100, true).build();
        CommonUtil.loadFrontHandler = mHandler;
    }

    @Override
    protected void setListener() {
        title_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        front_tv.setOnClickListener(this);
        front1_tv.setOnClickListener(this);
        front2_tv.setOnClickListener(this);
        front3_tv.setOnClickListener(this);
        lab_scale_tv.setOnClickListener(this);
        lab_evaporate_tv.setOnClickListener(this);
        lab_fall_tv.setOnClickListener(this);
        lab_anvil_tv.setOnClickListener(this);
        lab_line_tv.setOnClickListener(this);
        lab_typer_tv.setOnClickListener(this);
        lab_rainbow_tv.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        String frontPath = AssetFileUtils.getFrontDir(getApplicationContext());
        //字体一
        String front1_Name = front1.substring(front1.lastIndexOf("/") + 1);
        String front1_FullName = frontPath + File.separator + front1_Name;
        File front1_File = new File(front1_FullName);
        if (front1_File.exists()) {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front1", "1");
            front1_tv.setText("已下载");
            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front1", "0");
            front1_tv.setText("未下载");
            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }
        //字体二
        String front2_Name = front2.substring(front2.lastIndexOf("/") + 1);
        String front2_FullName = frontPath + File.separator + front2_Name;
        File front2_File = new File(front2_FullName);
        if (front2_File.exists()) {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front2", "1");
            front2_tv.setText("已下载");
            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front2", "0");
            front2_tv.setText("未下载");
            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }
        //字体三
        String front3_Name = front3.substring(front3.lastIndexOf("/") + 1);
        String front3_FullName = frontPath + File.separator + front3_Name;
        File front3_File = new File(front3_FullName);
        if (front3_File.exists()) {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front3", "1");
            front3_tv.setText("已下载");
            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
        } else {
            SharedPreferencesUtils.setParam(getApplicationContext(), "front3", "0");
            front3_tv.setText("未下载");
            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }

        String front_choose = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front_choose", "");
        String front1Name = front1.substring(front1.lastIndexOf("/") + 1);
        String front2Name = front2.substring(front2.lastIndexOf("/") + 1);
        String front3Name = front3.substring(front3.lastIndexOf("/") + 1);
        if ("".equals(front_choose)) {
            front_tv.setText("使用中");
            front_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        } else if (front1Name.equals(front_choose)) {
            front1_tv.setText("使用中");
            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        } else if (front2Name.equals(front_choose)) {
            front2_tv.setText("使用中");
            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        } else if (front3Name.equals(front_choose)) {
            front3_tv.setText("使用中");
            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
        }

        refreshUI(true, "");
    }

    public void loadFronFromUrl(String frontUrl, final SkinLoaderListener callback) {
        String frontPath = AssetFileUtils.getFrontDir(getApplicationContext());
        final String frontName = frontUrl.substring(frontUrl.lastIndexOf("/") + 1);
        String frontFullName = frontPath + File.separator + frontName;
        File frontFile = new File(frontFullName);
        if (frontFile.exists()) {
            callback.onSuccess();
            if (CommonUtil.loadFrontHandler != null) {
                Message message = new Message();
                message.what = CommonUtil.loadFrontCode;
                message.obj = frontName;
                CommonUtil.loadFrontHandler.sendMessage(message);
            }
            return;
        }

        Uri downloadUri = Uri.parse(frontUrl);
        Uri destinationUri = Uri.parse(frontFullName);

        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH);
        callback.onStart();
        downloadRequest.setStatusListener(new DownloadStatusListenerV1() {
            @Override
            public void onDownloadComplete(DownloadRequest downloadRequest) {
                callback.onSuccess();
                if (CommonUtil.loadFrontHandler != null) {
                    Message message = new Message();
                    message.what = CommonUtil.loadFrontCode;
                    message.obj = frontName;
                    CommonUtil.loadFrontHandler.sendMessage(message);
                }
            }

            @Override
            public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                callback.onFailed(errorMessage);
            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                callback.onProgress(progress);
            }
        });
        ThinDownloadManager manager = new ThinDownloadManager();
        manager.add(downloadRequest);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.front_tv:
                String front1_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front1", "0");
                String front2_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front2", "0");
                String front3_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front3", "0");
                if ("1".equals(front1_sp)) {
                    front1_tv.setText("已下载");
                    front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                } else {
                    front1_tv.setText("未下载");
                    front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                if ("1".equals(front2_sp)) {
                    front2_tv.setText("已下载");
                    front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                } else {
                    front2_tv.setText("未下载");
                    front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                if ("1".equals(front3_sp)) {
                    front3_tv.setText("已下载");
                    front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                } else {
                    front3_tv.setText("未下载");
                    front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
                front_tv.setText("使用中");
                front_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_choose", "");
                SkinManager.getInstance().loadFont(null);
                break;
            case R.id.front1_tv:
                loadFronFromUrl(front1, new SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("SkinLoaderListener", "字体正在切换中");
                        dialog1.setContent("正在从网络下载字体文件");
                        dialog1.show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("SkinLoaderListener", "字体切换成功");
                        front_tv.setText("未使用");
                        front1_tv.setText("使用中");
                        front_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

                        String front2_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front2", "0");
                        String front3_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front3", "0");
                        if ("1".equals(front2_sp)) {
                            front2_tv.setText("已下载");
                            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front2_tv.setText("未下载");
                            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        if ("1".equals(front3_sp)) {
                            front3_tv.setText("已下载");
                            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front3_tv.setText("未下载");
                            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        String frontName = front1.substring(front1.lastIndexOf("/") + 1);
                        SharedPreferencesUtils.setParam(getApplicationContext(), "front_choose", frontName);
                        dialog1.dismiss();
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("SkinLoaderListener", "字体切换失败:" + errMsg);
                        dialog1.setContent("更换字体失败:" + errMsg);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i("SkinLoaderListener", "字体文件下载中:" + progress);
                        dialog1.setProgress(progress);
                    }
                });
                break;
            case R.id.front2_tv:
                loadFronFromUrl(front2, new SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("SkinLoaderListener", "字体正在切换中");
                        dialog1.setContent("正在从网络下载字体文件");
                        dialog1.show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("SkinLoaderListener", "字体切换成功");
                        front_tv.setText("未使用");
                        front2_tv.setText("使用中");
                        front_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

                        String front1_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front1", "0");
                        String front3_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front3", "0");
                        if ("1".equals(front1_sp)) {
                            front1_tv.setText("已下载");
                            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front1_tv.setText("未下载");
                            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        if ("1".equals(front3_sp)) {
                            front3_tv.setText("已下载");
                            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front3_tv.setText("未下载");
                            front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        String frontName = front2.substring(front2.lastIndexOf("/") + 1);
                        SharedPreferencesUtils.setParam(getApplicationContext(), "front_choose", frontName);
                        dialog1.dismiss();
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("SkinLoaderListener", "字体切换失败:" + errMsg);
                        dialog1.setContent("更换字体失败:" + errMsg);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i("SkinLoaderListener", "字体文件下载中:" + progress);
                        dialog1.setProgress(progress);
                    }
                });
                break;
            case R.id.front3_tv:
                loadFronFromUrl(front3, new SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("SkinLoaderListener", "字体正在切换中");
                        dialog1.setContent("正在从网络下载字体文件");
                        dialog1.show();
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("SkinLoaderListener", "字体切换成功");
                        front_tv.setText("未使用");
                        front3_tv.setText("使用中");
                        front_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        front3_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));

                        String front1_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front1", "0");
                        String front2_sp = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front2", "0");
                        if ("1".equals(front1_sp)) {
                            front1_tv.setText("已下载");
                            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front1_tv.setText("未下载");
                            front1_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        if ("1".equals(front2_sp)) {
                            front2_tv.setText("已下载");
                            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
                        } else {
                            front2_tv.setText("未下载");
                            front2_tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        }
                        String frontName = front3.substring(front3.lastIndexOf("/") + 1);
                        SharedPreferencesUtils.setParam(getApplicationContext(), "front_choose", frontName);
                        dialog1.dismiss();
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("SkinLoaderListener", "字体切换失败:" + errMsg);
                        dialog1.setContent("更换字体失败:" + errMsg);
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.i("SkinLoaderListener", "字体文件下载中:" + progress);
                        dialog1.setProgress(progress);
                    }
                });
                break;
            case R.id.lab_scale_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "SCALE");
                refreshUI(false, "SCALE");
                break;
            case R.id.lab_evaporate_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "EVAPORATE");
                refreshUI(false, "EVAPORATE");
                break;
            case R.id.lab_fall_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "FALL");
                refreshUI(false, "FALL");
                break;
            case R.id.lab_anvil_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "ANVIL");
                refreshUI(false, "ANVIL");
                break;
            case R.id.lab_line_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "LINE");
                refreshUI(false, "LINE");
                break;
            case R.id.lab_typer_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "TYPER");
                refreshUI(false, "TYPER");
                break;
            case R.id.lab_rainbow_tv:
                SharedPreferencesUtils.setParam(getApplicationContext(), "front_animations", "RAINBOW");
                refreshUI(false, "RAINBOW");
                break;
        }
    }

    private void refreshUI(boolean init, String str) {
        if (!init && CommonUtil.mainLoginHandler != null) {
            Message message = new Message();
            message.what = CommonUtil.loadFrontAnimationsCode;
            message.obj = str;
            CommonUtil.mainLoginHandler.sendMessage(message);
        }
        String front_animations = (String) SharedPreferencesUtils.getParam(getApplicationContext(), "front_animations", "FALL");
        for (int i = 0; i < htextviewType.length; i++) {
            String curStr = htextviewType[i];
            TextView textView = (TextView) findViewById(selView[i]);
            if (curStr.equals(front_animations)) {//被选中的
                textView.setText("使用中");
                textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
            } else {//其他的
                textView.setText("未使用");
                textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_line));
            }
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.loadFrontCode:
                    String frontName = msg.obj.toString();
                    Typeface tf = AssetFileUtils.createTypeface(getApplicationContext(), frontName);
                    TextViewRepository.applyFont(tf);
                    break;
            }
        }
    };
}
