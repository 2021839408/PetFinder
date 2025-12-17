package com.project.petfinder;

public class FoundPetModel {

    public String id;
    public String locationAddress;
    public String color;
    public String imageBase64;
    public long timestamp;
    public double latitude;
    public double longitude;
    public String finderId;
    public String finderName;
    public String finderPhone;
    public String additionalInfo;
    public FoundPetModel() {}

    public FoundPetModel(String id, String locationAddress, String color,
                         String imageBase64, long timestamp,
                         double latitude, double longitude,
                         String finderId, String finderName, String finderPhone,
                         String additionalInfo) {

        this.id = id;
        this.locationAddress = locationAddress;
        this.color = color;
        this.imageBase64 = imageBase64;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.finderId = finderId;
        this.finderName = finderName;
        this.finderPhone = finderPhone;
        this.additionalInfo = additionalInfo;
    }
}
