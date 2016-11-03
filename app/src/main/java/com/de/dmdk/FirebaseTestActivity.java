package com.de.dmdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseTestActivity extends AppCompatActivity {

    DatabaseReference databaseRefAdmin = FirebaseDatabase.getInstance().getReference("admin");

    private TextView textView;
    private String phoneNum ="827648902";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        initView();

/*        databaseRefAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textView.setText((dataSnapshot.getValue(Admin.class)).getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.textView);
    }

    public void onSubmit(View view){
        Admin admin= new Admin();
        admin.setSno("4");
        admin.setName("Jerome");
        admin.setPassword("jerome123");
        admin.setEmail("jerome@calydontech.com");
        admin.setMembershipId("892475");
        admin.setPhoneNum("7200078102");
        admin.setIsSuperAdmin("n");

        databaseRefAdmin.push().setValue(admin);
/*        Query queryRef = databaseRefAdmin.orderByChild("phoneNum").equalTo(phoneNum);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot childDataSnapshot:dataSnapshot.getChildren()){
                        Admin admin= childDataSnapshot.getValue(Admin.class);
                        if(admin.getPhoneNum().equalsIgnoreCase(phoneNum)){
                            Toast.makeText(getApplicationContext(),admin.getName()+" Login Success!!!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Login failed! check your password!!",Toast.LENGTH_SHORT).show();
                        }
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Login failed! check your user name!!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }

}
