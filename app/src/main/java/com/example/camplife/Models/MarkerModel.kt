package com.example.camplife.Models

class MarkerModel {
    public lateinit var postId:String;
    public lateinit var campName:String;
    public lateinit var phone:String;
    public lateinit var address:String;
    public lateinit var description:String;
    public lateinit var imagePaths:ArrayList<String>;

    public var latitude:Double? = null;
    public var longitude:Double? = null;

    constructor(id:String, n:String,p:String, a:String, d:String, i:ArrayList<String>, lat:Double?, lng:Double?)
    {
        postId = id
        campName = n;
        phone = p;
        address = a;
        description = d;
        imagePaths = i;
        latitude = lat;
        longitude = lng;
    }
}