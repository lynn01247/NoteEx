package tatait.com.noteex.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import tatait.com.noteex.db.EmInviteMessage.InviteMesageStatus;
import tatait.com.noteex.model.EmConstant;
import tatait.com.noteex.util.EmCommonUtils;
import tatait.com.noteex.widget.App;

public class EmDBManager {
    static private EmDBManager dbMgr = new EmDBManager();
    private EmDbOpenHelper dbHelper;

    private EmDBManager() {
        dbHelper = EmDbOpenHelper.getInstance(App.getInstance().getApplicationContext());
    }

    public static synchronized EmDBManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new EmDBManager();
        }
        return dbMgr;
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<EmUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(EmUserDao.TABLE_NAME, null, null);
            for (EmUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(EmUserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null)
                    values.put(EmUserDao.COLUMN_NAME_NICK, user.getNick());
                if (user.getAvatar() != null)
                    values.put(EmUserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                db.replace(EmUserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取好友list
     *
     * @return
     */
    synchronized public Map<String, EmUser> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, EmUser> users = new Hashtable<String, EmUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + EmUserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(EmUserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(EmUserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(EmUserDao.COLUMN_NAME_AVATAR));
                EmUser user = new EmUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                if (username.equals(EmConstant.NEW_FRIENDS_USERNAME) || username.equals(EmConstant.GROUP_USERNAME)
                        || username.equals(EmConstant.CHAT_ROOM) || username.equals(EmConstant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    EmCommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除一个联系人
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(EmUserDao.TABLE_NAME, EmUserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    synchronized public void saveContact(EmUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EmUserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null)
            values.put(EmUserDao.COLUMN_NAME_NICK, user.getNick());
        if (user.getAvatar() != null)
            values.put(EmUserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if (db.isOpen()) {
            db.replace(EmUserDao.TABLE_NAME, null, values);
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(EmUserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(EmUserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(EmUserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(EmUserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(EmUserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + EmUserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array != null && array.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String str : array) {
                list.add(str);
            }

            return list;
        }

        return null;
    }

    /**
     * 保存message
     *
     * @param message
     * @return 返回这条messaged在db中的id
     */
    public synchronized Integer saveMessage(EmInviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(EmInviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(EmInviteMessgeDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(EmInviteMessgeDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(EmInviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(EmInviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(EmInviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(EmInviteMessgeDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(EmInviteMessgeDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + EmInviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * 更新message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(EmInviteMessgeDao.TABLE_NAME, values, EmInviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * 获取messges
     *
     * @return
     */
    synchronized public List<EmInviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<EmInviteMessage> msgs = new ArrayList<EmInviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + EmInviteMessgeDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext()) {
                EmInviteMessage msg = new EmInviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(EmInviteMessgeDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if (status == InviteMesageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEINVITEED);
                else if (status == InviteMesageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAGREED);
                else if (status == InviteMesageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEREFUSED);
                else if (status == InviteMesageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.AGREED);
                else if (status == InviteMesageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.REFUSED);
                else if (status == InviteMesageStatus.BEAPPLYED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAPPLYED);
                else if (status == InviteMesageStatus.GROUPINVITATION.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION);
                else if (status == InviteMesageStatus.GROUPINVITATION_ACCEPTED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_ACCEPTED);
                else if (status == InviteMesageStatus.GROUPINVITATION_DECLINED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_DECLINED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * 删除要求消息
     *
     * @param from
     */
    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(EmInviteMessgeDao.TABLE_NAME, EmInviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + EmInviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + EmInviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(EmInviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(EmInviteMessgeDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }


}
