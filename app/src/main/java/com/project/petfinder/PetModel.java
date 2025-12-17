package com.project.petfinder;

public class PetModel {

    public String petId;
    public String name;
    public String type;
    public String color;
    public String breed;
    public String imageBase64;
    public PetModel() {}

    public PetModel(String petId, String name, String type, String color, String breed, String imageBase64) {
        this.petId = petId;
        this.name = name;
        this.type = type;
        this.color = color;
        this.breed = breed;
        this.imageBase64 = imageBase64;
    }
}
