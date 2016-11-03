package com.de.dmdk.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.de.dmdk.Admin;
import com.de.dmdk.BaseActivity;
import com.de.dmdk.MenuScreenActivity;
import com.de.dmdk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends BaseActivity implements View.OnClickListener{

    DatabaseReference databaseRefAdmin = FirebaseDatabase.getInstance().getReference("admin");


    private Button btnLogin;
    private TextView textVLinkRegister;
    private EditText editTPhone,editTPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.admin_login));
        initView();
        setUpListeners();
    }

    private void initView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        textVLinkRegister = (TextView) findViewById(R.id.textVLinkRegister);
        editTPhone = (EditText) findViewById(R.id.editTPhone);
        editTPassword = (EditText) findViewById(R.id.editTPassword);
    }

    private void setUpListeners() {
        btnLogin.setOnClickListener(this);
        textVLinkRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogin:
                if (!TextUtils.isEmpty(editTPhone.getText().toString().trim())) {
                    if (!TextUtils.isEmpty(editTPassword.getText().toString().trim())) {

                        Query queryRef = databaseRefAdmin.orderByChild("phoneNum").equalTo(editTPhone.getText().toString().trim());
                        queryRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                        Admin admin = childDataSnapshot.getValue(Admin.class);
                                        if (admin.getPassword().equalsIgnoreCase(editTPassword.getText().toString().trim())) {
                                            navigateToMenuScreen();
                                        } else {
                                            showAlertDialog("Login failed. Please check your password!!");
                                        }
                                    }

                                } else {
                                    showAlertDialog("Login failed. Please check your user name!!");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        showAlertDialog("Please enter your password!!");

                    }
                } else {
                    showAlertDialog("Please enter your phone number!!");
                }
                break;
            case R.id.textVLinkRegister:
                Intent intentRegister = new Intent(this, NewAdminRegisterationActivity.class);
                startActivity(intentRegister);
                finish();
                break;
        }
    }

    private void navigateToMenuScreen() {
        Intent intentLogin = new Intent(LoginActivity.this, MenuScreenActivity.class);
        startActivity(intentLogin);
        finish();
    }
}
