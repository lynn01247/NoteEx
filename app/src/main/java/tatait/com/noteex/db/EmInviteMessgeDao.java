/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tatait.com.noteex.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;


public class EmInviteMessgeDao {
    static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_FROM = "username";
    static final String COLUMN_NAME_GROUP_ID = "groupid";
    static final String COLUMN_NAME_GROUP_Name = "groupname";

    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


    public EmInviteMessgeDao(Context context) {
    }

    /**
     * 保存message
     * @param message
     * @return 返回这条messaged在db中的id
     */
    public Integer saveMessage(EmInviteMessage message) {
        return EmDBManager.getInstance().saveMessage(message);
    }

    /**
     * 更新message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId, ContentValues values) {
        EmDBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * 获取messges
     * @return
     */
    public List<EmInviteMessage> getMessagesList() {
        return EmDBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from) {
        EmDBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount() {
        return EmDBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count) {
        EmDBManager.getInstance().setUnreadNotifyCount(count);
    }
}
