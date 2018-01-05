package com.dummy.trivia.db.model;

import com.google.gson.annotations.Expose;

public class ChangePasswordBean {
    @Expose
    private String oldPassword;
    @Expose
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
