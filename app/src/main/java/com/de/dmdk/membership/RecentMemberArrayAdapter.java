package com.de.dmdk.membership;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.de.dmdk.R;

import java.util.List;

public class RecentMemberArrayAdapter extends ArrayAdapter<MemberDatum> {

    private Context context;
    public RecentMemberArrayAdapter(Context context, int recent_member_row, List<MemberDatum> recentMembersList) {
        super(context, recent_member_row);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View rowView = convertView;
        final ViewHolder viewHolder;
        MemberDatum memberDatum= getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView = inflater.inflate(R.layout.recent_member_row,
             parent, false);
            viewHolder = new ViewHolder();
            viewHolder.district = (TextView) convertView.findViewById(R.id.textVDistrict);
            viewHolder.dob = (TextView) convertView.findViewById(R.id.textVDob);

            viewHolder.doj = (TextView) convertView.findViewById(R.id.textVDoj);
     //       viewHolder.entryBy = (TextView) convertView.findViewById(R.id.textVEntryBy);

            viewHolder.gender = (TextView) convertView.findViewById(R.id.textVGender);
            viewHolder.husbandName = (TextView) convertView.findViewById(R.id.textVHusbandName);

            viewHolder.membershipId = (TextView) convertView.findViewById(R.id.textVMemberIDLabel);
            viewHolder.name = (TextView) convertView.findViewById(R.id.textVNameLabel);

            viewHolder.mp = (TextView) convertView.findViewById(R.id.textVMp);
            viewHolder.mla = (TextView) convertView.findViewById(R.id.textVMla);

            viewHolder.panchayat = (TextView) convertView.findViewById(R.id.textVPanchayat);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.textVPhone);

            viewHolder.townCity = (TextView) convertView.findViewById(R.id.textVTownCity);
            viewHolder.voterId = (TextView) convertView.findViewById(R.id.textVVoterID);

            viewHolder.ward = (TextView) convertView.findViewById(R.id.textVWard);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.district.setText(memberDatum.getDistrict());
        viewHolder.dob.setText(memberDatum.getDob());

        viewHolder.doj.setText(memberDatum.getDoj());
        //       viewHolder.entryBy = (TextView) convertView.findViewById(R.id.textVEntryBy);

        viewHolder.gender.setText(memberDatum.getGender());
        viewHolder.husbandName.setText(memberDatum.getHusbandName());

        viewHolder.membershipId.setText(memberDatum.getMembershipId());
        viewHolder.name.setText(memberDatum.getName());

        viewHolder.mla.setText(memberDatum.getMla());
        viewHolder.mp.setText(memberDatum.getMp());

        viewHolder.panchayat.setText(memberDatum.getPanchayat());
        viewHolder.phone.setText(memberDatum.getPhone());

        viewHolder.townCity.setText(memberDatum.getTownCity());
        viewHolder.voterId.setText(memberDatum.getVoterId());

        viewHolder.ward.setText(memberDatum.getWard());

        return convertView;
    }

    static class ViewHolder {
        private TextView district;
        private TextView dob;
        private TextView doj;
//        private TextView entryBy;
        private TextView gender;
        private TextView husbandName;
        private TextView membershipId;
        private TextView mla;
        private TextView mp;
        private TextView name;
        private TextView panchayat;
        private TextView phone;
        private TextView townCity;
        private TextView voterId;
        private TextView ward;

    }

}