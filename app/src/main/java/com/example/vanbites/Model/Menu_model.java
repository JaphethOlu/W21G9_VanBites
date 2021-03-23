package com.example.vanbites.Model;

public class Menu_model {

    private int id;
    private String image;
    private String name;


    // empty constructor required for Firebase's automatic data mapping
    public Menu_model() {
    }

    public Menu_model(int id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
