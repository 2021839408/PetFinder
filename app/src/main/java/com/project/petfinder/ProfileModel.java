package com.project.petfinder;

public class ProfileModel {
    public String name;
    public String phone;
    public String address;
    public String email;

    public ProfileModel() {}

    public ProfileModel(String name, String phone, String address, String email) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }
}
