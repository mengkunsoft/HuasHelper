package cn.mkblog.huashelper.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by wan on 2016/10/12.
 * 自定义标题栏，用来显示周一到周日
 */
public class WeekTitle extends View {

    private Paint mPaint;
    private String[] days = {"一", "二", "三", "四", "五", "六", "日"};

    public WeekTitle(Context context)
    {
        super(context);
    }

    public WeekTitle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint();
    }

    /**
     * 重写测量函数，否则在设置wrap_content的时候默认为match_parent
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onDraw(Canvas canvas)
    {
        int textSize = 26;  // 字体大小

        // paint.setTextSize()根据不同手机分辨率设置字体大小 http://blog.csdn.net/eileenching/article/details/67639481
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        // 以分辨率为720*1080准，计算宽高比值
        float ratioWidth = (float) mScreenWidth / 720;
        float ratioHeight = (float) mScreenHeight / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        textSize = Math.round(textSize * ratioMetrics);


        // 获取星期
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        if(day == 1) day = 8;
        day = day - 2;

        // 获得当前View的宽度
        int width = getWidth();
        int offset = width / days.length;
        int currentPosition = (offset - textSize) / 2;

        // 设置要绘制的字体
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        mPaint.setTextSize(textSize);

        // 获取文字大小信息
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;            // 为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;      // 为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (getHeight() - top - bottom)/2;   // 基线中间点的y轴计算公式

        for(int i = 0; i < days.length ; i++)
        {
            // 圈出当前的日期
            if(day == i)
            {
                mPaint.setColor(Color.rgb(200, 134, 139));
            } else {
                mPaint.setColor(Color.rgb(0, 134, 139));
            }
            canvas.drawText(days[i], currentPosition, baseLineY, mPaint);
            currentPosition += offset;
        }

        // 调用父类的绘图方法
        super.onDraw(canvas);
    }
}
