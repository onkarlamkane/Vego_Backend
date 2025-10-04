package com.eptiq.vegobike.dtos;

public class BikeDocumentsDTO {
    private String documentImage;
    private String insuranceImage;
    private String pucImage;

    public BikeDocumentsDTO(String documentImage, String insuranceImage, String pucImage) {
        this.documentImage = documentImage;
        this.insuranceImage = insuranceImage;
        this.pucImage = pucImage;
    }

    // Getters and setters
    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

    public String getInsuranceImage() {
        return insuranceImage;
    }

    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    public String getPucImage() {
        return pucImage;
    }

    public void setPucImage(String pucImage) {
        this.pucImage = pucImage;
    }
}
