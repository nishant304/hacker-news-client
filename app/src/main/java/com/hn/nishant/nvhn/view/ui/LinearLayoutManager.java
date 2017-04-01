package com.hn.nishant.nvhn.view.ui;

import android.content.Context;

/**
 * Created by nishant on 20.03.17.
 */

public class LinearLayoutManager extends android.support.v7.widget.LinearLayoutManager {

    public LinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
