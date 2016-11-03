package com.de.dmdk.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.de.dmdk.BaseActivity;
import com.de.dmdk.R;

public class NewAdminRegisterationActivity extends BaseActivity implements View.OnClickListener {

    private TextView textVLinkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        setTitle(getString(R.string.admin_registeration));
        initView();
        setUpListeners();
    }

    private void initView() {
        textVLinkLogin = (TextView) findViewById(R.id.textVLinkLogin);
    }

    private void setUpListeners() {
        textVLinkLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textVLinkLogin:
                Intent intentRegister = new Intent(this, LoginActivity.class);
                startActivity(intentRegister);
                finish();
                break;
        }
    }
}
