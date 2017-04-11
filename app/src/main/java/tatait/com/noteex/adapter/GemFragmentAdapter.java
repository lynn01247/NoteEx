package tatait.com.noteex.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.picasso.Picasso;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import tatait.com.noteex.R;
import tatait.com.noteex.model.GsonNoteModel;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.FontManager;
import tatait.com.noteex.util.StringUtils;

/**
 * 创建时间:15/5/21 上午12:39
 * 描述:
 */
public class GemFragmentAdapter extends BGARecyclerViewAdapter<GsonNoteModel.NoteModel> {
    public GemFragmentAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_fragment_note);
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        viewHolderHelper.setItemChildClickListener(R.id.item_fragment_note_user_img);
        viewHolderHelper.setItemChildClickListener(R.id.item_fragment_note_user_name);
        viewHolderHelper.setItemChildClickListener(R.id.item_fragment_note_user_rl);
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, GsonNoteModel.NoteModel model) {
        CircleImageView user_img = viewHolderHelper.getView(R.id.item_fragment_note_user_img);
        if ("JueJin".equals(model.from) && !StringUtils.isEmpty2(model.jj_user_img)) {//掘金干货
            Picasso.with(mContext).load(model.jj_user_img).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
        } else if("JueJin".equals(model.from)){
            Picasso.with(mContext).load(R.drawable.icon_user_img).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
        }else {
            if (StringUtils.isEmpty2(model.imgurl)) {
                Picasso.with(mContext).load(R.drawable.icon_user_img).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
            } else {
                Picasso.with(mContext).load(model.imgurl).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
            }
        }
        if ("true".equals(model.collected)) {
            viewHolderHelper.getView(R.id.item_fragment_note_collect).setVisibility(View.VISIBLE);
        } else {
            viewHolderHelper.getView(R.id.item_fragment_note_collect).setVisibility(View.GONE);
        }
        if (("new_shared".equals(model.type) || "best_shared".equals(model.type) || "gem_shared".equals(model.type))) {
            viewHolderHelper.getView(R.id.item_fragment_note_shared).setVisibility(View.VISIBLE);
        } else {
            viewHolderHelper.getView(R.id.item_fragment_note_shared).setVisibility(View.GONE);
        }
        viewHolderHelper.setText(R.id.tv_item_fragment_note_title, model.title)
                .setText(R.id.tv_item_fragment_note_time, model.updatedTime)
                .setText(R.id.tv_item_fragment_note_category, model.category)
                .setText(R.id.item_fragment_note_user_name, "JueJin".equals(model.from)?model.jj_userName:model.name)
                .setText(R.id.tv_item_fragment_note_detail, model.content)
                .setText(R.id.view_num, model.viewsNum)
                .setText(R.id.collect_num, model.collectNum)
                .setText(R.id.tv_item_fragment_note_originalUrl, model.jj_originalUrl)
                .setText(R.id.praise_num, model.praisedNum);
        FontManager.setDefaultFont(mContext);//由于是读取SD卡 需要重新设置默认字体
    }
}