package tatait.com.noteex.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tatait.com.noteex.PicDetailActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.ReadDetailActivity;
import tatait.com.noteex.model.GsonFictionModel;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ViewHolder;

public class MissionAdapter extends BaseAdapter {
    private ArrayList<GsonFictionModel.Fiction> listAttentionData;
    private Context mContext;

    public MissionAdapter(Context mContext, ArrayList<GsonFictionModel.Fiction> listAttentionData) {
        this.mContext = mContext;
        this.listAttentionData = listAttentionData;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) mContext).getLayoutInflater().inflate(
                    R.layout.item_mission_layout, null);
        }
        RelativeLayout item_mission_layout_rl = ViewHolder.get(convertView, R.id.item_mission_layout_rl);
        final TextView time = ViewHolder.get(convertView, R.id.item_mission_layout_time);
        final TextView title = ViewHolder.get(convertView, R.id.item_mission_layout_title);
        final TextView content = ViewHolder.get(convertView, R.id.item_mission_layout_content);

        final GsonFictionModel.Fiction result = listAttentionData.get(position);

        final String typeStr = result.getType();
        time.setText(StringUtils.getObjString(result.getUpdatedTime()));
        title.setText(StringUtils.getObjString(result.getTitle()));
        content.setText(StringUtils.getObjString(result.getContent()));
        item_mission_layout_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeStr.contains("_pic")){
                    Intent intent = new Intent(mContext,PicDetailActivity.class);
                    intent.putExtra("title",title.getText().toString());
                    intent.putExtra("content",content.getText().toString());
                    intent.putExtra("time",time.getText().toString());
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext,ReadDetailActivity.class);
                    intent.putExtra("title",title.getText().toString());
                    intent.putExtra("content",content.getText().toString());
                    intent.putExtra("time",time.getText().toString());
                    mContext.startActivity(intent);
                }

            }
        });
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listAttentionData.get(position);
    }

    @Override
    public int getCount() {
        return listAttentionData.size();
    }
}