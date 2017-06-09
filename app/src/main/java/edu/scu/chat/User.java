package edu.scu.chat;

import java.util.List;

/**
 * Created by yaojianwang on 6/7/17.
 */

public class User {
    private Long id;
    private String entityID;
    private Integer AuthenticationType;
    private String messageColor;
    private Boolean Online;

    private List<User> friends;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String entityID, Integer AuthenticationType, String messageColor, Boolean Online) {
        this.id = id;
        this.entityID = entityID;
        this.AuthenticationType = AuthenticationType;
        this.messageColor = messageColor;
        this.messageColor = messageColor;
        this.Online = Online;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public Integer getAuthenticationType() {
        return AuthenticationType;
    }

    public void setAuthenticationType(Integer AuthenticationType) {
        this.AuthenticationType = AuthenticationType;
    }

    public String getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(String messageColor) {
        this.messageColor = messageColor;
    }

    public Boolean getOnline() {
        return Online;
    }

    public void setOnline(Boolean Online) {
        this.Online = Online;
    }

//    public List<User> setFriends() {
//        if (friends == null) {
//
//        }
//    }

    public void resetFriends() {
        friends = null;
    }
}
