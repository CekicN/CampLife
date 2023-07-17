package com.example.camplife.Models

public class User {
    public var username:String = "";
    public var email:String = "";
    public var profileImage:String = "";
    public var address:String = "";
    public var phoneNumber:String = "";

    constructor(u:String,e:String, i:String, a:String, p:String)
    {
        this.username = u;
        this.email = e;
        this.profileImage = i;
        this.address = a;
        this.phoneNumber = p;
    }
}