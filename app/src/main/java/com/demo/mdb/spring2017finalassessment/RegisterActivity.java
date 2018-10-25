package com.demo.mdb.spring2017finalassessment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private ImageView image;
    private TextView nameText;
    private TextView emailText;
    private TextView passText;
    private TextView confirmPassText;


    private String email;
    private String pass;
    private String name;
    private Uri imageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.createnewuser).setOnClickListener(this);
        findViewById(R.id.imageView).setOnClickListener(this);
        image = (ImageView) findViewById(R.id.imageView);
        nameText = (TextView) findViewById(R.id.name);
        emailText = (TextView) findViewById(R.id.email2);
        passText = (TextView) findViewById(R.id.password2);
        confirmPassText = (TextView) findViewById(R.id.confirmpassword);
        mAuth = FirebaseAuth.getInstance();

        /* TODO Part 2
        * Implement registration. If the imageView is clicked, set it to an image from the gallery
        * and store the image as a Uri instance variable (also change the imageView's image to this
        * Uri. If the create new user button is pressed, call createUser using the email and password
        * from the edittexts. Remember that it's email2 and password2 now!
        */
    }

    private void createUser(final String email, final String password) {
        /* TODO Part 2.1
         * This part's long, so listen up!
         * Create a user, and if it fails, display a Toast.
         *
         * If it works, we're going to add their image to the database. To do this, we will need a
         * unique user id to identify the user (push isn't the best answer here. Do some Googling!)
         *
         * Now, if THAT works (storing the image), set the name and photo uri of the user (hint: you
         * want to update a firebase user's profile.)
         *
         * Finally, if updating the user profile works, go to the TabbedActivity
         */
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Creating Account");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // upload information to database
                    String userID = mAuth.getCurrentUser().getUid();
                    uploadInfo(userID);
                } else {
                    // register failed
                    // display toast
                    Utils.displayError(getApplicationContext(), "Cannot create user");
                }
                nDialog.hide();

            }
        });

    }

    private void uploadInfo(String userID) {
        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Creating Account");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();

        // upload image to storage, then upload all data in user account
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("users").child(userID).child("info");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference("users").child("profiles");

        // upload image to storage
        imageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // upload data to user account

                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(imageURI)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // transition to tabbed activity
                                    Intent i = new Intent(getApplicationContext(), TabbedActivity.class);
                                    startActivity(i);
                                } else {
                                    Utils.displayError(getApplicationContext(), "Could not create user");
                                }
                                nDialog.hide();
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utils.displayError(getApplicationContext(), "Could not create account");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // fetched image
        if (resultCode == RESULT_OK) try {
            imageURI = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageURI);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            image.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createnewuser:
                email = emailText.getText().toString();
                pass = passText.getText().toString();
                name = nameText.getText().toString();
                String confirmPass = confirmPassText.getText().toString();
                if (Utils.isValid(getApplicationContext(), email, pass, confirmPass)) {
                    if (imageURI != null && name != null) {
                        createUser(email, pass);
                    } else {
                        Utils.displayError(getApplicationContext(), "Registration failed");
                    }
                }
                break;
            case R.id.imageView:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }
}
