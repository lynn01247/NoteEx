package tatait.com.noteex.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tatait.com.noteex.EmChatActivity;
import tatait.com.noteex.EmNewFriendsMsgActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.model.GsonUserModel;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;
import tatait.com.noteex.widget.App;

public class EmTalkFragment extends BaseFragment {
    private ListView listView;
    private List<EMConversation> conversationList;
    private List<String> conversationidList;
    private String conversationid = "";
    private EaseConversationAdapater adapter;

    private static Context mContext;
    List<GsonUserModel.UserModel> listUser;
    private static long TALKTIME = 3000;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_em_conversation);
        listView = getViewById(R.id.listView);
        mContext = getActivity();
    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName().toUpperCase();
                String imgurl = "";
                String cickname = "";
                if (username.equals(App.getInstance().getCurrentUserName()))
                    ToastUtil.show(" 不能和自己聊天");
                else {
                    // 进入聊天页面
                    if (!listUser.isEmpty()) {
                        for (int i = 0; i < listUser.size(); i++) {
                            if (username.equals(listUser.get(i).emid)) {
                                cickname = listUser.get(i).name;
                                if (!StringUtils.isEmpty2(listUser.get(i).imgurl)) {
                                    imgurl = listUser.get(i).imgurl;
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(mContext, EmChatActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("cickname", cickname);
                    intent.putExtra("imgurl", imgurl);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        refreshGetData(mContext, false);
    }

    protected void onInvisibleToUser() {
        refreshGetData(mContext, true);
    }

    protected void onVisibleToUser() {
        refreshGetData(mContext, true);
    }

    /**
     * 点击刷新按钮
     */
    public void refreshGetData(Context context, boolean timeLimit) {
        long a = (long) SharedPreferencesUtils.getParam(mApp, CommonUtil.EMTALKTIME, new Date().getTime());
        long b = new Date().getTime();
        SharedPreferencesUtils.setParam(mApp, CommonUtil.EMTALKTIME, b);
        long interval = (b - a);
        if (interval < TALKTIME && timeLimit) {
            return;
        }
        conversationList = new ArrayList<>();
        conversationidList = new ArrayList<>();
        listUser = new ArrayList<>();

        if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
            if (listView != null) listView.setVisibility(View.GONE);
        } else {
            if (listView != null) listView.setVisibility(View.VISIBLE);
        }
        mContext = context;

        if (mContext == null) mContext = getActivity();

        conversationList.addAll(loadConversationList());
        getConversationUserData();
    }


    /**
     * 点击更多按钮
     */
    public void MenuNewClick() {
        startActivity(new Intent(getContext(), EmNewFriendsMsgActivity.class));
    }

    private void getConversationUserData() {
        CommonUtil.conversationUserDataHandler = mHandler;
//        showLoadingDialog();
        String emid = App.getInstance().getCurrentUserName();
        if (!StringUtils.isEmpty2(conversationid)) {
            emid = conversationid.substring(0, conversationid.length() - 1);
        }
        String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_CONVERSATION_USER + "?emid=" + emid;
        OkHttpClientManager.getAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<GsonUserModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
//                dismissLoadingDialog();
            }

            @Override
            public void onResponse(GsonUserModel gsonModel) {
//                dismissLoadingDialog();
                if (gsonModel != null) {
//                    ToastUtil.show(gsonModel.getInfo());
                    if (Integer.parseInt(gsonModel.getCode()) == 1) {
                        if (CommonUtil.conversationUserDataHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.conversationUserDataCode;
                            message.obj = gsonModel.getData();
                            CommonUtil.conversationUserDataHandler.sendMessage(message);
                        }
                    } else {
//                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.conversationUserDataCode:
                    listUser = (List<GsonUserModel.UserModel>) msg.obj;
                    adapter = new EaseConversationAdapater(mContext, 1, conversationList, listUser);
                    if (listView == null) {
                        listView = getViewById(R.id.listView);
                    }
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 获取会话列表
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
            for (String str : conversations.keySet()) {
                conversationidList.add(str);
                conversationid += str + ",";
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    private class EaseConversationAdapater extends ArrayAdapter<EMConversation> {
        private List<EMConversation> conversationList;
        private List<EMConversation> copyConversationList;
        private List<GsonUserModel.UserModel> listUser;
        private boolean notiyfyByFilter;
        private int primaryColor;
        private int secondaryColor;
        private int timeColor;
        private int primarySize;
        private int secondarySize;
        private float timeSize;

        private EaseConversationAdapater(Context context, int resource, List<EMConversation> objects, List<GsonUserModel.UserModel> list) {
            super(context, resource, objects);
            conversationList = objects;
            listUser = list;
            copyConversationList = new ArrayList<>();
            copyConversationList.addAll(objects);
        }

        @Override
        public int getCount() {
            return conversationList.size();
        }

        @Override
        public EMConversation getItem(int arg0) {
            if (arg0 < conversationList.size()) {
                return conversationList.get(arg0);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_em_conversation, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
                holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
                holder.message = (TextView) convertView.findViewById(R.id.message);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.msgState = convertView.findViewById(R.id.msg_state);
                convertView.setTag(holder);
            }
            // 获取与此用户/群组的会话
            EMConversation conversation = getItem(position);
            // 获取用户username或者群组groupid
            String username = conversation.getUserName().toUpperCase();
            if (!listUser.isEmpty()) {
                for (int i = 0; i < listUser.size(); i++) {
                    if (username.equals(listUser.get(i).emid)) {
                        holder.name.setText("与 " + listUser.get(i).name + " 的会话");
                        if (StringUtils.isEmpty2(listUser.get(i).imgurl)) {
                            Picasso.with(mContext).load(R.drawable.em_default_avatar).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                        } else {
                            Picasso.with(mContext).load(listUser.get(i).imgurl).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.avatar);
                        }
                    }
                }
            } else {
                holder.name.setText("与 " + username + " 的会话");
            }
            if (conversation.getUnreadMsgCount() > 0) {
                // 显示与此用户的消息未读数
                holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
                holder.unreadLabel.setVisibility(View.VISIBLE);
                if (CommonUtil.unReadNumHandler != null) {//通知首页
                    Message message = new Message();
                    message.what = CommonUtil.unReadNumCode;
                    message.obj = String.valueOf(conversation.getUnreadMsgCount());
                    CommonUtil.unReadNumHandler.sendMessage(message);
                }
            } else {
                if (CommonUtil.unReadNumHandler != null) {//通知首页
                    Message message = new Message();
                    message.what = CommonUtil.readNumCode;
                    CommonUtil.unReadNumHandler.sendMessage(message);
                }
                holder.unreadLabel.setVisibility(View.INVISIBLE);
            }
            if (conversation.getAllMsgCount() != 0) {
                // 把最后一条消息的内容作为item的message内容
                EMMessage lastMessage = conversation.getLastMessage();
                holder.message.setText(lastMessage.getBody().toString());
                holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                    holder.msgState.setVisibility(View.VISIBLE);
                } else {
                    holder.msgState.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (!notiyfyByFilter) {
                copyConversationList.clear();
                copyConversationList.addAll(conversationList);
                notiyfyByFilter = false;
            }
        }

        public void setPrimaryColor(int primaryColor) {
            this.primaryColor = primaryColor;
        }

        public void setSecondaryColor(int secondaryColor) {
            this.secondaryColor = secondaryColor;
        }

        public void setTimeColor(int timeColor) {
            this.timeColor = timeColor;
        }

        public void setPrimarySize(int primarySize) {
            this.primarySize = primarySize;
        }

        public void setSecondarySize(int secondarySize) {
            this.secondarySize = secondarySize;
        }

        public void setTimeSize(float timeSize) {
            this.timeSize = timeSize;
        }
    }

    private static class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView name;
        /**
         * 头像
         */
        CircleImageView avatar;
        /**
         * 消息未读数
         */
        TextView unreadLabel;
        /**
         * 最后一条消息的内容
         */
        TextView message;
        /**
         * 最后一条消息的时间
         */
        TextView time;
        /**
         * 最后一条消息的发送状态
         */
        View msgState;
        /** 整个list中每一行总布局 */
    }
}