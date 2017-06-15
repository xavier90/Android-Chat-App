package edu.scu.chat;

/**
 * Created by yaojianwang on 6/3/17.
 */

public class Message {
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;
    private String senderId;
    private String recipientId;
    private int mRecipientOrSenderStatus; //2:sender text 22 sender image 3 recipient text 23 recipient image

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public Message(String text, String photoUrl, String imageUrl, String senderId, String recipientId) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getsenderId() {
        return senderId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    public String getRecipientId() {
        return recipientId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    //if entityid fits the condition, we will show the message
//    public boolean isShow(String key) {
//        if (key.equals(this.contactId + this.currentUserId) ||
//                key.equals(this.currentUserId + this.contactId)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }
    public void setRecipientOrSenderStatus(int status) {
        this.mRecipientOrSenderStatus = status;
    }
}
