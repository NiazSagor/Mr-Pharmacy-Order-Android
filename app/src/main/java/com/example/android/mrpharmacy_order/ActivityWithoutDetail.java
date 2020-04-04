package com.example.android.mrpharmacy_order;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mrpharmacy_order.Model.BottomSheet;
import com.example.android.mrpharmacy_order.Model.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityWithoutDetail extends AppCompatActivity implements BottomSheet.BottomSheetListener {

    DatabaseReference firebaseDatabase, databaseReference1;//1st is for the Order Upload database 2nd is for the Delivery

    //Making ArrayLists to store the data from database
    ArrayList<String> brand = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> quantity = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<String> userDetails = new ArrayList<>();

    ListView listView;//To show to order ids

    TextView cName, cPhone, cAddress;//User details

    SharedPreferences sharedPreferences;//Getting the phone number
    SharedPreferences sp_TakeOrderButtonClicked;//For getting the saved phone number

    Button updateButton;//Taking the order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_without_detail);

        sp_TakeOrderButtonClicked = getSharedPreferences("isClicked", MODE_PRIVATE);

        if (sp_TakeOrderButtonClicked.getBoolean("clicked", false)) {
            BottomSheet bottomSheet = new BottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "example");
        }

        //Getting the current selected order id sent from Without Fragment
        final String code = getIntent().getStringExtra("currentOrder");

        cName = findViewById(R.id.customerName);
        cPhone = findViewById(R.id.customerPhone);
        cAddress = findViewById(R.id.customerAddress);
        listView = findViewById(R.id.listview_1);

        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance().getReference("Order Upload").child(code);
                firebaseDatabase.child("mStatus").setValue("Order is taken");
                sp_TakeOrderButtonClicked.edit().putBoolean("clicked", true).apply();
                getValue();
            }
        });

        getValues(code);//Getting the values from the arrays saved in database using current id which is code

        OrderDetailAdapter adapter = new OrderDetailAdapter(this, name, quantity, brand, type);
        listView.setAdapter(adapter);
    }

    public void getValues(String code) {

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Order Upload").child(code);

        //Entering the child and grabbing the values
        firebaseDatabase.child("mBrand").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                //Using a loop to store the values using index
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    brand.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.child("mName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    name.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.child("mQuantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    quantity.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.child("mType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    type.add(i, string);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.child("mUserDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String string = Objects.requireNonNull(postsnapshot.getValue()).toString();
                    userDetails.add(i, string);
                    i++;
                }
                //Setting the user details from the array using index
                cName.setText(userDetails.get(0));
                cPhone.setText(userDetails.get(1));
                cAddress.setText(userDetails.get(2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.child("mStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String string = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                if (string.equals("Pending")) {
                    updateButton.setBackgroundColor(Color.GREEN);
                } else if (string.equals("Order is taken")) {
                    updateButton.setBackgroundColor(Color.RED);
                    updateButton.setText("THIS ORDER IS TAKEN");
                    updateButton.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getValue() {
        sharedPreferences = getSharedPreferences("delNumber", MODE_PRIVATE);
        String delNumber = sharedPreferences.getString("delNumber", null);
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Delivery").child(delNumber).child("Total");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                count = count + 1;
                databaseReference1.setValue(count);
                Toast.makeText(ActivityWithoutDetail.this, "" + count, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onButtonClicked(boolean isClicked) {
        final String code = getIntent().getStringExtra("currentOrder");
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Order Upload").child(code);
        firebaseDatabase.child("mReached").setValue("YES");
        sp_TakeOrderButtonClicked = getSharedPreferences("isClicked", MODE_PRIVATE);
        sp_TakeOrderButtonClicked.edit().clear().apply();
    }
}