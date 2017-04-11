package tatait.com.noteex;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tatait.com.noteex.util.CommonUtil;

public class AboutUsActivity extends BaseActivity implements
        OnClickListener {
    TextView aboutus_layout_version, aboutus_title;
    RelativeLayout aboutus_layout_rl;
    LinearLayout aboutus_layout_back;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_aboutus);
        aboutus_layout_version = (TextView) findViewById(R.id.aboutus_layout_version);
        aboutus_title = (TextView) findViewById(R.id.aboutus_title);
        aboutus_layout_rl = (RelativeLayout) findViewById(R.id.aboutus_layout_rl);
        aboutus_layout_back = (LinearLayout) findViewById(R.id.aboutus_layout_back);
    }

    @Override
    protected void setListener() {
        aboutus_layout_back.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        aboutus_layout_version.setText("NoteEx(超级笔记) Version：" + CommonUtil.getVersion(getApplicationContext()));
        aboutus_title.setText("关于我们");
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            //返回
            case R.id.aboutus_layout_back:
                finish();
                break;
        }
    }
}