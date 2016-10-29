package com.de.dmdk.login;

import android.os.Bundle;

import com.de.dmdk.BaseActivity;
import com.de.dmdk.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
