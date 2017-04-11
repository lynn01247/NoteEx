package tatait.com.noteex.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.picasso.Picasso;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import tatait.com.noteex.R;
import tatait.com.noteex.model.GsonNoteModel;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.FontManager;
import tatait.com.noteex.util.RippleView;
import tatait.com.noteex.util.StringUtils;

/**
 * 创建时间:15/5/21 上午12:39
 * 描述:
 */
public class MainFragmentAdapter extends BGARecyclerViewAdapter<GsonNoteModel.NoteModel> {
    public MainFragmentAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_fragment_list);
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper, int viewType) {
        RippleView rippleview = viewHolderHelper.getView(R.id.tv_item_fragment_list_ripple);
        rippleview.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (CommonUtil.itemClickHandler != null) {
                    Message message = new Message();
                    message.what = CommonUtil.itemClickCode;
                    CommonUtil.itemClickHandler.sendMessage(message);
                }
            }
        });
        viewHolderHelper.setItemChildClickListener(R.id.tv_item_normal_delete);
        viewHolderHelper.setItemChildLongClickListener(R.id.tv_item_normal_delete);
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, GsonNoteModel.NoteModel model) {
        CircleImageView user_img = viewHolderHelper.getView(R.id.item_fragment_list_user_img);
        if (StringUtils.isEmpty2(model.imgurl)) {
            Picasso.with(mContext).load(R.drawable.icon_user_img).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
        } else {
            Picasso.with(mContext).load(model.imgurl).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(user_img);
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
        viewHolderHelper.setText(R.id.tv_item_fragment_list_title, model.title)
                .setText(R.id.tv_item_fragment_list_time, model.updatedTime)
                .setText(R.id.tv_item_fragment_list_category, model.category)
                .setText(R.id.item_fragment_list_user_name, model.name)
                .setText(R.id.tv_item_fragment_list_detail, model.content)
                .setText(R.id.view_num, model.viewsNum)
                .setText(R.id.collect_num, model.collectNum)
                .setText(R.id.praise_num, model.praisedNum);
        FontManager.setDefaultFont(mContext);//由于是读取SD卡 需要重新设置默认字体
    }
}