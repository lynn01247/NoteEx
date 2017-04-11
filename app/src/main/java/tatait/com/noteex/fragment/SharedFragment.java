package tatait.com.noteex.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import tatait.com.noteex.MainActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.ViewPagerActivity;
import tatait.com.noteex.util.CommonUtil;

public class SharedFragment extends BaseFragment {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_list);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    protected void onVisibleToUser() {
        CommonUtil.setCurViewPagerHandler = mHandler;
        showLoadingDialog();
        startActivity(new Intent(getContext(), ViewPagerActivity.class));
        super.onVisibleToUser();
    }

    @Override
    protected void onInvisibleToUser() {
        dismissLoadingDialog();
        super.onInvisibleToUser();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.setCurViewPagerCode:
                    ((MainActivity) getActivity()).setCurViewPager();
                    break;
            }
        }
    };
}