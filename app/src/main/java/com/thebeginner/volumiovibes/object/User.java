package com.thebeginner.volumiovibes.object;

/**
 * Created by domen on 26/02/2018.
 */

public class User {
    private String name, email, imgUrl;

    public User(String name, String email, String imgUrl) {
        this.name = name;
        this.email = email;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String displayName) {
        this.name = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
