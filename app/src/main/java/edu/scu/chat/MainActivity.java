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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private EditText mMessageEditText;
    private Button mSendButton;
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

    //define message view class
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }


    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    private static final String TAG = "MainActivity";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
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
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

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
                chatIntent.putExtra(CURRENT_USER_ID, mFirebaseUser.getUid());
//                chatIntent.putExtra(EXTRA_RECIPIENT_ID, user.getRecipientId());
                chatIntent.putExtra(CONTACT_ID, contactID);

                // Start new activity
                startActivity(chatIntent);
            }
        });
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
//                Message.class,
//                R.layout.item_message,
//                MessageViewHolder.class,
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {
//
//            @Override
//            protected Message parseSnapshot(DataSnapshot snapshot) {
//                Message friendlyMessage = super.parseSnapshot(snapshot);
//                if (friendlyMessage != null) {
//                    friendlyMessage.setId(snapshot.getKey());
//                }
//                return friendlyMessage;
//            }


//            @Override
//            protected void populateViewHolder(final MessageViewHolder viewHolder,
//                                              Message friendlyMessage, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                if (friendlyMessage.getText() != null) {
//                    viewHolder.messageTextView.setText(friendlyMessage.getText());
//                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
//                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
//                } else {
//                    String imageUrl = friendlyMessage.getImageUrl();
//                    if (imageUrl.startsWith("gs://")) {
//                        StorageReference storageReference = FirebaseStorage.getInstance()
//                                .getReferenceFromUrl(imageUrl);
//                        storageReference.getDownloadUrl().addOnCompleteListener(
//                                new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        if (task.isSuccessful()) {
//                                            String downloadUrl = task.getResult().toString();
//                                            Glide.with(viewHolder.messageImageView.getContext())
//                                                    .load(downloadUrl)
//                                                    .into(viewHolder.messageImageView);
//                                        } else {
//                                            Log.w(TAG, "Getting download url was not successful.",
//                                                    task.getException());
//                                        }
//                                    }
//                                });
//                    } else {
//                        Glide.with(viewHolder.messageImageView.getContext())
//                                .load(friendlyMessage.getImageUrl())
//                                .into(viewHolder.messageImageView);
//                    }
//                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
//                    viewHolder.messageTextView.setVisibility(TextView.GONE);
//                }
//
//
//                viewHolder.messengerTextView.setText(friendlyMessage.getName());
//                if (friendlyMessage.getPhotoUrl() == null) {
//                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                            R.drawable.ic_account_circle_black_36dp));
//                } else {
//                    Glide.with(MainActivity.this)
//                            .load(friendlyMessage.getPhotoUrl())
//                            .into(viewHolder.messengerImageView);
//                }
//
//                if (friendlyMessage.getText() != null) {
//                    // write this message to the on-device index
//                    FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
//                }
//
//                // log a view action on it
//                FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
//            }
        };
//
//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
//                // to the bottom of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
//                    mMessageRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
//
//        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
//
//
//        // Initialize Firebase Measurement.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//
//        // Initialize Firebase Remote Config.
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//
//        // Define Firebase Remote Config Settings.
//        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
//                new FirebaseRemoteConfigSettings.Builder()
//                        .setDeveloperModeEnabled(true)
//                        .build();
//
//        // Define default config values. Defaults are used when fetched config values are not
//        // available. Eg: if an error occurred fetching values from the server.
//        Map<String, Object> defaultConfigMap = new HashMap<>();
//        defaultConfigMap.put("friendly_msg_length", 50L);
//
//        // Apply config settings and default values.
//        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
//        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);
//
//        // Fetch remote config.
//        fetchConfig();

