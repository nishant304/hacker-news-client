package com.hn.nishant.nvhn.view.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.hn.nishant.nvhn.view.custom.HnWebView;

/**
 * Created by nishant on 15.06.17.
 */

public class HnRecyclerView extends RecyclerView {

    public HnRecyclerView(Context context){
        super(context);
    }


    public HnRecyclerView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return super.fling(velocityX*1000, velocityY);
    }
}
