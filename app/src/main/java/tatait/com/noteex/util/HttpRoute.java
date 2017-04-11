package tatait.com.noteex.util;

public class HttpRoute {
    // 头部
    public static final String URL_HEAD = "https://turtle.leanapp.cn/";

    // 获取笔记信息
    public static final String URL_GET_NOTE = "getNote";

    // 获取笔记信息(未登录)
    public static final String URL_GET_NOTE_TYPE = "getNoteByType";

    // 获取笔记信息
    public static final String URL_GET_COLL = "getColl";

    // 提交笔记信息
    public static final String URL_POST_NOTE = "addNote";

    // 修改笔记信息
    public static final String URL_POST_UPDATE_NOTE = "updateNote";

    // 删除笔记信息
    public static final String URL_DEL_NOTE = "delNote";

    // 收藏笔记信息
    public static final String URL_POST_COLLECT_NOTE = "addNoteCollect";

    // 删除笔记收藏信息
    public static final String URL_DEL_NOTE_COLLECT = "delNoteCollect";

    // 删除文件夹或标签
    public static final String URL_DEL_FILE_TAG = "delFileTag";

    // 分享笔记信息
    public static final String URL_POST_SHARED_NOTE = "shareNote";

    // 点赞笔记信息
    public static final String URL_POST_PRAISE_NOTE = "praiseNote";

    // 删除笔记分享信息
    public static final String URL_DEL_NOTE_SHARE = "delNoteShared";

    // 获取文件夹分类
    public static final String URL_GET_NOTE_TREE = "getNoteTree";

    // 新增文件夹分类
    public static final String URL_POST_NOTE_TREE = "addNoteTree";

    // 获取标签分类
    public static final String URL_GET_NOTE_TAG = "getNoteTag";

    // 新增标签分类
    public static final String URL_POST_NOTE_TAG = "addNoteTag";

    // 登录
    public static final String URL_GET_LOGIN_USER = "loginUser";

    // 注册
    public static final String URL_GET_REGISTER_USER = "registerUser";

    // 获取用户信息
    public static final String URL_GET_CONVERSATION_USER = "conversationUser";

    // 反馈信息
    public static final String URL_POST_FEEK_BACK = "addFeekBack";

    // 获取版本信息
    public static final String URL_GET_VERSION = "getVersion";

    // 获取广告栏图片
    public static final String URL_GET_LINK_IMG = "getLinkImg";

    // 获取自动化数据
    public static final String URL_GET_JUE_JIN_DATA = "addJueJinNote";

    // 获取自动化数据--小说
    public static final String URL_ADD_FL_FICTION = "addCaoliuNote";

    // 获取自动化数据--图片
    public static final String URL_ADD_FL_PIC = "addCaoliuPic";

    // 获取小说
    public static final String URL_GET_FL_FICTION = "getFlFiction";

    // 获取图片
    public static final String URL_GET_FL_PIC = "getFlPic";
}
