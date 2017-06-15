package edu.scu.chat.Utils;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;

import edu.scu.chat.Message;
import edu.scu.chat.MessageListAdapter;
import edu.scu.chat.View.ChatMessageBoxView;

/**
 * Created by yaojianwang on 6/12/17.
 */

public class ChatHelper implements ChatMessageBoxView.MessageBoxOptionsListener, ChatMessageBoxView.MessageSendListener{

    private ChatMessageBoxView messageBoxView;
    private DatabaseReference messagesDatabaseRef;
    private FirebaseUser mFirebaseUser;
    private String senderId;
    private String recipientId;
    private MessageListAdapter messageListAdapter;
    private WeakReference<Activity> activity;

    private static final int REQUEST_IMAGE = 2;

    public ChatHelper(ChatMessageBoxView messageBoxView, DatabaseReference messageDatabaseRef, FirebaseUser mFirebaseUser,
                      String senderId, String recipientId) {
        this.messageBoxView = messageBoxView;
        this.messagesDatabaseRef = messageDatabaseRef;
        this.mFirebaseUser = mFirebaseUser;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public void setMessageListAdapter(MessageListAdapter messageListAdapter) {
        this.messageListAdapter = messageListAdapter;
    }

    public void setActivity(Activity activity) {
        this.activity = new WeakReference<Activity>(activity);
    }
    @Override
    public void onLocationPressed() {

    }

    @Override
    public void onTakePhotoPressed() {

    }

    @Override
    public void onPickImagePressed() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.get().startActivityForResult(intent, REQUEST_IMAGE);

    }



    @Override
    public boolean onOptionButtonPressed() {
        return false;
    }

    @Override
    public void onSendPressed(String text) {
        sendMessageWithText(messageBoxView.getMessageText(), true);
    }

    private void sendMessageWithText(String text, boolean clearEditText){

        if (StringUtils.isEmpty(text) || StringUtils.isBlank(text))
        {
            return;
        }

        // Clear all white space from message
        text = text.trim();

        Message newMessage = new Message(text, mFirebaseUser.getPhotoUrl().toString(), null, senderId, recipientId);
        messagesDatabaseRef.push().setValue(newMessage);
//        if (messageListAdapter != null)
//            messageListAdapter.addRow(newMessage);

        if (clearEditText && messageBoxView!=null)
            messageBoxView.clearText();
    }
}
