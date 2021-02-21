package com.example.travelbuddyv2.model;

public class User {


    private String name;
    private String email;
    private String phone;
    private String profile_image;
    private String user_id;

    public User(String name,String email, String phone, String profile_image, String user_id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profile_image = profile_image;
        this.user_id = user_id;

    }

    public User() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "email= " + email + '\'' +
                ", phone='" + phone + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

}
