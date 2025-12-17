package com.project.petfinder;

public class ReportedPetModel {

    public String type;
    public String petId;
    public String imageBase64;
    public long timestamp;
    public double latitude;
    public double longitude;
    public ReportedPetModel() { }

    public ReportedPetModel(String type, String petId, String imageBase64,
                            long timestamp, double latitude, double longitude) {

        this.type = type;
        this.petId = petId;
        this.imageBase64 = imageBase64;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
