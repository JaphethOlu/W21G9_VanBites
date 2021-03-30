package com.example.vanbites;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {

    public  static final Parcelable.Creator CREATOR = new
            Parcelable.Creator(){
                public Address createFromParcel(Parcel in){
                    return new Address(in);
                }
                public Address[] newArray(int size){
                    return new Address[size];
                }
            };
    private String address;

    public Address(String address) {
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }


    public Address(Parcel in){
        this.address=in.readString();
            }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
    }
}
