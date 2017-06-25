package com.hn.nishant.nvhn.view.custom;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hn.nishant.nvhn.R;

/**
 * Created by nishant on 20.06.17.
 */

public class DragView extends FrameLayout {

    private ViewDragHelper viewDragHelper;

    private int left;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallBack());
    }

    public class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child.getId() == R.id.storyItemView;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            System.out.println("xvel " + xvel);

            int pos = releasedChild.getPaddingLeft();
            if (xvel > 0.0f) {
                if (left > releasedChild.getWidth() / 3)
                    pos = releasedChild.getWidth() / 3;
            } else {
                if (left < -releasedChild.getWidth() / 3)
                    pos = -releasedChild.getWidth() / 3;
            }
            viewDragHelper.settleCapturedViewAt(pos, releasedChild.getPaddingTop());
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            DragView.this.left = left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (dx < 0) {
                return Math.max(-2 * child.getWidth() / 3, left + dx);
            }
            return Math.min(2 * child.getWidth() / 3, left + dx);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (viewDragHelper.shouldInterceptTouchEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && left > -10 && left < 10) {
            return super.onTouchEvent(event);
        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
