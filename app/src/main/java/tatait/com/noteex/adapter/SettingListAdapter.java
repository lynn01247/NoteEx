package tatait.com.noteex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import tatait.com.noteex.R;
import tatait.com.noteex.util.CircleImageView;

/**
 * 创建时间:15/5/21 上午12:39
 * 描述:
 */
public class SettingListAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public SettingListAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public final class Zujian {
        public CircleImageView image;
        public TextView title;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian = null;
        if (convertView == null) {
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.item_setting_list, null);
            zujian.image = (CircleImageView) convertView.findViewById(R.id.nav_main_setting_img);
            zujian.title = (TextView) convertView.findViewById(R.id.nav_main_setting_title);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据
        zujian.image.setBackgroundResource((Integer) data.get(position).get("image"));
        zujian.title.setText((String) data.get(position).get("title"));
        return convertView;
    }
}