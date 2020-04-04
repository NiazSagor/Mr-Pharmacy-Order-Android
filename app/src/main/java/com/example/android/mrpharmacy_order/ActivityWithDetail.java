package com.example.android.mrpharmacy_order;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mrpharmacy_order.Model.BottomSheet;
import com.example.android.mrpharmacy_order.Model.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ActivityWithDetail extends AppCompatActivity implements BottomSheet.BottomSheetListener {


    Button button;//Take order button

    DatabaseReference databaseReference;//Ref for Prescription Uploads database

    DatabaseReference databaseReference1;//Ref for current delivery man

    SharedPreferences sharedPreferences;//For getting the saved phone number
    SharedPreferences sp_TakeOrderButtonClicked;//For storing the state of take order button

    ImageView imageView;//Prescription image view
    TextView imagename, username, phone, address, detail;//Details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_detail);

        sp_TakeOrderButtonClicked = getSharedPreferences("isClicked", MODE_PRIVATE);//Making sp

        //If already clicked then show the bottom sheet if he reached the destination of order
        if (sp_TakeOrderButtonClicked.getBoolean("clicked", false)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "example");
        }

        //Getting the current selected id from the With Fragment
        final String code = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription Uploads").child(code);//By this code we are entering into the details

        button = findViewById(R.id.takeOrder);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("mStatus").setValue("Order is taken");//Changing the status as taken
                sp_TakeOrderButtonClicked.edit().putBoolean("clicked", true).apply();//True if button clicked
                getValue();//Addition of 1 of Total count for the delivery man
            }
        });

        getViews();
        setDatabaseStatus();
        setViews();
    }

    //Getting the views
    private void getViews() {
        imageView = findViewById(R.id.image);
        imagename = findViewById(R.id.imagename);
        detail = findViewById(R.id.detail);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
    }

    //Setting the view with res
    private void setViews() {
        imagename.setText(Common.currentUpload.getName());
        detail.setText(Common.currentUpload.getDetails());
        username.setText(Common.currentUpload.getmUserName());
        phone.setText(Common.currentUpload.getUserPhone());
        address.setText(Common.currentUpload.getmUserAddress());

        Picasso.get().load(Common.currentUpload.getImageUrl()).into(imageView);
    }

    //Grabbing the database status and setting button color according to it
    private void setDatabaseStatus() {
        databaseReference.child("mStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String string = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                if (string.equals("Pending")) {
                    button.setBackgroundColor(Color.GREEN);//If available make it green
                } else if (string.equals("Order is taken")) {
                    button.setBackgroundColor(Color.RED);//If not, make it red
                    button.setText("THIS ORDER IS TAKEN");//Change the title
                    button.setEnabled(false);//Making the button disable
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getValue() {
        sharedPreferences = getSharedPreferences("delNumber", MODE_PRIVATE);
        String delNumber = sharedPreferences.getString("delNumber", null);//Getting the phone number from sp
        assert delNumber != null;
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Delivery").child(delNumber).child("Total");

        //Using addListenerForSingleValueEvent as we have to change one value one time and do not need to hear for it
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());//Getting current count
                count = count + 1;//Add 1 to it
                databaseReference1.setValue(count);//Set new value
                Toast.makeText(ActivityWithDetail.this, "" + count, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onButtonClicked(boolean isClicked) {
        //If yes button clicked in the bottom sheet then setting the mReached YES and clearing the saved sp
        final String code = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription Uploads").child(code);
        databaseReference.child("mReached").setValue("YES");
        sp_TakeOrderButtonClicked = getSharedPreferences("isClicked", MODE_PRIVATE);
        sp_TakeOrderButtonClicked.edit().clear().apply();
    }
}
