package com.hn.nishant.nvhn.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hn.nishant.nvhn.R;

/**
 * Created by nishant on 25.06.17.
 */

public class CommentCountView extends ImageView {

    private Bitmap bitmap;

    public int getCount() {
        return count;
    }

    private int count;

    private Paint paint;

    private int alpha = 20;

    public CommentCountView(Context context){
        this(context, null);
    }

    public CommentCountView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20.0f);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setSubpixelText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getWidth()/2;
        int cy = getHeight()/2;
        int w = getDrawable().getIntrinsicWidth()/4;
        int h = getDrawable().getIntrinsicHeight()/4;
        paint.setTextSize(alpha);
        canvas.drawText(""+count,cx , cy +h,paint );
    }

    public void setCommentCount(int count){
        this.count = count;
        invalidate();
    }

    public void setAlpha(int alpha){
        this.alpha = alpha;
        System.out.println("new alpha" + alpha);
        invalidate();
    }

}
