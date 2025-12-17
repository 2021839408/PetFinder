package com.project.petfinder;

public class LostPetModel {

    public String id;
    public String name;
    public String color;
    public String breed;
    public String lastSeen;
    public String imageBase64;
    public long timestamp;
    public double latitude;
    public double longitude;
    public String ownerId;
    public String ownerName;
    public String ownerPhone;
    public LostPetModel() {

    }

    public LostPetModel(String id, String name, String color, String breed,
                        String lastSeen, String imageBase64, long timestamp,
                        double latitude, double longitude,
                        String ownerId, String ownerName, String ownerPhone) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.breed = breed;
        this.lastSeen = lastSeen;
        this.imageBase64 = imageBase64;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }
}
