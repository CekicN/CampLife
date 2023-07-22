package com.example.camplife.Models

public class User {
    public var username:String = "";
    public var profileImage:String = "";
    public var address:String = "";
    public var phoneNumber:String = "";
    public var points:Int = 0;


    constructor(u:String,i:String, a:String, p:String, point:Int)
    {
        this.username = u;
        this.profileImage = i;
        this.address = a;
        this.phoneNumber = p;
        this.points = point
    }
}