package com.de.dmdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class IDCardConfirmActivity extends AppCompatActivity {

    private View idCardView;
    private LinearLayout mLinearLayout;
    RelativeLayout idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_confirm);

        // clearing any focus
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) currentFocusedView.clearFocus();
        if (mLinearLayout != null) {
            mLinearLayout.requestFocus();
        }
        idCardView = (View) findViewById(R.id.idCardLayout);
        idCardView.setVisibility(View.GONE);
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.few);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        /*idCard = getIDCard();
        layout.addView(idCard);
        imageVPhotoPrint.setImageDrawable(imageVPhoto.getDrawable());*/
    }
}
