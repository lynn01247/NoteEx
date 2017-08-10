package tatait.com.noteex.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class NewTextView extends EditText {
    public NewTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NewTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NewTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean getDefaultEditable() {//禁止EditText被编辑
        return false;
    }
}
