package com.de.dmdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.de.dmdk.membership.NewMembershipEntryActivity;

public class MenuScreenActivity extends BaseActivity implements View.OnClickListener {

    private Button btnNewRegister,btnRecentRegister,btnUpdateRecords,btnPrint,btnReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        initViews();
        setUpListeners();
    }


    private void initViews() {
        btnNewRegister = (Button)findViewById(R.id.btnNewRegister);
        btnRecentRegister = (Button)findViewById(R.id.btnRecentRegister);
        btnUpdateRecords = (Button)findViewById(R.id.btnUpdateRecords);
        btnPrint = (Button)findViewById(R.id.btnPrint);
        btnReports = (Button)findViewById(R.id.btnReports);
    }

    private void setUpListeners() {

        btnNewRegister.setOnClickListener(this);
        btnRecentRegister.setOnClickListener(this);
        btnUpdateRecords.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnReports.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnNewRegister:
                Intent intentNewRegister = new Intent(this, MembershipEntryActivity.class);
                intentNewRegister.putExtra(DMDKConstants.PURPOSE,DMDKConstants.PURPOSE_NEW_REGISTER);
                startActivity(intentNewRegister);
                break;
            case R.id.btnRecentRegister:
                Intent intentRecentRegister= new Intent(this, RecentMembershipsActivity.class);
                startActivity(intentRecentRegister);
                break;
            case R.id.btnUpdateRecords:
                Intent intentUpdateRecords = new Intent(this, MembershipEntryActivity.class);
                intentUpdateRecords.putExtra(DMDKConstants.PURPOSE,DMDKConstants.PURPOSE_UPDATE_RECORDS);
                startActivity(intentUpdateRecords);
                break;
            case R.id.btnPrint:
                Intent intentPrint = new Intent(this, MembershipEntryActivity.class);
                intentPrint.putExtra(DMDKConstants.PURPOSE,DMDKConstants.PURPOSE_PRINT);
                startActivity(intentPrint);
                break;
            case R.id.btnReports:
                Intent intentReports = new Intent(this, ReportsActivity.class);
                startActivity(intentReports);
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
