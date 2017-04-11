package tatait.com.noteex.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import solid.ren.skinlibrary.loader.SkinManager;
import solid.ren.skinlibrary.loader.TextViewRepository;

/**
 * Just a simple singleton class to manage font loading from assets.
 * Original source: <a href="http://stackoverflow.com/a/29134056">http://stackoverflow.com/a/29134056</a>
 * <p/>
 * Created by mathias.berwig on 22/06/2016.
 */
public class FontManager {
    private static final String TAG = FontManager.class.getName();

    private static FontManager instance;

    private AssetManager assetManager;

    private Map<String, Typeface> fonts;

    private FontManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.fonts = new HashMap<>();
    }

    public static FontManager getInstance(AssetManager assetManager) {
        if (instance == null) {
            instance = new FontManager(assetManager);
        }
        return instance;
    }

    public Typeface getFont(String asset) {
        if (fonts.containsKey(asset))
            return fonts.get(asset);

        Typeface font = null;

        try {
            font = Typeface.createFromAsset(assetManager, asset);
            fonts.put(asset, font);
        } catch (RuntimeException e) {
            Log.e(TAG, "getFont: Can't create font from asset.", e);
        }
        return font;
    }

    public static void setDefaultFont(Context context) {
        String front_choose = (String) SharedPreferencesUtils.getParam(context, "front_choose", "");
        if ("".equals(front_choose)) {
            SkinManager.getInstance().loadFont(null);
        } else {
            Typeface tf = AssetFileUtils.createTypeface(context, front_choose);
            TextViewRepository.applyFont(tf);
        }
    }
}
