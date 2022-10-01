package com.example.f2c;

//import java.util.SplittableRandom;

public class Detail {
    public String title,address,description,price;

    public Detail(){

    }

    public Detail(String title, String address,String description, String price) {
        this.title = title;
        this.address = address;
        this.description = description;
        this.price = price;
    }

    public String getAddress(){
        return address;
    }

    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getPrice(){
        return price;
    }
}
