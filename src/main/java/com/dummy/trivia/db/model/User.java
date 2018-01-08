package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class User extends BaseModel implements UserDetails {

    @Expose
    private String username;
    @Expose(serialize = false)
    private String password;
    @Expose
    private String avatarUrl;
    @Expose(serialize = false)
    @Transient
    private String avatarBase64;
    @Expose
    private int winCount;
    @Expose
    private int totalPlay;
    @Expose
    private int exp;
    @Expose
    private boolean isReady;

    private List<String> roles;

    @Transient
    private List<? extends GrantedAuthority> authorities;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public void incrementWinCount() {
        this.winCount++;
    }

    public int getTotalPlay() {
        return totalPlay;
    }

    public void setTotalPlay(int totalPlay) {
        this.totalPlay = totalPlay;
    }

    public void incrementTotalPlay() {
        this.totalPlay++;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void incrementExpBy(int value) {
        this.exp += value;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", winCount=" + winCount +
                ", totalPlay=" + totalPlay +
                ", exp=" + exp +
                ", isReady=" + isReady +
                ", roles=" + roles +
                ", authorities=" + authorities +
                '}';
    }
}
