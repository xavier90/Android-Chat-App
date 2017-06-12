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
    private String currentUserId;
    private String contactId;

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    public String getContactId() {
        return contactId;
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

    public boolean isShow(String key) {
        if (key.equals(this.contactId + t))
    }
}
