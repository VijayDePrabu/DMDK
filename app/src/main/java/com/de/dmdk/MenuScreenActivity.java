package com.de.dmdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.de.dmdk.membership.NewMembershipEntryActivity;

public class MenuScreenActivity extends BaseActivity implements View.OnClickListener {

    private Button btnNewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        initViews();
        setUpListeners();
    }


    private void initViews() {
        btnNewRegister = (Button)findViewById(R.id.btnNewRegister);
    }

    private void setUpListeners() {
        btnNewRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNewRegister:
                Intent intentLogin = new Intent(this, MembershipEntryActivity.class);
                startActivity(intentLogin);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        showAlertDialogWithClickListener(getString(R.string.close_app_msg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // okey button
                MenuScreenActivity.this.finish();
            }
        }, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // cancel button
                dialog.dismiss();
            }
        });

    }
}
