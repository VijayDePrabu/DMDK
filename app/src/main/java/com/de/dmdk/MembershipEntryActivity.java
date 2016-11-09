package com.de.dmdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import com.de.dmdk.view.InstantAutoCompleteTextView;
import android.print.PrinterInfo;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.graphics.pdf.PdfDocument.*;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.ZoomStyle;
import com.de.dmdk.languagehelper.LocaleHelper;
import com.de.dmdk.membership.MemberDatum;
import com.de.dmdk.permissions.MarshMallowPermission;
import com.de.dmdk.printhelper.PdfDocumentAdapter;
import com.de.dmdk.printhelper.PrintJobMonitorService;
import com.de.dmdk.printhelper.USBAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hp.mss.hpprint.activity.PrintPluginManagerActivity;
import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.de.dmdk.R.id.autoCompleteTVTownCity;
import static com.de.dmdk.R.id.editTVoterId;
import static com.de.dmdk.R.id.idCardLayout;


public class MembershipEntryActivity extends BaseActivity implements View.OnClickListener,TextWatcher{


    private boolean isFormEditable;
    private View idCardView;
    private LinearLayout mLinearLayout;
    private PrintManager mgr = null;
    private ImageView imageVPhoto, imageVPhotoPrint;
    private File file;

    private Bitmap memberBitmap ;

    /* Input Fields of ID Card*/

    private Spinner spinGender;


    private InstantAutoCompleteTextView autoCompleteTVEntryFormPanchayat;
    private InstantAutoCompleteTextView autoCompleteTVTownCity;
    private InstantAutoCompleteTextView autoCompleteTVDistrict;
    private int purpose;


    PanchayatDetailsAdapter adapter ;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    /* Firebase elements */
    /* Fire Database */
    private DatabaseReference databaseRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseMemberDataReference = FirebaseDatabase.getInstance().getReference("member_data");

    private RelativeLayout few;
    private EditText editTPhone;
    private EditText editTName;
    private EditText editTDOB;
    private EditText editTHusband;
    private EditText editTVoterId;
    private EditText editTWard;
    private EditText editTMembershipId;
    private EditText editTDate;
    private InstantAutoCompleteTextView editTMLA;
    private InstantAutoCompleteTextView editTMP;

    private Button btnPreview;
    private boolean isBackMenuAvailable, isPrintMenuAvailable, isClearMenuAvailable;
    private int idCardRId;
    private Menu menu;
    private PanchayatDetails selectedPanchayat;
    private MpMlaDistrict selectedMpMlaDistrict;
    private String tempDOB;
    private boolean isPushMemberData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this,"ta");
        super.onCreate(savedInstanceState);
        getIntentCheck();
        setContentView(R.layout.activity_new_membership_entry);
        initView();
        setUpListeners();
        if(purpose == DMDKConstants.PURPOSE_UPDATE_RECORDS) {
            showDialogs("Update Member Record","Enter the MemberID");
        }else if(purpose == DMDKConstants.PURPOSE_PRINT) {
            showDialogs("Print","Enter the MemberID");
        }
        mgr = (PrintManager) getSystemService(PRINT_SERVICE);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
/*  get all mp, mla,district data*/
        databaseRootReference.child("mp_mla_district").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("VIJAY "+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                Log.e("Count " ,""+snapshot.getChildrenCount());
                ArrayList<String>  mp = new ArrayList<String>();
                ArrayList<String>  mla = new ArrayList<String>();
/*
                ArrayList<String>  mpmlaDistrict = new ArrayList<String>();
*/

                final ArrayList<MpMlaDistrict>  mpMlaDistrictDetailFull = new ArrayList<MpMlaDistrict>();

                for (DataSnapshot single : snapshot.getChildren()) {
                    MpMlaDistrict mpMlaDistrictDetails= single.getValue(MpMlaDistrict.class);
                    Log.e("village_panchayat_name" ,""+mpMlaDistrictDetails.getMp_tamil());
                    if(!TextUtils.isEmpty(mpMlaDistrictDetails.getMp())) {
                        mp.add(mpMlaDistrictDetails.getMp());
                    }
                    mla.add(mpMlaDistrictDetails.getMla());
/*
                    mpmlaDistrict.add(mpMlaDistrictDetails.getDistrictTamil());
*/

                    mpMlaDistrictDetailFull.add(mpMlaDistrictDetails);
                }
                /*PanchayatDetailsAdapter adapter =
                        new PanchayatDetailsAdapter(MembershipEntryActivity.this, R.layout.list_row, panchayatDetail);*/
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(MembershipEntryActivity.this, R.layout.list_row,R.id.txtTitle, mp);
                editTMP.setAdapter(adapter);

                editTMP.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        String selected = (String) arg0.getAdapter().getItem(arg2);

                        for(MpMlaDistrict mpMlaDistrict:mpMlaDistrictDetailFull)
                        {
                            if(selected.equalsIgnoreCase(mpMlaDistrict.getMp())){
                                  /*Toast.makeText(MembershipEntryActivity.this,
                                          "Clicked " + arg2 + " name: " + panchayat.getVillage_panchayat_name_tamil(),
                                          Toast.LENGTH_SHORT).show();*/
                                selectedMpMlaDistrict = mpMlaDistrict;
                                editTMP.setText(mpMlaDistrict.getMp_tamil());
                                autoCompleteTVDistrict.setText(mpMlaDistrict.getDistrict_tamil());

                                //    autoCompleteTVTownCity.setText(mpMlaDistrict.getDistrict_name());
                            }
                        }

                    }
                });

                /*editTMP.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        *//*if((TextUtils.isEmpty(s.toString()) || (null==selectedMpMlaDistrict)) || (!s.toString().equalsIgnoreCase(selectedMpMlaDistrict.getDistrict()))){
                            selectedMpMlaDistrict = null;
                            autoCompleteTVDistrict.setText("");
                        }*//*
                    }
                });*/

                /* mla */
                ArrayAdapter<String> adaptereditTMLA =
                        new ArrayAdapter<String>(MembershipEntryActivity.this, R.layout.list_row,R.id.txtTitle, mla);
                editTMLA.setAdapter(adaptereditTMLA);

                editTMLA.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        String selected = (String) arg0.getAdapter().getItem(arg2);

                        for(MpMlaDistrict mpMlaDistrict:mpMlaDistrictDetailFull)
                        {
                            if(selected.equalsIgnoreCase(mpMlaDistrict.getMla())){
                                  /*Toast.makeText(MembershipEntryActivity.this,
                                          "Clicked " + arg2 + " name: " + panchayat.getVillage_panchayat_name_tamil(),
                                          Toast.LENGTH_SHORT).show();*/
                                selectedMpMlaDistrict = mpMlaDistrict;
                                editTMLA.setText(mpMlaDistrict.getMla_tamil());
                                autoCompleteTVDistrict.setText(mpMlaDistrict.getDistrict_tamil());
                                //    autoCompleteTVTownCity.setText(mpMlaDistrict.getDistrict_name());
                            }
                        }

                    }
                });

               /* editTMP.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                       *//* if((TextUtils.isEmpty(s.toString()) || (null==selectedMpMlaDistrict)) || (!s.toString().equalsIgnoreCase(selectedMpMlaDistrict.getDistrict()))){
                            selectedMpMlaDistrict = null;
                            autoCompleteTVDistrict.setText("");
                        }*//*
                    }
                });*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
/*  get all panchayat data*/
        databaseRootReference.child("autocomplete").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("VIJAY "+snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                Log.e("Count " ,""+snapshot.getChildrenCount());
                ArrayList<String>  panchayatName = new ArrayList<String>();
                ArrayList<String>  panchayatDistrict = new ArrayList<String>();

                final ArrayList<PanchayatDetails>  panchayatDetailFull = new ArrayList<PanchayatDetails>();

                for (DataSnapshot single : snapshot.getChildren()) {
                    PanchayatDetails panchayatDetails= single.getValue(PanchayatDetails.class);
                    Log.e("village_panchayat_name" ,""+panchayatDetails.getVillage_panchayat_name_tamil());
                    panchayatName.add(panchayatDetails.getVillage_panchayat_name());
                    panchayatDetailFull.add(panchayatDetails);
                }
                /*PanchayatDetailsAdapter adapter =
                        new PanchayatDetailsAdapter(MembershipEntryActivity.this, R.layout.list_row, panchayatDetail);*/
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(MembershipEntryActivity.this, R.layout.list_row,R.id.txtTitle, panchayatName);
                autoCompleteTVEntryFormPanchayat.setAdapter(adapter);

                autoCompleteTVEntryFormPanchayat.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        String selected = (String) arg0.getAdapter().getItem(arg2);

                        for(PanchayatDetails panchayat:panchayatDetailFull)
                        {
                              if(selected.equalsIgnoreCase(panchayat.getVillage_panchayat_name())){
                                  /*Toast.makeText(MembershipEntryActivity.this,
                                          "Clicked " + arg2 + " name: " + panchayat.getVillage_panchayat_name_tamil(),
                                          Toast.LENGTH_SHORT).show();*/
                                  selectedPanchayat = panchayat;
                                  autoCompleteTVEntryFormPanchayat.setText(panchayat.getVillage_panchayat_name_tamil());
                                  autoCompleteTVDistrict.setText(panchayat.getDistrict_name_tamil());
                                  autoCompleteTVTownCity.setText(panchayat.getDistrict_name_tamil());
                              }
                        }

                    }
                });

                autoCompleteTVEntryFormPanchayat.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                               if((TextUtils.isEmpty(s.toString()) || (null==selectedPanchayat)) || (!s.toString().equalsIgnoreCase(selectedPanchayat.getVillage_panchayat_name()))){
                                   selectedPanchayat = null;
                                   autoCompleteTVDistrict.setText("");
                                   autoCompleteTVTownCity.setText("");
                               }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



      /* Auto complete*/
