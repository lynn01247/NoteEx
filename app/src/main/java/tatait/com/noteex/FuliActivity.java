package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import tatait.com.noteex.model.Constants;
import tatait.com.noteex.util.SharedPreferencesUtils;

/**
 * 福利
 */
public class FuliActivity extends BaseActivity {
    private LinearLayout title_layout_back,bannerContainer_ll;
    private TextView wenxue_tuijian, wenxue_jingpin, wenxue_new, pic_tuijian, pic_jingpin, pic_new, link_tuijian, link_jingpin, link_new;
    private ViewGroup bannerContainer;
    BannerView bv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fuli);
        wenxue_tuijian = getViewById(R.id.activity_fuli_wenxue_tuijian);
        wenxue_jingpin = getViewById(R.id.activity_fuli_wenxue_jingpin);
        wenxue_new = getViewById(R.id.activity_fuli_wenxue_new);
        pic_tuijian = getViewById(R.id.activity_fuli_pic_tuijian);
        pic_jingpin = getViewById(R.id.activity_fuli_pic_jingpin);
        pic_new = getViewById(R.id.activity_fuli_pic_new);
        link_tuijian = getViewById(R.id.activity_fuli_link_tuijian);
        link_jingpin = getViewById(R.id.activity_fuli_link_jingpin);
        link_new = getViewById(R.id.activity_fuli_link_new);
        title_layout_back = getViewById(R.id.title_layout_back);
        bannerContainer = getViewById(R.id.bannerContainer);
        bannerContainer_ll = getViewById(R.id.bannerContainer_ll);
    }

    @Override
    protected void setListener() {
        title_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wenxue_tuijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "fiction");
                tuijianIntent.putExtra("type", "good");
                startActivity(tuijianIntent);
            }
        });
        wenxue_jingpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "fiction");
                tuijianIntent.putExtra("type", "best");
                startActivity(tuijianIntent);
            }
        });
        wenxue_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "fiction");
                tuijianIntent.putExtra("type", "new");
                startActivity(tuijianIntent);
            }
        });
        pic_tuijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "pic");
                tuijianIntent.putExtra("type", "good_pic");
                startActivity(tuijianIntent);
            }
        });
        pic_jingpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "pic");
                tuijianIntent.putExtra("type", "best_pic");
                startActivity(tuijianIntent);
            }
        });
        pic_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tuijianIntent = new Intent(FuliActivity.this, ReadActivity.class);
                tuijianIntent.putExtra("category", "pic");
                tuijianIntent.putExtra("type", "new_pic");
                startActivity(tuijianIntent);
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        boolean isVip = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(), "isVip", false);
        if (!isVip) {
            this.bv = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
            // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
            // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
            bv.setRefresh(30);
            bv.setADListener(new AbstractBannerADListener() {

                @Override
                public void onNoAD(int arg0) {
                    Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
                }

                @Override
                public void onADReceiv() {
                    Log.i("AD_DEMO", "ONBannerReceive");
                }
            });
            bannerContainer.addView(bv);
            this.bv.loadAD();
            bannerContainer_ll.setVisibility(View.VISIBLE);
        }else{
            bannerContainer_ll.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerContainer.removeAllViews();
        if (bv != null) {
            bv.destroy();
            bv = null;
        }
    }
}
