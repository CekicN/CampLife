package com.example.camplife.Models

public class User {
    public var username:String = "";
    public var email:String = "";
    public var profileImage:String = "";

    constructor(u:String,e:String, i:String)
    {
        this.username = u;
        this.email = e;
        this.profileImage = i;
    }
}