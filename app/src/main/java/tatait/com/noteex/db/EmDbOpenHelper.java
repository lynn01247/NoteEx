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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tatait.com.noteex.widget.App;

public class EmDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static EmDbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + EmUserDao.TABLE_NAME + " ("
            + EmUserDao.COLUMN_NAME_NICK + " TEXT, "
            + EmUserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + EmUserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + EmInviteMessgeDao.TABLE_NAME + " ("
            + EmInviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EmInviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + EmInviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + EmInviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + EmInviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + EmInviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + EmInviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + EmInviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + EmInviteMessgeDao.COLUMN_NAME_TIME + " TEXT, "
            + EmInviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + EmUserDao.ROBOT_TABLE_NAME + " ("
            + EmUserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + EmUserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + EmUserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + EmUserDao.PREF_TABLE_NAME + " ("
            + EmUserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + EmUserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private EmDbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static EmDbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EmDbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return App.getInstance().getCurrentUserName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + EmUserDao.TABLE_NAME + " ADD COLUMN " +
                    EmUserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }

        if (oldVersion < 3) {
            db.execSQL(CREATE_PREF_TABLE);
        }
        if (oldVersion < 4) {
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + EmInviteMessgeDao.TABLE_NAME + " ADD COLUMN " +
                    EmInviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER ;");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + EmInviteMessgeDao.TABLE_NAME + " ADD COLUMN " +
                    EmInviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT;");
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
