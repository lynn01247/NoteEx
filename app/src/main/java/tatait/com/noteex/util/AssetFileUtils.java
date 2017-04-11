package tatait.com.noteex.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by _SOLID
 * Date:2016/7/5
 * Time:10:17
 */
public class AssetFileUtils {
    /**
     * 得到存放皮肤的目录
     *
     * @param context the context
     * @return 存放皮肤的目录
     */
    public static String getFrontDir(Context context) {
        File frontDir = new File(getCacheDir(context), "front");
        if (!frontDir.exists()) {
            frontDir.mkdirs();
        }
        return frontDir.getAbsolutePath();
    }

    /**
     * 得到手机的缓存目录
     *
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir.getAbsolutePath();
            }
        }
        File cacheDir = context.getCacheDir();
        return cacheDir.getAbsolutePath();
    }

    /**
     * 从SD卡中自定义字体
     *
     * @param context
     * @param fontName
     * @return
     */
    public static Typeface createTypeface(Context context, String fontName) {
        Typeface tf;
        if (!TextUtils.isEmpty(fontName)) {
            tf = Typeface.createFromFile(new File(getCacheDir(context), "front" + File.separator + fontName));
        } else {
            tf = Typeface.DEFAULT;
        }
        return tf;
    }
}
