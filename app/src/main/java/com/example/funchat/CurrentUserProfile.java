package com.example.funchat;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class CurrentUserProfile extends AppCompatActivity {

    TextView currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_current_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton deleteAccount = findViewById(R.id.deleteAccount);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });



        currentUserName = findViewById(R.id.currentUserName);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("name");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the username from the database
                String username = dataSnapshot.getValue(String.class);

                // Set the username to the TextView
                currentUserName.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        // Get a reference to the ImageView
        ImageView profilePicImageView = findViewById(R.id.currentUserPic);

// Get the current user's UID


// Get a reference to the current user's profile picture URL in the database
        DatabaseReference profilePicRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("profilePictureUrl");

// Attach a listener to read the data from the database
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the profile picture URL from the database
                String profilePicUrl = dataSnapshot.getValue(String.class);

                // Load the profile picture using Glide or any other image loading library
                Glide.with(CurrentUserProfile.this)
                        .load(profilePicUrl)
                        .placeholder(R.drawable.meerkat) // Placeholder image while loading
                        .error(R.drawable.img_4) // Error image if loading fails
                        .into(profilePicImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


    }
    void deleteAccount(){
        // Get the current user's UID
        // Get the current user
        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


// Delete the user's details from Firebase Realtime Database
        deleteUserDetailsFromDatabase();

// Delete the user's account using Firebase Authentication
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {



                            FirebaseAuth.getInstance().signOut();

                            finish();

                            // User account deleted successfully
                            Toast.makeText(CurrentUserProfile.this,"Account Deleted",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CurrentUserProfile.this, GoogleSignUp.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

// Finish the current activity
                            finish();
                            // Navigate to the login screen or perform any other actions as needed
                        } else {
                            // Handle errors while deleting the user account
                            Toast.makeText(CurrentUserProfile.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void deleteUserDetailsFromDatabase() {
        // Get the current user's UID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

// Get a reference to the location of the user's details in the database
        DatabaseReference userRef = databaseReference.child("users").child(uid);

// Delete the user's details
        userRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        // Deletion successful
                        Toast.makeText(CurrentUserProfile.this,"Account Deleted Successfully",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(CurrentUserProfile.this, GoogleSignUp.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(CurrentUserProfile.this,"Failed to delete the account",Toast.LENGTH_LONG).show();
                    }
                });

    }
}