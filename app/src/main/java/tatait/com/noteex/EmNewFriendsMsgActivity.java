package tatait.com.noteex;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import tatait.com.noteex.adapter.EmNewFriendsMsgAdapter;
import tatait.com.noteex.db.EmInviteMessage;
import tatait.com.noteex.db.EmInviteMessgeDao;

/**
 * 申请与通知
 */
public class EmNewFriendsMsgActivity extends BaseActivity {
    private ListView listView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_em_new_friends_msg);
        listView = (ListView) findViewById(R.id.list);
        LinearLayout title_layout_back = (LinearLayout) this.findViewById(R.id.title_layout_back);
        EmInviteMessgeDao dao = new EmInviteMessgeDao(this);
        List<EmInviteMessage> msgs = dao.getMessagesList();
        EmNewFriendsMsgAdapter adapter = new EmNewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
        title_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    public void back(View view) {
        finish();
    }
}
