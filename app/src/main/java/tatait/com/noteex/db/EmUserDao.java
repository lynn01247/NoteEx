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

import android.content.Context;

import java.util.List;
import java.util.Map;


public class EmUserDao {

    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";


    public EmUserDao(Context context) {
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<EmUser> contactList) {
        EmDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EmUser> getContactList() {

        return EmDBManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人
     * @param username
     */
    public void deleteContact(String username) {
        EmDBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContact(EmUser user) {
        EmDBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups) {
        EmDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String> getDisabledGroups() {
        return EmDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        EmDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return EmDBManager.getInstance().getDisabledIds();
    }


}