/*        databaseRootReference.child("0").orderByChild("village_panchayat_name").*//*startAt("Na").limitToFirst(6).*//*addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PanchayatDetails panchayatDetails= dataSnapshot.getValue(PanchayatDetails.class);

                Log.e("village_panchayat_name" ,""+panchayatDetails.getVillage_panchayat_name());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }

    private void getIntentCheck() {
        Intent intent = getIntent();
        purpose = intent.getIntExtra(DMDKConstants.PURPOSE,DMDKConstants.PURPOSE_NEW_REGISTER);
    }

    private void setUpListeners() {
        imageVPhoto.setOnClickListener(this);
        autoCompleteTVEntryFormPanchayat.addTextChangedListener(this);
        String[] genderArray= getResources().getStringArray(R.array.genderArray);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, genderArray);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinGender.setAdapter(genderAdapter);
         editTPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if(!hasFocus) {
                    if(TextUtils.isEmpty(editTPhone.getText().toString()) && (!isValidMobile(editTPhone.getText().toString()))){
                        showAlertDialog("Enter valid phone number");
                    }
                 }
             }
         });
        editTDOB.setOnClickListener(this);
        editTPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editTDOB.setText("");
            }

            });
        editTDOB.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if((!TextUtils.isEmpty(editTPhone.getText().toString().trim()) && (!TextUtils.isEmpty(s.toString())))  && (!s.toString().equalsIgnoreCase(tempDOB))){
                    tempDOB= s.toString();
                    if((purpose != DMDKConstants.PURPOSE_UPDATE_RECORDS) && (purpose != DMDKConstants.PURPOSE_PRINT)) {
                        verifyPhoneDOB();
                    }
                }
            }
        });

        btnPreview.setOnClickListener(this);
    }

    private boolean isValidMobile(String phone2)
    {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone2))
        {
            if(phone2.length() < 6 || phone2.length() > 13)
            {
                check = false;
                editTPhone.setError(editTPhone.getText());
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check=false;
        }
        return check;
    }

    private void verifyPhoneDOB() {
        Query queryRef = databaseMemberDataReference.orderByChild("phone").equalTo(editTPhone.getText().toString().trim())/*.orderByChild("dob")*/;
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot childDataSnapshot:dataSnapshot.getChildren()){
                        MemberDatum memberDatum= childDataSnapshot.getValue(MemberDatum.class);
                        if(memberDatum.getDob().equalsIgnoreCase(editTDOB.getText().toString().trim())){
                          //  Toast.makeText(getApplicationContext(),memberDatum.getName()+" Member already found!!!",Toast.LENGTH_SHORT).show();
                            showAlertDialog("Member "+memberDatum.getName()+" already found!!!");
                            btnPreview.setEnabled(false);
                            isPushMemberData = false;
                            return;
                        }else{
                          //  Toast.makeText(getApplicationContext(),"Phone number already used!!",Toast.LENGTH_SHORT).show();
                            if(isClearMenuAvailable) {
                                showAlertDialog("Phone number already used!!");
                                btnPreview.setEnabled(false);
                            }
                            isPushMemberData = false;
                            return;
                        }
                    }

                }else{
                 //   Toast.makeText(getApplicationContext(),"New Memeber ID created!!",Toast.LENGTH_SHORT).show();
                    if(isClearMenuAvailable) {
                        editTMembershipId.setText("" + 10068 + (int) (Math.random() * ((10000 - 1068) + 1)));
                    }
                    isPushMemberData = true;
                    btnPreview.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_focus);
        imageVPhoto = (ImageView) findViewById(R.id.imageVPhoto);
        spinGender = (Spinner)findViewById(R.id.gender);
        autoCompleteTVEntryFormPanchayat = (InstantAutoCompleteTextView)findViewById(R.id.autoCompleteTVPanchayat);
        autoCompleteTVTownCity = (InstantAutoCompleteTextView)findViewById(R.id.autoCompleteTVTownCity);
        autoCompleteTVDistrict = (InstantAutoCompleteTextView)findViewById(R.id.autoCompleteTVDistrict);
      //  dotLoader = (DotLoader) findViewById(R.id.dotLoader);


        few = (RelativeLayout)findViewById( R.id.few );
        editTPhone = (EditText)findViewById( R.id.editTPhone );
        editTName = (EditText)findViewById( R.id.editTName );
        editTDOB = (EditText)findViewById( R.id.editTDOB );
        editTHusband = (EditText)findViewById( R.id.editTHusband );
        editTVoterId = (EditText)findViewById( R.id.editTVoterId );
        editTWard = (EditText)findViewById( R.id.editTWard );
        editTMembershipId = (EditText)findViewById( R.id.editTMembershipId );
        editTDate = (EditText)findViewById( R.id.editTDate );
        editTMLA = (InstantAutoCompleteTextView)findViewById( R.id.editTMLA );
        editTMP = (InstantAutoCompleteTextView)findViewById( R.id.editTMP );

        btnPreview = (Button) findViewById(R.id.btnPreview);

        isBackMenuAvailable= false;
        isPrintMenuAvailable= false;
        isClearMenuAvailable = true;
        invalidateOptionsMenu();

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        editTDate.setText(new StringBuilder()
                .append(mDay).append("/")
                // Month is 0 based so add 1
                .append(mMonth + 1).append("/")
                .append(mYear).append(" "));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.membershipform_menu, menu);
        this.menu= menu;
        return super.onCreateOptionsMenu(menu);
    }
    RelativeLayout idCard;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                View currentFocusedView = getCurrentFocus();
                if (currentFocusedView != null) currentFocusedView.clearFocus();
                if (mLinearLayout != null) {
                    mLinearLayout.requestFocus();
                }
                idCardView = (View) findViewById(idCardLayout);
                idCardView.setVisibility(View.VISIBLE);
                RelativeLayout layout=(RelativeLayout)findViewById(R.id.few);

                layout.removeView(idCard);

                isPrintMenuAvailable= false;
                isBackMenuAvailable = false;
                isClearMenuAvailable = true;
                invalidateOptionsMenu();

                return true;
            case R.id.action_print:
                if(idCard!=null) {
                    Bitmap bitmap = Bitmap.createBitmap(convertDpToPx(332), convertDpToPx(472),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);

                    /* temporrily remove icons borders and secretart signature for printing*/
                    View printPartView= idCard;

                    printPartView.setBackgroundResource(R.drawable.print_part_border);
                    printPartView.findViewById(R.id.imageVFlagIcon).setVisibility(View.INVISIBLE);
                    printPartView.findViewById(R.id.imageVSymbolIcon).setVisibility(View.INVISIBLE);
                    printPartView.findViewById(R.id.textVGeneralSecretary).setVisibility(View.INVISIBLE);


                    printPartView.draw(canvas);

                   /* back to how it is was before sending printing part*/
                    idCard.setBackgroundResource(R.drawable.id_card_border);
                    idCard.setPadding(convertDpToPx(32), convertDpToPx(16),convertDpToPx(32), 0);
                    idCard.findViewById(R.id.imageVFlagIcon).setVisibility(View.VISIBLE);
                    idCard.findViewById(R.id.imageVSymbolIcon).setVisibility(View.VISIBLE);
                    idCard.findViewById(R.id.textVGeneralSecretary).setVisibility(View.VISIBLE);

                    PrintHelper help = new PrintHelper(this);
                    help.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    help.printBitmap("IDCard", bitmap);


                    // old code
                    /*PrintHelper help = new PrintHelper(this);
                    help.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    help.printBitmap("IDCard", bitmap);*/


                    // github library
                    /* ImageAsset */
/*                    ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 3.46f,4.92f);
                    *//* PrintItem *//*
                 //   PrintItem printItemDefault = new ImagePrintItem(PrintItem.ScaleType.CENTER, bitmapAsset);
                    PrintItem printItemB7 = new ImagePrintItem(PrintAttributes.MediaSize.ISO_B7, PrintItem.ScaleType.FIT, bitmapAsset);

                    *//*PrintJobData*//*
                    PrintJobData printJobData = new PrintJobData(this, printItemB7);
                    printJobData.setJobName("Example");

                    PrintAttributes printAttributes = new PrintAttributes.Builder()
                            .setMediaSize(printItemB7.getMediaSize())
                            .build();
                    printJobData.setPrintDialogOptions(printAttributes);



                    PrintUtil.setPrintJobData(printJobData);


                    if(Build.VERSION.SDK_INT >= 19) {
                        Intent intent = new Intent(this, PrintPluginManagerActivity.class);
                        startActivity(intent);
                    }*/

                    /*Print*/
                    /*PrintUtil.print(this);*/

                    // posifix code
                    /*USBAdapter usbAdapter = new USBAdapter();
                    usbAdapter.printBitmap(this,bitmap);*/
                }
                return true;
            case R.id.clear_form:

                ViewGroup group = (ViewGroup)findViewById(idCardLayout);
                for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                    View view = group.getChildAt(i);
                    if (view instanceof EditText) {
                        ((EditText)view).setText("");
                    }
                }

                imageVPhoto.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
                return true;
            /*case R.id.action_options:

                return true;
            */
            case R.id.action_about:
                if(idCard!=null) {
                    Bitmap bitmap = Bitmap.createBitmap(convertDpToPx(332), convertDpToPx(472),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    idCard.draw(canvas);

                    /* ImageAsset */
                    ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 3.46f,4.92f);
                 //   PrintItem printItemDefault = new ImagePrintItem(PrintItem.ScaleType.CENTER, bitmapAsset);
                    PrintItem printItemB7 = new ImagePrintItem(PrintAttributes.MediaSize.ISO_B7, PrintItem.ScaleType.FIT, bitmapAsset);


                    PrintJobData printJobData = new PrintJobData(this, printItemB7);
                    printJobData.setJobName("Example");

                    PrintAttributes printAttributes = new PrintAttributes.Builder()
                            .setMediaSize(printItemB7.getMediaSize())
                            .build();
                    printJobData.setPrintDialogOptions(printAttributes);



                    PrintUtil.setPrintJobData(printJobData);


                    if(Build.VERSION.SDK_INT >= 19) {
                        Intent intent = new Intent(this, PrintPluginManagerActivity.class);
                        startActivity(intent);
                    }

                    /*Print*/
                //    PrintUtil.print(this);
                }
                /*Toast.makeText(this,"This is a demo app, work in progress!",Toast.LENGTH_SHORT).show();*/
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem itemPrint = menu.findItem(R.id.action_print);
        MenuItem itemBack = menu.findItem(R.id.back);
        MenuItem itemClear = menu.findItem(R.id.clear_form);


        enablePrintMenu(itemPrint,isPrintMenuAvailable);
        enableMenu(itemBack,isBackMenuAvailable);
        enableMenu(itemClear,isClearMenuAvailable);

        return true;
    }

    /* enable menu and change alpha value */
    private void enablePrintMenu(MenuItem item,boolean isMenuAvailable) {
        if (isMenuAvailable) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            // disabled
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }
    }

    /* enable menu and change visibility */
    private void enableMenu(MenuItem item,boolean isMenuAvailable) {
        if (isMenuAvailable) {
            item.setEnabled(true);
            item.setVisible(true);
        } else {
            // disabled
            item.setEnabled(false);
            item.setVisible(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageVPhoto:
                takePhoto();
                break;
            case R.id.editTDOB:
                showStartDateDialog();
                break;
            case R.id.btnPreview:
                if((purpose != DMDKConstants.PURPOSE_UPDATE_RECORDS) && (purpose != DMDKConstants.PURPOSE_PRINT)) {
                    verifyPhoneDOB();
                }

                if(selectedPanchayat==null){
                    showAlertDialog("Panchayat/Town/City/District details manually typed. Tamil Translation not available!");
                }

                // show uneditable preview
                if (isAllFieldsFilled()) {
                    //  activate print menu and back menu
                    isPrintMenuAvailable = true;
                    isBackMenuAvailable = true;
                    isClearMenuAvailable = false;
                    invalidateOptionsMenu();

                    setLocale("ta");

                    // storing to database
                    if(isPushMemberData) {
                        storingToDatabase();
                    }
                    // end of storing to database

                    idCardView = (View) findViewById(idCardLayout);
                    idCardView.setVisibility(View.GONE);
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.few);


                    idCard = getIDCard();
                    idCardRId = View.generateViewId();
                    idCard.setId(idCardRId);
                    layout.addView(idCard);
                    imageVPhotoPrint.setImageDrawable(imageVPhoto.getDrawable());

                    // clearing any focus
                    View currentFocusedView = getCurrentFocus();
                    if (currentFocusedView != null) currentFocusedView.clearFocus();
                    if (mLinearLayout != null) {
                        mLinearLayout.requestFocus();
                    }

                    hideKeyboard(this);

                }else{
                   showAlertDialog("Some fields are missing!");
                }
                break;

        }
    }

    private void storingToDatabase() {
        MemberDatum memberDatum = new MemberDatum();
        memberDatum.setPhone(editTPhone.getText().toString());
        memberDatum.setDob(editTDOB.getText().toString());
        memberDatum.setMembershipId(editTMembershipId.getText().toString());
        memberDatum.setName(editTName.getText().toString());
        memberDatum.setHusbandName(editTHusband.getText().toString());
        String[] genderArray= getResources().getStringArray(R.array.genderArray);
        if(spinGender.getSelectedItemPosition()>0) {
            memberDatum.setGender(genderArray[spinGender.getSelectedItemPosition()]);
        }else{
            memberDatum.setGender("-");
        }
        memberDatum.setVoterId(editTVoterId.getText().toString());
        if(selectedMpMlaDistrict!=null) {
            memberDatum.setMp(selectedMpMlaDistrict.getMp_tamil());
            memberDatum.setMla(selectedMpMlaDistrict.getMla_tamil());
        }
        else{
            memberDatum.setMp(editTMP.getText().toString());
            memberDatum.setMla(editTMLA.getText().toString());
        }
        if(selectedPanchayat!=null) {
            memberDatum.setPanchayat(selectedPanchayat.getVillage_panchayat_name_tamil());
        }
        else{
            memberDatum.setPanchayat(autoCompleteTVEntryFormPanchayat.getText().toString().trim());
        }

        if(selectedPanchayat!=null) {
            memberDatum.setTownCity(selectedPanchayat.getDistrict_name_tamil());
        }
        else{
            memberDatum.setTownCity(autoCompleteTVTownCity.getText().toString().trim());
        }

        if(selectedPanchayat!=null) {
            memberDatum.setDistrict(selectedPanchayat.getDistrict_name_tamil());
        }
        else{
            memberDatum.setDistrict(autoCompleteTVTownCity.getText().toString().trim());
        }

        memberDatum.setWard(editTWard.getText().toString());
        memberDatum.setDoj(editTDate.getText().toString());

        databaseMemberDataReference.push().setValue(memberDatum);
    }

    private boolean isAllFieldsFilled() {
        ViewGroup group = (ViewGroup)findViewById(idCardLayout);
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                if(TextUtils.isEmpty(((EditText)view).getText().toString().trim())){
                    return false;
                }
            }
        }
        return true;
    }


    public void printPDF(View view) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize( PrintAttributes.MediaSize.ISO_B7);

        printManager.print("print_any_view_job_name", new ViewPrintAdapter(this,
                view), builder.build());
    }
    @TargetApi(19)
    private void generatePdf() {
        int widthPostScript = (int) 3.56 * 72;
        int heighPostScript = (int) 4.94 * 72;

        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_B7);// or ISO_B7
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
/*
        builder.setResolution(PrintAttributes.Resolution.ISO_B7);
*/


        PrintedPdfDocument document = new PrintedPdfDocument(this, builder.build());

        Page page = document.startPage(1);
        idCardView = (View) findViewById(idCardLayout);

        View content = idCardView;

        Canvas canvas = page.getCanvas();

        float scaleWidth = (float)332.16/(float)content.getWidth();
        float scaleHeight = (float)472.32/(float)content.getHeight();
        canvas.scale(scaleWidth, scaleHeight);


        content.draw(canvas);
        document.finishPage(page);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //handle case of no SDCARD present
        } else {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();

            //create file
            String pdfName = "idcard_pdf"
                /*+ sdf.format(Calendar.getInstance().getTime()) */ + ".pdf";
            try {
                File outputFile = new File(dir, pdfName);

                Prefs.putString(DMDKConstants.IDCARD_PDF_PATH, outputFile.getPath());

/*
                File file = new File(getExternalFilesDir(null).getAbsolutePath(), "document22.pdf");
*/
                document.writeTo(new FileOutputStream(outputFile));

            } catch (IOException e) {
                Log.e("cannot generate pdf", e.toString());
            }
        }
        document.close();
    }

    private boolean generateIDCardPDF() {


        //   Toast.makeText(this,"Printing the ID card",Toast.LENGTH_SHORT).show();
        // create a new document
        PdfDocument document = new PdfDocument();

        int widthPostScript = (int) 3 * 72;
        int heighPostScript = (int) 4 * 72;

        // crate a page description
        PageInfo pageInfo = new PageInfo.Builder(widthPostScript, heighPostScript, 1).create();


        // start a page
        Page page = document.startPage(pageInfo);

        idCardView = (View) findViewById(idCardLayout);


        Canvas canvas = page.getCanvas();

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        float scaleWidth = (float)widthPostScript/(float)(idCardView.getMeasuredWidth()*72/dm.xdpi);
        float scaleHeight = (float)heighPostScript/(float)(idCardView.getMeasuredHeight()*72/dm.xdpi);
        canvas.scale(scaleWidth, scaleHeight);

        idCardView.draw(canvas);

        // finish the page
        document.finishPage(page);
        // add more pages
        // write the document content
        //  document.writeTo(getOutputStream());

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //handle case of no SDCARD present
        } else {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();

            //create file
            String pdfName = "idcard_pdf"
                /*+ sdf.format(Calendar.getInstance().getTime()) */ + ".pdf";

            File outputFile = new File(dir, pdfName);

            Prefs.putString(DMDKConstants.IDCARD_PDF_PATH, outputFile.getPath());

            try {
                outputFile.createNewFile();
                OutputStream out = new FileOutputStream(outputFile);
                document.writeTo(out);
                // close the document
                document.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
        }

        return true;

    }


    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        startService(new Intent(this, PrintJobMonitorService.class));

        return (mgr.print(name, adapter, attrs));
    }


    private void takePhoto() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        }else {
            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                marshMallowPermission.requestPermissionForExternalStorage();
            } else {
                file = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "/test.jpg"/*getFilesDir(), "member_photo.jpg"*/);
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent i = new CameraActivity.IntentBuilder(this)
                        .skipConfirm()
                        .facing(Facing.BACK)
                        .to(Uri.fromFile(file))
                        .zoomStyle(ZoomStyle.SEEKBAR)
                        .updateMediaStore()
                        .build();
                startActivityForResult(i, DMDKConstants.REQUEST_PICTURE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == DMDKConstants.REQUEST_PICTURE) {
            if (resultCode == RESULT_OK) {
                memberBitmap = (Bitmap) data.getParcelableExtra("data");
                if(memberBitmap != null) {
                    imageVPhoto.setImageBitmap(memberBitmap);
                }else{
                    // app flow this way always as we provide target file location in intentbuilder
                    setPic(imageVPhoto);
                }
            } else {
                imageVPhoto.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
            }
        }

    }

    private void setPic(ImageView mImageView) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
        mImageView.setImageBitmap(bitmap);
    }




    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
      //  MembershipEntryActivity.this.recreate();
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "IDCardPreview Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.de.dmdk/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocaleHelper.setLocale(this,"en");
    }




    private class PDFGeneration extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            return generateIDCardPDF();
        }

        @Override
        protected void onPostExecute(Boolean result) {
 //           dotLoader.setVisibility(View.GONE);
            if(result) {
                print("TEST IDCARD PDF",
                        new PdfDocumentAdapter(getApplicationContext()),
                        new PrintAttributes.Builder().build());
            }
        }

        @Override
        protected void onPreExecute() {
//            dotLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

       /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    private  RelativeLayout getIDCard(){

        RelativeLayout idCardLayout = new RelativeLayout(this);
        idCardLayout.setId(R.id.idCardLayout);
        idCardLayout.setBackgroundResource(R.drawable.id_card_border);
        idCardLayout.setPadding(convertDpToPx(32), convertDpToPx(16),convertDpToPx(32), 0);
        RelativeLayout.LayoutParams layout_506 = new RelativeLayout.LayoutParams(convertDpToPx(332),convertDpToPx(472));
        layout_506.addRule(RelativeLayout.CENTER_IN_PARENT);
        idCardLayout.setLayoutParams(layout_506);

        ImageView imageVFlagIcon = new ImageView(this);
        imageVFlagIcon.setId(R.id.imageVFlagIcon);
        imageVFlagIcon.setImageResource(R.drawable.idcard_logo_flag);
        RelativeLayout.LayoutParams layout_621 = new RelativeLayout.LayoutParams(convertDpToPx(50),convertDpToPx(50));
        layout_621.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layout_621.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layout_621.width = convertDpToPx(50);
        layout_621.height = convertDpToPx(50);
        imageVFlagIcon.setLayoutParams(layout_621);
        idCardLayout.addView(imageVFlagIcon);

        ImageView imageVSymbolIcon = new ImageView(this);
        imageVSymbolIcon.setId(R.id.imageVSymbolIcon);
        imageVSymbolIcon.setImageResource(R.drawable.idcard_logo_symbol);
        RelativeLayout.LayoutParams layout_565 = new RelativeLayout.LayoutParams(convertDpToPx(50),convertDpToPx(50));
        layout_565.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layout_565.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layout_565.width = convertDpToPx(50);
        layout_565.height = convertDpToPx(50);
        imageVSymbolIcon.setLayoutParams(layout_565);
        idCardLayout.addView(imageVSymbolIcon);

        TextView textVName = new TextView(this);
        textVName.setId(R.id.textVName);
        textVName.setText(getResources().getString(R.string.name));
        textVName.setTextColor(getResources().getColor(R.color.colorFont));
        textVName.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_391 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_391.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_391.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_391.addRule(RelativeLayout.BELOW, R.id.imageVFlagIcon);

        textVName.setLayoutParams(layout_391);
        idCardLayout.addView(textVName);

        EditText editTNamePrint = new EditText(this);
        editTNamePrint.setId(R.id.editTName);
        editTNamePrint.setEms(20);
        editTNamePrint.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        editTNamePrint.setLines(1);
        editTNamePrint.setText(editTName.getText().toString().trim());
        setEditTextEditable(editTNamePrint, false);
        editTNamePrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_447 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_447.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        
        layout_447.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_447.addRule(RelativeLayout.RIGHT_OF, R.id.textVName);
        layout_447.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVName);

        editTNamePrint.setLayoutParams(layout_447);
        idCardLayout.addView(editTNamePrint);

        TextView textVAge = new TextView(this);
        textVAge.setId(R.id.textVAge);
        textVAge.setText(getResources().getString(R.string.dob));
        textVAge.setTextColor(getResources().getColor(R.color.colorFont));
        textVAge.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_131 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_131.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_131.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_131.addRule(RelativeLayout.BELOW, R.id.textVName);
        layout_131.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVName);
        layout_131.rightMargin = convertDpToPx(8);
        layout_131.topMargin = convertDpToPx(15);
        textVAge.setLayoutParams(layout_131);
        idCardLayout.addView(textVAge);

        EditText editTAgePrint = new EditText(this);
        editTAgePrint.setId(R.id.editTAge);
        editTAgePrint.setEms(6);
        editTAgePrint.setText(editTDOB.getText().toString().trim());
        setEditTextEditable(editTAgePrint, false);
        editTAgePrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTAgePrint.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        RelativeLayout.LayoutParams layout_192 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_192.addRule(RelativeLayout.RIGHT_OF, R.id.textVAge);
        layout_192.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVAge);
        layout_192.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_192.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_192.rightMargin = convertDpToPx(8);
        editTAgePrint.setLayoutParams(layout_192);
        idCardLayout.addView(editTAgePrint);

        TextView textVGender = new TextView(this);
        textVGender.setId(R.id.textVGender);
        textVGender.setText(getResources().getString(R.string.gender));
        textVGender.setTextColor(getResources().getColor(R.color.colorFont));
        textVGender.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_297 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_297.addRule(RelativeLayout.RIGHT_OF, R.id.editTAge);
        layout_297.addRule(RelativeLayout.ALIGN_BASELINE, R.id.editTAge);
        layout_297.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_297.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_297.rightMargin = convertDpToPx(8);
        textVGender.setLayoutParams(layout_297);
        idCardLayout.addView(textVGender);


        EditText editGender = new EditText(this);
        editGender.setId(R.id.spinGender);
        editTNamePrint.setEms(4);
        editGender.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        String[] genderArray= getResources().getStringArray(R.array.genderArray);
        if(spinGender.getSelectedItemPosition()>0) {
            editGender.setText(genderArray[spinGender.getSelectedItemPosition()]);
        }else{
            editGender.setText("-");
        }
        setEditTextEditable(editGender, false);
        RelativeLayout.LayoutParams layout_803 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_803.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_803.addRule(RelativeLayout.RIGHT_OF, R.id.textVGender);
        layout_803.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVGender);
        layout_803.rightMargin = convertDpToPx(8);
        editGender.setLayoutParams(layout_803);
        idCardLayout.addView(editGender);

        TextView textVHusband = new TextView(this);
        textVHusband.setId(R.id.textVHusband);
        textVHusband.setText(getResources().getString(R.string.husband_name));
        textVHusband.setTextColor(getResources().getColor(R.color.colorFont));
        textVHusband.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_19 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_19.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_19.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_19.addRule(RelativeLayout.BELOW, R.id.textVAge);
        layout_19.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVAge);
        layout_19.rightMargin = convertDpToPx(8);
        layout_19.topMargin = convertDpToPx(15);
        textVHusband.setLayoutParams(layout_19);
        idCardLayout.addView(textVHusband);

        EditText editTHusbandPrint = new EditText(this);
        editTHusbandPrint.setId(R.id.editTHusband);
        editTHusbandPrint.setEms(20);
        editTHusbandPrint.setText(editTHusband.getText().toString().trim());
        setEditTextEditable(editTHusbandPrint, false);
        editTHusbandPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTHusbandPrint.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        RelativeLayout.LayoutParams layout_752 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_752.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_752.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_752.addRule(RelativeLayout.RIGHT_OF, R.id.textVHusband);
        layout_752.addRule(RelativeLayout.BELOW, R.id.spinGender);
        layout_752.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVHusband);
        layout_752.rightMargin = convertDpToPx(8);
        editTHusbandPrint.setLayoutParams(layout_752);
        idCardLayout.addView(editTHusbandPrint);

        TextView textVVoterId = new TextView(this);
        textVVoterId.setId(R.id.textVVoterId);
        textVVoterId.setText(getResources().getString(R.string.voter_id));
        textVVoterId.setTextColor(getResources().getColor(R.color.colorFont));
        textVVoterId.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_760 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_760.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_760.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_760.addRule(RelativeLayout.BELOW, R.id.textVHusband);
        layout_760.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVHusband);
        layout_760.rightMargin = convertDpToPx(8);
        layout_760.topMargin = convertDpToPx(15);
        textVVoterId.setLayoutParams(layout_760);
        idCardLayout.addView(textVVoterId);

        EditText editTVoterIdPrint = new EditText(this);
        editTVoterIdPrint.setId(R.id.editTVoterId);
        editTVoterIdPrint.setEms(20);
        editTVoterIdPrint.setText(editTVoterId.getText().toString().trim());
        setEditTextEditable(editTVoterIdPrint, false);
        editTVoterIdPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTVoterIdPrint.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        RelativeLayout.LayoutParams layout_357 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_357.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_357.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_357.addRule(RelativeLayout.RIGHT_OF, R.id.textVVoterId);
        layout_357.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVVoterId);
        layout_357.rightMargin = convertDpToPx(8);
        editTVoterIdPrint.setLayoutParams(layout_357);
        idCardLayout.addView(editTVoterIdPrint);

        TextView textVWard = new TextView(this);
        textVWard.setId(R.id.textVWard);
        textVWard.setText(getResources().getString(R.string.ward));
        textVWard.setTextColor(getResources().getColor(R.color.colorFont));
        textVWard.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_61 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_61.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_61.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_61.addRule(RelativeLayout.BELOW, R.id.textVVoterId);
        layout_61.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVVoterId);
        layout_61.rightMargin = convertDpToPx(8);
        layout_61.topMargin = convertDpToPx(15);
        textVWard.setLayoutParams(layout_61);
        idCardLayout.addView(textVWard);

        EditText editTWardPrint = new EditText(this);
        editTWardPrint.setId(R.id.editTWard);
        editTWardPrint.setEms(20);
        editTWardPrint.setText(editTWard.getText().toString().trim());
        setEditTextEditable(editTWardPrint, false);
        editTWardPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTWardPrint.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        RelativeLayout.LayoutParams layout_611 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_611.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_611.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_611.addRule(RelativeLayout.RIGHT_OF, R.id.textVWard);
        layout_611.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVWard);
        layout_611.rightMargin = convertDpToPx(8);
        editTWardPrint.setLayoutParams(layout_611);
        idCardLayout.addView(editTWardPrint);

        TextView textVPanchayat = new TextView(this);
        textVPanchayat.setId(R.id.textVPanchayat);
        textVPanchayat.setText(getResources().getString(R.string.panchayat));
        textVPanchayat.setTextColor(getResources().getColor(R.color.colorFont));
        textVPanchayat.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_695 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_695.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_695.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_695.addRule(RelativeLayout.BELOW, R.id.textVWard);
        layout_695.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVWard);
        layout_695.rightMargin = convertDpToPx(8);
        layout_695.topMargin = convertDpToPx(15);
        textVPanchayat.setLayoutParams(layout_695);
        idCardLayout.addView(textVPanchayat);

        InstantAutoCompleteTextView autoCompleteTVPanchayatPrint = new InstantAutoCompleteTextView(this);
        autoCompleteTVPanchayatPrint.setId(R.id.autoCompleteTVPanchayatPrint);
        autoCompleteTVPanchayatPrint.setEms(12);
        if(selectedPanchayat!=null) {
            autoCompleteTVPanchayatPrint.setText(selectedPanchayat.getVillage_panchayat_name_tamil());
        }
        else{
            autoCompleteTVPanchayatPrint.setText(autoCompleteTVEntryFormPanchayat.getText().toString().trim());
        }
        setEditTextEditable(autoCompleteTVPanchayatPrint, false);
        autoCompleteTVPanchayatPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_80 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_80.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_80.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_80.addRule(RelativeLayout.RIGHT_OF, R.id.textVPanchayat);
        layout_80.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVPanchayat);
        layout_80.rightMargin = convertDpToPx(8);
        autoCompleteTVPanchayatPrint.setLayoutParams(layout_80);
        idCardLayout.addView(autoCompleteTVPanchayatPrint);

        TextView textVTownCity = new TextView(this);
        textVTownCity.setId(R.id.textVTownCity);
        textVTownCity.setText(getResources().getString(R.string.town_city));
        textVTownCity.setTextColor(getResources().getColor(R.color.colorFont));
        textVTownCity.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_528 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_528.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_528.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_528.addRule(RelativeLayout.BELOW, R.id.textVPanchayat);
        layout_528.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVPanchayat);
        layout_528.rightMargin = convertDpToPx(8);
        layout_528.topMargin = convertDpToPx(15);
        textVTownCity.setLayoutParams(layout_528);
        idCardLayout.addView(textVTownCity);

        InstantAutoCompleteTextView autoCompleteTVTownCityPrint = new InstantAutoCompleteTextView(this);
        autoCompleteTVTownCityPrint.setId(R.id.autoCompleteTVTownCityPrint);
        autoCompleteTVTownCityPrint.setEms(8);
        if(selectedPanchayat!=null) {
            autoCompleteTVTownCityPrint.setText(selectedPanchayat.getDistrict_name_tamil());
        }
        else{
            autoCompleteTVTownCityPrint.setText(autoCompleteTVTownCity.getText().toString().trim());
        }
        autoCompleteTVTownCityPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        setEditTextEditable(autoCompleteTVTownCityPrint, false);



        RelativeLayout.LayoutParams layout_656 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_656.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_656.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_656.addRule(RelativeLayout.RIGHT_OF, R.id.textVTownCity);
        layout_656.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVTownCity);
        layout_656.rightMargin = convertDpToPx(8);
        autoCompleteTVTownCityPrint.setLayoutParams(layout_656);
        idCardLayout.addView(autoCompleteTVTownCityPrint);

        TextView textVDistrict = new TextView(this);
        textVDistrict.setId(R.id.textVDistrict);
        textVDistrict.setText(getResources().getString(R.string.district));
        textVDistrict.setTextColor(getResources().getColor(R.color.colorFont));
        textVDistrict.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_45 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_45.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_45.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_45.addRule(RelativeLayout.BELOW, R.id.textVTownCity);
        layout_45.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVTownCity);
        layout_45.rightMargin = convertDpToPx(8);
        layout_45.topMargin = convertDpToPx(15);
        textVDistrict.setLayoutParams(layout_45);
        idCardLayout.addView(textVDistrict);

        InstantAutoCompleteTextView autoCompleteTVDistrictPrint = new InstantAutoCompleteTextView(this);
        autoCompleteTVDistrictPrint.setId(R.id.autoCompleteTVDistrictPrint);
        autoCompleteTVDistrictPrint.setEms(8);
        if(selectedPanchayat!=null) {
            autoCompleteTVDistrictPrint.setText(selectedPanchayat.getDistrict_name_tamil());
        }
        else{
            autoCompleteTVDistrictPrint.setText(autoCompleteTVDistrict.getText().toString().trim());
        }
        autoCompleteTVDistrictPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        setEditTextEditable(autoCompleteTVDistrictPrint, false);
        RelativeLayout.LayoutParams layout_147 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_147.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_147.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_147.addRule(RelativeLayout.RIGHT_OF, R.id.textVDistrict);
        layout_147.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVDistrict);
        layout_147.rightMargin = convertDpToPx(8);
        autoCompleteTVDistrictPrint.setLayoutParams(layout_147);
        idCardLayout.addView(autoCompleteTVDistrictPrint);

        TextView textVMembershipId = new TextView(this);
        textVMembershipId.setId(R.id.textVMembershipId);
        textVMembershipId.setText(getResources().getString(R.string.membership_id));
        textVMembershipId.setTextColor(getResources().getColor(R.color.colorFont));
        textVMembershipId.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_961 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_961.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_961.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_961.addRule(RelativeLayout.BELOW, R.id.textVDistrict);
        layout_961.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVDistrict);
        layout_961.rightMargin = convertDpToPx(8);
        layout_961.topMargin = convertDpToPx(15);
        layout_961.bottomMargin = convertDpToPx(10);
        textVMembershipId.setLayoutParams(layout_961);
        idCardLayout.addView(textVMembershipId);

        EditText editTMembershipIdPrint = new EditText(this);
        editTMembershipIdPrint.setId(R.id.editTMembershipIdPrint);
        editTMembershipIdPrint.setEms(5);
        editTMembershipIdPrint.setText(editTMembershipId.getText().toString().trim());
        setEditTextEditable(editTMembershipIdPrint, false);
        editTMembershipIdPrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTMembershipIdPrint.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        RelativeLayout.LayoutParams layout_302 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_302.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_302.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_302.addRule(RelativeLayout.RIGHT_OF, R.id.textVMembershipId);
        layout_302.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVMembershipId);
        layout_302.rightMargin = convertDpToPx(8);
        editTMembershipIdPrint.setLayoutParams(layout_302);
        idCardLayout.addView(editTMembershipIdPrint);

        TextView textVdate = new TextView(this);
        textVdate.setId(R.id.textVdate);
        textVdate.setText(getResources().getString(R.string.date));
        textVdate.setTextColor(getResources().getColor(R.color.colorFont));
        textVdate.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_70 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_70.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_70.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_70.addRule(RelativeLayout.RIGHT_OF, R.id.editTMembershipIdPrint);
        layout_70.addRule(RelativeLayout.ALIGN_BASELINE, R.id.editTMembershipIdPrint);
        layout_70.rightMargin = convertDpToPx(8);
        layout_70.topMargin = convertDpToPx(15);
        textVdate.setLayoutParams(layout_70);
        idCardLayout.addView(textVdate);

        EditText editTDatePrint = new EditText(this);
        editTDatePrint.setId(R.id.editTDatingPrint);
        editTDatePrint.setText(editTDate.getText().toString().trim());
        setEditTextEditable(editTDatePrint, false);
        editTDatePrint.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        editTDatePrint.setEms(7);
        RelativeLayout.LayoutParams layout_112 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_112.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_112.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_112.addRule(RelativeLayout.RIGHT_OF, R.id.textVdate);
        layout_112.addRule(RelativeLayout.ALIGN_BASELINE, R.id.textVdate);
        editTDatePrint.setLayoutParams(layout_112);
        idCardLayout.addView(editTDatePrint);


        imageVPhotoPrint = new ImageView(this);
        imageVPhotoPrint.setId(R.id.imageVPhoto);
        RelativeLayout.LayoutParams layout_63 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_63.addRule(RelativeLayout.ALIGN_LEFT, R.id.textVMembershipId);
        layout_63.addRule(RelativeLayout.BELOW, R.id.textVMembershipId);
        layout_63.width = convertDpToPx(70);
        layout_63.height = convertDpToPx(80);
        layout_63.bottomMargin = convertDpToPx(18);
        imageVPhotoPrint.setLayoutParams(layout_63);
        idCardLayout.addView(imageVPhotoPrint);

        TextView textVGeneralSecretary = new TextView(this);
        textVGeneralSecretary.setId(R.id.textVGeneralSecretary);
        textVGeneralSecretary.setText(getResources().getString(R.string.general_secretary));
        textVGeneralSecretary.setTextColor(getResources().getColor(R.color.colorFont));
        textVGeneralSecretary.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        RelativeLayout.LayoutParams layout_452 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout_452.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_452.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout_452.bottomMargin = convertDpToPx(18);
        layout_452.setMarginEnd(convertDpToPx(36));
        layout_452.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout_452.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        textVGeneralSecretary.setLayoutParams(layout_452);
        idCardLayout.addView(textVGeneralSecretary);

        return idCardLayout;

    }

    private int convertDpToPx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());

    }

    private void setEditTextEditable(EditText editText,boolean yes){
        if (!yes) { // disable editing password
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            editText.setClickable(false); // user navigates with wheel and selects widget
        } else { // enable editing of password
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setClickable(true);
        }
    }

    public void showStartDateDialog(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(this,android.R.style.Theme_Holo_Dialog,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

/*    public void showAlertDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }*/

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            editTDOB.setText(new StringBuilder()
                    .append(mDay).append("/")
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("/")
                    .append(mYear).append(" "));
            System.out.println(editTDOB.getText().toString());


        }
    }

    @Override
    public void onBackPressed() {
        if(isBackMenuAvailable){
            setLocale("en");
            MenuItem menuBack = menu.findItem(R.id.back);
            onOptionsItemSelected(menuBack);
        }else {
            super.onBackPressed();
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


  /*  void printTemplateSample()
    {
        Printer myPrinter = new Printer();
        PrinterInfo myPrinterInfo = new PrinterInfo();

        try{
            // Retrieve printer informations
            myPrinterInfo = myPrinter.getPrinterInfo();

            // Set printer informations
            myPrinterInfo.printerModel = PrinterInfo.Model.PJ_663;
            myPrinterInfo.port=PrinterInfo.Port.BLUETOOTH;
            myPrinterInfo.printMode=PrinterInfo.PrintMode.ORIGINAL;

            myPrinterInfo.paperSize = PrinterInfo.PaperSize.A4;

            myPrinter.setPrinterInfo(myPrinterInfo);

            // Start creating P-touch Template command print data
            myPrinter.startPTTPrint(1, null);

            // Replace text
            myPrinter.replaceText("abcde");
            myPrinter.replaceText("12345");

            // Trasmit P-touch Template command print data
            myPrinter.flushPTTPrint();

        }catch(Exception e){
        }
    }*/


    private void showDialogs(String title,String message){
        LayoutInflater linf = LayoutInflater.from(this);
        final View inflator = linf.inflate(R.layout.search_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.DialogTheme);

        alert.setTitle(title);
        /*alert.setMessage(message);*/
        alert.setView(inflator);

        final EditText et1 = (EditText) inflator.findViewById(R.id.editTSearchMemberId);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                if(!TextUtils.isEmpty(et1.getText())) {
                    searchMember(et1.getText().toString().trim());
                }else{
                    showAlertDialog("Please enter memberid");
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                finish();
            }
        });

        alert.show();
    }

    /* Search member */

    private void searchMember(final String memberID) {
        showProgress("");
        Query queryRef = databaseMemberDataReference.orderByChild("membershipId").equalTo(memberID)/*.orderByChild("dob")*/;
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    dismissProgress();
                    for(DataSnapshot childDataSnapshot:dataSnapshot.getChildren()){
                        MemberDatum memberDatum= childDataSnapshot.getValue(MemberDatum.class);
                        if(memberDatum.getMembershipId().equalsIgnoreCase(memberID)){
                            //  Toast.makeText(getApplicationContext(),memberDatum.getName()+" Member already found!!!",Toast.LENGTH_SHORT).show();
                            showAlertDialog("Member "+memberDatum.getName()+" found!!!");
                            if(purpose == DMDKConstants.PURPOSE_UPDATE_RECORDS) {
                                btnPreview.setText("Update");
                            }else if(purpose == DMDKConstants.PURPOSE_PRINT){
                                btnPreview.setText("Print");
                            }
                            btnPreview.setEnabled(true);

                            fillData(memberDatum);

                            isPushMemberData = true;
                            return;
                        }else{
                            //  Toast.makeText(getApplicationContext(),"Phone number already used!!",Toast.LENGTH_SHORT).show();
                            if(isClearMenuAvailable) {
                                showAlertDialog("Member "+memberID+" not found!!!");
                                if(purpose == DMDKConstants.PURPOSE_UPDATE_RECORDS) {
                                    btnPreview.setText("Update");
                                }else if(purpose == DMDKConstants.PURPOSE_PRINT){
                                    btnPreview.setText("Print");
                                }                                btnPreview.setEnabled(false);
                            }
                            isPushMemberData = false;
                            return;
                        }
                    }

                }else{
                    dismissProgress();
                    if(isClearMenuAvailable) {
                        showAlertDialog("Member "+memberID+" not found!!!");
                        if(purpose == DMDKConstants.PURPOSE_UPDATE_RECORDS) {
                            btnPreview.setText("Update");
                        }else if(purpose == DMDKConstants.PURPOSE_PRINT){
                            btnPreview.setText("Print");
                        }                        btnPreview.setEnabled(false);
                    }
                    isPushMemberData = false;
                    return;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgress();

            }
        });
    }

    private void fillData(MemberDatum memberDatum) {
        editTPhone.setText(memberDatum.getPhone());
        editTDOB.setText(memberDatum.getDob());
        editTMembershipId.setText(memberDatum.getMembershipId());
        editTName.setText(memberDatum.getName());
        editTHusband.setText(memberDatum.getHusbandName());
        editTVoterId.setText(memberDatum.getVoterId());
        editTMP.setText(memberDatum.getMp());
        editTMLA.setText(memberDatum.getMla());
        autoCompleteTVDistrict.setText(memberDatum.getDistrict());
        autoCompleteTVEntryFormPanchayat.setText(memberDatum.getPanchayat());
        autoCompleteTVTownCity.setText(memberDatum.getTownCity());
        editTDate.setText(memberDatum.getDoj());
    }


}
