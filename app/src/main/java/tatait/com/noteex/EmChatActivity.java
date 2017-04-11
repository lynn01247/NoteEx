package tatait.com.noteex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.squareup.picasso.Picasso;

import java.util.List;

import tatait.com.noteex.model.EmConstant;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.EmCommonUtils;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;

public class EmChatActivity extends BaseActivity {
    private ListView listView;
    private int chatType = 1;
    private String toChatUsername, toChatimgurl, toChatcickname;
    private Button btn_send;
    private EditText et_content;
    private List<EMMessage> msgList;
    MessageAdapter adapter;
    private EMConversation conversation;
    protected int pagesize = 20;

    private Context mContext;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_em_chat);
        mContext = this;
        toChatUsername = this.getIntent().getStringExtra("username");
        toChatcickname = this.getIntent().getStringExtra("cickname");
        toChatimgurl = this.getIntent().getStringExtra("imgurl");
        LinearLayout title_layout_back = (LinearLayout) this.findViewById(R.id.title_layout_back);
        TextView tv_toUsername = (TextView) this.findViewById(R.id.tv_toUsername);
        tv_toUsername.setText(toChatcickname);
        listView = (ListView) this.findViewById(R.id.listView);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);
        getAllMessage();
        msgList = conversation.getAllMessages();
        adapter = new MessageAdapter(msgList, mContext);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                setMesaage(content);
            }

        });
        title_layout_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    protected void getAllMessage() {
        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EmCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    private void setMesaage(String content) {

        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == EmConstant.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        msgList.add(message);

        adapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
        et_content.setText("");
        et_content.clearFocus();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);
                    adapter.notifyDataSetChanged();
                    if (msgList.size() > 0) {
                        et_content.setSelection(listView.getCount() - 1);

                    }

                }
            }

            // 收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @SuppressLint("InflateParams")
    private class MessageAdapter extends BaseAdapter {
        private List<EMMessage> msgs;
        private LayoutInflater inflater;

        private MessageAdapter(List<EMMessage> msgs, Context context_) {
            this.msgs = msgs;
            inflater = LayoutInflater.from(context_);
        }

        @Override
        public int getCount() {
            return msgs.size();
        }

        @Override
        public EMMessage getItem(int position) {
            return msgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            EMMessage message = getItem(position);
            return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EMMessage message = getItem(position);
            int viewType = getItemViewType(position);
            if (convertView == null) {
                if (viewType == 0) {
                    convertView = inflater.inflate(R.layout.item_em_message_received, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.item_em_message_sent, parent, false);
                }
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                holder.avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
                if (viewType == 0) {
                    if (StringUtils.isEmpty2(toChatimgurl)) {
                        Picasso.with(mContext).load(R.drawable.em_default_avatar).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                    } else {
                        Picasso.with(mContext).load(toChatimgurl).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                    }
                } else {
                    if (StringUtils.isEmpty2(SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.IMGURL, "").toString())) {
                        Picasso.with(mContext).load(R.drawable.em_default_avatar).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                    } else {
                        Picasso.with(mContext).load(SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.IMGURL, "").toString()).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                    }
                }
                convertView.setTag(holder);
            }

            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            holder.tv.setText(txtBody.getMessage());
            return convertView;
        }

    }

    private static class ViewHolder {
        TextView tv;
        ImageView avatar;
    }
}
