package tatait.com.noteex.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 监听软键盘变化的LineaLayout,一般用在界面优化之中<p>
 * Use:
 * KeyBoradLineaLayout.setOnResizeListener(new OnResizeListener() {
 *
 * @author liangshiquan
 * @Override public void OnResize(int w, int h, int oldw, int oldh) {
 * if(h<oldh){
 * //此时软键盘弹出
 * }
 * }
 * });
 */
public class KeyBoradLineaLayout extends LinearLayout {
    private OnResizeListener mListener;

    public interface OnResizeListener {
        void OnResize(int w, int h, int oldw, int oldh);
    }

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    public KeyBoradLineaLayout(Context context) {
        super(context);
    }

    public KeyBoradLineaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mListener != null) {
            mListener.OnResize(w, h, oldw, oldh);
        }
    }
}
