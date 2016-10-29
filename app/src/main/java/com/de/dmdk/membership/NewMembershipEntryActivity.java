package com.de.dmdk.membership;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.de.dmdk.BaseActivity;
import com.de.dmdk.R;

public class NewMembershipEntryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership_entry);
        setTitle(getString(R.string.new_membership_entry));
    }
}
