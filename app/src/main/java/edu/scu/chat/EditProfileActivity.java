package edu.scu.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etName, etEmail;
    private Button saveButton;
    private FirebaseUser mFirebaseUser;
    private String email;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        saveButton = (Button) findViewById(R.id.btn_save_profile);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        name = mFirebaseUser.getDisplayName();
        email = mFirebaseUser.getEmail();

        if (name != null) {
            etName.setText(name);
        }
        if (email != null) {
            etEmail.setText(email);
        }

        findViewById(R.id.btn_save_profile).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save_profile) {
            mFirebaseUser.
        }
    }
}
