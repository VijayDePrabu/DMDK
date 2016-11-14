package com.de.dmdk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.de.dmdk.membership.MemberDatum;
import com.de.dmdk.membership.RecentMemberArrayAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentMembershipsActivity extends BaseActivity {

    /* Firebase elements */
    /* Fire Database */
    private DatabaseReference databaseRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseMemberDataReference = FirebaseDatabase.getInstance().getReference("member_data");

    private ListView listView;
    private List<MemberDatum> recentMembersList = new ArrayList<MemberDatum>();
    private ArrayAdapter<MemberDatum> recentMemberArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_memberships);
        listView = (ListView) findViewById(R.id.listVRecentMembers);
        recentMemberArrayAdapter = new RecentMemberArrayAdapter(this,R.layout.recent_member_row,recentMembersList);
        listView.setAdapter(recentMemberArrayAdapter);
        fetchRecentMembers(5);
    }


       /* fetch Recent Members of given count*/

    private void fetchRecentMembers(final int count) {
        showProgress("");
        recentMembersList.clear();
        Query queryRef = databaseMemberDataReference.orderByChild("membershipId").limitToLast(count)/*.orderByChild("dob")*/;
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    dismissProgress();
                    for(DataSnapshot childDataSnapshot:dataSnapshot.getChildren()){
                        MemberDatum memberDatum= childDataSnapshot.getValue(MemberDatum.class);
                        recentMembersList.add(memberDatum);
                    }
                    recentMemberArrayAdapter.addAll(recentMembersList);
                    recentMemberArrayAdapter.notifyDataSetChanged();

                }else{
                    dismissProgress();

                    return;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgress();

            }
        });
    }


}
