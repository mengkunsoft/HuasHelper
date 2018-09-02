package cn.mkblog.huashelper.tool;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * 自定义下拉提示
 * 参考：http://blog.sina.com.cn/s/blog_54109a5801012pmi.html
 */

public class ZeroAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public ZeroAutoCompleteTextView(Context context) {
        super(context);
    }
    public ZeroAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ZeroAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean enoughToFilter() {
        return true;
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
//        throw new RuntimeException("Stub!");
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
    }
}
