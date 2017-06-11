package edu.scu.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etName, etPhone;
    private Button saveButton;
    private FirebaseUser mFirebaseUser;
    private String phone;
    private String name;
    private DatabaseReference databaseReference;
    private static User user;
    private static final String TAG = "EditProfileActivity";
    private String entityID;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        if (getActionBar() != null) {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        saveButton = (Button) findViewById(R.id.btn_save_profile);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        entityID = mFirebaseUser.getUid();
        photoUrl = mFirebaseUser.getPhotoUrl().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        databaseReference.orderByChild("entityID").equalTo(entityID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "failed to read profile");
            }
        });
        if (user != null) {
            name = user.getName();
            phone = user.getPhone();
        }


        if (name != null) {
            etName.setText(name);
        }
        if (phone != null) {
            etPhone.setText(phone);
        }

        findViewById(R.id.btn_save_profile).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save_profile) {
            name = etName.getText().toString();
            phone = etPhone.getText().toString();

            databaseReference.child(entityID).setValue(new User(name, phone, entityID, photoUrl));
            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        }
    }
}
