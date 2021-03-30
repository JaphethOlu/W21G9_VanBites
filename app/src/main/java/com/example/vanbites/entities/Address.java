package com.example.vanbites.entities;

public class Address {
    private int id;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String provinceAndPostCode;

    public Address(int id, String name, String addressLine1, String addressLine2, String provinceAndPostCode) {
        this.id = id;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.provinceAndPostCode = provinceAndPostCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getProvinceAndPostCode() {
        return provinceAndPostCode;
    }

    public void setPostCode(String postCode) {
        this.provinceAndPostCode = provinceAndPostCode;
    }
}
