package com.hn.nishant.nvhn.customtabs;

import android.app.Activity;
import android.content.Intent;

import com.hn.nishant.nvhn.view.activity.BrowseActivity;

/**
 * Created by nishant on 28.05.17.
 */

public class CTFallBack implements CustomTabActivityHelper.CustomTabFallBack {

    @Override
    public void openUri(Activity activity, long  position) {
        Intent intent = new Intent(activity, BrowseActivity.class);
        intent.putExtra("storyId", position);
        activity.startActivity(intent);
    }
}
