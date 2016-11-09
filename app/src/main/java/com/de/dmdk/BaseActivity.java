package com.de.dmdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showAlertDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showAlertDialogWithClickListener(String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onCancelClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                onClickListener/*new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }*/);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", onCancelClickListener);
        alertDialog.show();
    }


    public void showMemberSearchDialog(String title, String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onCancelClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                onClickListener/*new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }*/);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", onCancelClickListener);
        alertDialog.show();
    }

    ProgressDialog loading = null;

    public ProgressDialog getProgressDialog(String message) {
        if (loading == null) {
            loading = new ProgressDialog(this,R.style.MyTheme);
            loading.setCancelable(false);
            loading.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        }
        if (!TextUtils.isEmpty(message)) {
            loading.setMessage(message);
        }

        return loading;
    }

    public void showProgress(String message) {
        getProgressDialog(message).show();
    }

    public void dismissProgress() {
        getProgressDialog("").dismiss();
        ;
    }




}
