package com.example.android.mrpharmacy_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText delNumber;//Delivery man number
    Button delLoginBtn;//Login button
    Button delSignUpBtn;//Register button

    DatabaseReference databaseReference;//Delivery database ref

    SharedPreferences sharedPreferences, sharedPreferences1;//1st is for the login and 2nd is for storing the number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Delivery");//Getting ref
        sharedPreferences = getSharedPreferences("isLogged", MODE_PRIVATE);//Making sp named isLogged

        sharedPreferences1 = getSharedPreferences("delNumber", MODE_PRIVATE);//Making another sp called delNumber

        final SharedPreferences.Editor editor = sharedPreferences1.edit();//Calling editor to put string values

        //Checking if the user is already logged in
        if (sharedPreferences.getBoolean("isLogged", false)) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        //Getting the views
        delNumber = findViewById(R.id.delNumber);
        delSignUpBtn = findViewById(R.id.delSignUpBtn);
        delLoginBtn = findViewById(R.id.delLoginBtn);

        //Login button clicked
        delLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Please Wait");

                //If empty
                if (delNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.show();

                //Listening to the database and checking if the input number is already in the database
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //If number is in the database
                        if (dataSnapshot.child(delNumber.getText().toString()).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            sharedPreferences.edit().putBoolean("isLogged", true).apply();//Putting true value to indicate the user is successfully logged in

                            editor.putString("delNumber", delNumber.getText().toString());//Putting the number into the 2nd sp
                            editor.apply();

                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        delSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Please Wait");

                if (delNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Your Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.show();

                /* Using addListenerForSingleValueEvent as we are making changes in the database
                 * So if we use addValueEventListener database will fall into a loop
                 * Because if a change is made, listener will listen to it and will try to act upon it
                 * But we need to make change one time and do not have listen to it
                 */
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //If the phone number is already in the database
                        if (dataSnapshot.child(delNumber.getText().toString()).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.dismiss();
                            final List<String> list = new ArrayList<>();//Making a list to store number and an initializing value for the Total Order Count which is 0
                            list.add(delNumber.getText().toString());
                            list.add("0");//Order count
                            //Setting these values
                            databaseReference.child(delNumber.getText().toString()).child("Phone").setValue(list.get(0));
                            databaseReference.child(delNumber.getText().toString()).child("Total").setValue(list.get(1));
                            Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
