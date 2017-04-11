package tatait.com.noteex.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;

import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tatait.com.noteex.LoginActivity;
import tatait.com.noteex.MainActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;

/**
 * Created by Lynn on 03/27/2017.
 */
public class SettingFragment extends PreferenceFragment {

    CheckBoxPreference pre_1024;
    EditTextPreference vip_txt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final PushAgent mPushAgent = PushAgent.getInstance(getActivity().getApplicationContext());
        final CheckBoxPreference push_notice_pref = (CheckBoxPreference) getPreferenceManager().findPreference("push_notice");
        final CheckBoxPreference not_disturb_pref = (CheckBoxPreference) getPreferenceManager().findPreference("not_disturb");
        final ListPreference disturb_time_pref = (ListPreference) getPreferenceManager().findPreference("disturb_time");
        pre_1024 = (CheckBoxPreference) getPreferenceManager().findPreference("pre_1024");
        vip_txt = (EditTextPreference) getPreferenceManager().findPreference("vip_txt");

        CommonUtil.onettwozerofourHandler = mHandler;
        push_notice_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = Boolean.valueOf(newValue.toString());
                SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"push_notice",checked);
                if(checked) {
                    push_notice_pref.setSummary("向客户端推送系统通知--当前已开启\n开启或关闭该设置大约需要5分钟才能生效");
                    if (CommonUtil.onPushHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.onPushCode;
                        CommonUtil.onPushHandler.sendMessage(message);
                    }
                }else{
                    push_notice_pref.setSummary("向客户端推送系统通知\n开启或关闭该设置大约需要5分钟才能生效");
                    if (CommonUtil.onPushHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.offPushCode;
                        CommonUtil.onPushHandler.sendMessage(message);
                    }
                }
                return true;

            }
        });

        not_disturb_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = Boolean.valueOf(newValue.toString());
                SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"not_disturb",checked);
                if(checked) {
                    not_disturb_pref.setSummary("免打扰模式已开启");
                    disturb_time_pref.setSummary("默认时间段：23:00--7:00");
                    mPushAgent.setNoDisturbMode(23, 0, 7, 0);
                }else{
                    not_disturb_pref.setSummary("免打扰模式下：\n收到通知消息时不响铃，不振动，不闪灯");
                    disturb_time_pref.setSummary("开启免打扰模式的时候生效");
                    mPushAgent.setNoDisturbMode(0, 0, 0, 0);
                }
                return true;
            }
        });
        disturb_time_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!(boolean)SharedPreferencesUtils.getParam(getActivity().getApplicationContext(),"not_disturb",false)){
                    ToastUtil.show("您还未开启免打扰模式哦，设置的值不会生效！");
                    return true;
                }
                if(newValue.equals("1")){
                    SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"disturb_time","1");
                    mPushAgent.setNoDisturbMode(22, 0, 7, 0);
                    disturb_time_pref.setSummary("时间段：22:00--7:00");
                }else if(newValue.equals("2")){
                    SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"disturb_time","2");
                    mPushAgent.setNoDisturbMode(22, 0, 8, 0);
                    disturb_time_pref.setSummary("时间段：22:00--8:00");
                }else if(newValue.equals("3")){
                    SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"disturb_time","3");
                    mPushAgent.setNoDisturbMode(23, 0, 7, 0);
                    disturb_time_pref.setSummary("时间段：23:00--7:00");
                }else if(newValue.equals("4")){
                    SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"disturb_time","4");
                    mPushAgent.setNoDisturbMode(23, 0, 8, 0);
                    disturb_time_pref.setSummary("时间段：23:00--8:00");
                }
                return true;
            }
        });

        pre_1024.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = Boolean.valueOf(newValue.toString());
                SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"pre_1024",checked);
                if(checked) {
                    JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getActivity().getApplicationContext());
                    boolean value = false;
                    try {
                        value = json.getBoolean("1024");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(!value){
                        if (CommonUtil.onettwozerofourHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.onettwozerofourCode;
                            CommonUtil.onettwozerofourHandler.sendMessage(message);
                        }
                        ToastUtil.show("服务器或应用升级期间，入口暂时无法访问. 请联系QQ: 2443938389 询问详情");
                        SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"allow_1024",false);
                    }else{
                        ToastUtil.show("当前入口已开启，请返回主界面的侧边栏通道进入");
                        SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"allow_1024",true);
                    }
                    vip_txt.setEnabled(true);
                }else{
                    vip_txt.setEnabled(false);
                    ToastUtil.show("当前入口已关闭");
                    SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"allow_1024",false);
                }
                if (CommonUtil.mainLoginHandler != null) {
                    Message message = new Message();
                    message.what = CommonUtil.fuliCode;
                    CommonUtil.mainLoginHandler.sendMessage(message);
                }
                return true;
            }
        });
        vip_txt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean isVip = (boolean)SharedPreferencesUtils.getParam(getActivity().getApplicationContext(),"isVip",false);
                if(isVip){
                    AlertDialog dialog = new AlertDialog(getActivity());
                    dialog.builder().setMsg("重新填写账号可能会导致VIP权限失效，请谨慎修改").setNegativeButton("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
                return false;
            }
        });
        vip_txt.setEnabled(false);
        vip_txt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                vip_txt.setSummary(newValue.toString());
                JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getActivity().getApplicationContext());
                try {
                    JSONArray value = json.getJSONArray("vip");
                    for (int i = 0; i <value.length(); i++){
                        if(!StringUtils.isEmpty2(newValue.toString()) && newValue.toString().equals(value.get(i))){
                            SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"isVip",true);
                            SharedPreferencesUtils.setParam(getActivity().getApplicationContext(),"vip_num",newValue.toString());
                            vip_txt.setSummary(newValue.toString() + "  【vip 权限已激活,请勿重写VIP账号】");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        boolean isVip = (boolean)SharedPreferencesUtils.getParam(getActivity().getApplicationContext(),"isVip",false);
        if(isVip){
            String vip_num = (String) SharedPreferencesUtils.getParam(getActivity().getApplicationContext(),"vip_num","");
            vip_txt.setSummary(vip_num + "  【vip 权限已激活,请勿重写VIP账号】");
        }
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.onettwozerofourCode:
                    pre_1024.setChecked(false);
                    vip_txt.setEnabled(false);
                    break;
            }
        }
    };
}
