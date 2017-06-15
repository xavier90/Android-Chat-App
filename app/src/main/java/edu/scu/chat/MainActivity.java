package edu.scu.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import edu.scu.chat.Utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private DatabaseReference mFirebaseDatabaseReference;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private FirebaseUser mFirebaseUser;
    protected PopupWindow optionPopup;
    private ImageButton userProfileImage;
    private ListView mListView;
    private FirebaseListAdapter<User> firebaseListAdapter;
    private ListView contacts;


    public Action getIndexApiAction() {
        return Actions.newView("Main", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }


    private static final String TAG = "MainActivity";

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;

    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    public static final String ANONYMOUS = "anonymous";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //click head image and start editprofile activity
        View headerView = navigationView.getHeaderView(0);
        userProfileImage = (ImageButton) headerView.findViewById(R.id.btn_user_profile_image);

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //messenger
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        //change image of imagebutton
        if (mPhotoUrl != null) {
            Glide.with(headerView.getContext()).load(mPhotoUrl).into(userProfileImage);
        }



//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //use firebase list adapter to show contact
        mListView = (ListView) findViewById(R.id.list_view_users);


        firebaseListAdapter = new FirebaseListAdapter<User>(
                this,
                User.class,
                R.layout.item_contact,
                mFirebaseDatabaseReference.child("contacts").getRef()
        ) {
            @Override
            protected void populateView(View v, User model, int position) {
                ImageView imgView = (ImageView) v.findViewById(R.id.img_contact_picture);
                if (model.getPhotoUrl() != null) {
                    Glide.with(MainActivity.this).load(model.getPhotoUrl()).into(imgView);
                }

                TextView textView = (TextView) v.findViewById(R.id.contact_name);
                textView.setText(model.getName());
            }
        };

        mListView.setAdapter(firebaseListAdapter);


        //after click listview, start chatactivity
        contacts = (ListView) findViewById(R.id.list_view_users);
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) contacts.getItemAtPosition(position);
                String contactID = user.getEntityID();

                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                chatIntent.putExtra(Utils.CURRENT_USER_ID, mFirebaseUser.getUid());
//                chatIntent.putExtra(EXTRA_RECIPIENT_ID, user.getRecipientId());
                chatIntent.putExtra(Utils.CONTACT_ID, contactID);

                // Start chat activity
                startActivity(chatIntent);
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