//        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
//        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//                .getInt(FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
//        mMessageEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0) {
//                    mSendButton.setEnabled(true);
//                } else {
//                    mSendButton.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        mAddMessageImageView = (ImageView) findViewById(R.id.btn_options);
//        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_IMAGE);
//            }
//        });
//
//        mSendButton = (Button) findViewById(R.id.btn_chat_send_message);
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Message friendlyMessage = new Message(mMessageEditText.getText().toString(), mUsername,
//                        mPhotoUrl, null);
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
//                mMessageEditText.setText("");
//                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
//            }
//        });
//    }

//    private Action getMessageViewAction(Message friendlyMessage) {
//        return new Action.Builder(Action.Builder.VIEW_ACTION)
//                .setObject(friendlyMessage.getName(), MESSAGE_URL.concat(friendlyMessage.getId()))
//                .setMetadata(new Action.Metadata.Builder().setUpload(false))
//                .build();
//    }
//
//    private Indexable getMessageIndexable(Message friendlyMessage) {
//        PersonBuilder sender = Indexables.personBuilder()
//                .setIsSelf(mUsername.equals(friendlyMessage.getName()))
//                .setName(friendlyMessage.getName())
//                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));
//
//        PersonBuilder recipient = Indexables.personBuilder()
//                .setName(mUsername)
//                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));
//
//        Indexable messageToIndex = Indexables.messageBuilder()
//                .setName(friendlyMessage.getText())
//                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId()))
//                .setSender(sender)
//                .setRecipient(recipient)
//                .build();
//
//        return messageToIndex;
//    }

    // Fetch the config to determine the allowed length of messages.
//    public void fetchConfig() {
//        long cacheExpiration = 3600; // 1 hour in seconds
//        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
//        // server. This should not be used in release builds.
//        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
//            cacheExpiration = 0;
//        }
//        mFirebaseRemoteConfig.fetch(cacheExpiration)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
//                        mFirebaseRemoteConfig.activateFetched();
//                        applyRetrievedLengthLimit();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // There has been an error fetching the config
//                        Log.w(TAG, "Error fetching config", e);
//                        applyRetrievedLengthLimit();
//                    }
//                });
//    }

//    private void applyRetrievedLengthLimit() {
//        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
//        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
//        Log.d(TAG, "FML is: " + friendly_msg_length);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
//
//        if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                if (data != null) {
//                    final Uri uri = data.getData();
//                    Log.d(TAG, "Uri: " + uri.toString());
//
//                    Message tempMessage = new Message(null, mUsername, mPhotoUrl,
//                            LOADING_IMAGE_URL);
//                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
//                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
//                                @Override
//                                public void onComplete(DatabaseError databaseError,
//                                                       DatabaseReference databaseReference) {
//                                    if (databaseError == null) {
//                                        String key = databaseReference.getKey();
//                                        StorageReference storageReference =
//                                                FirebaseStorage.getInstance()
//                                                        .getReference(mFirebaseUser.getUid())
//                                                        .child(key)
//                                                        .child(uri.getLastPathSegment());
//
//                                        putImageInStorage(storageReference, uri, key);
//                                    } else {
//                                        Log.w(TAG, "Unable to write message to database.",
//                                                databaseError.toException());
//                                    }
//                                }
//                            });
//                }
//            }
//        } else if (requestCode == REQUEST_INVITE) {
//            if (resultCode == RESULT_OK) {
//                // Use Firebase Measurement to log that invitation was sent.
//                Bundle payload = new Bundle();
//                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");
//
//                // Check how many invitations were sent and log.
//                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
//                Log.d(TAG, "Invitations sent: " + ids.length);
//            } else {
//                // Use Firebase Measurement to log that invitation was not sent
//                Bundle payload = new Bundle();
//                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);
//
//                // Sending failed or it was canceled, show failure message to the user
//                Log.d(TAG, "Failed to send invitation.");
//            }
//        }
//    }


//    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
//        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this,
//                new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Message friendlyMessage =
//                                    new Message(null, mUsername, mPhotoUrl,
//                                            task.getResult().getDownloadUrl()
//                                                    .toString());
//                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
//                                    .setValue(friendlyMessage);
//                        } else {
//                            Log.w(TAG, "Image upload task was not successful.",
//                                    task.getException());
//                        }
//                    }
//                });
//    }

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
