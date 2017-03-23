package com.hn.nishant.nvhn.view.activity;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by nishant on 16.03.17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void makeToast(@NonNull String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

}
