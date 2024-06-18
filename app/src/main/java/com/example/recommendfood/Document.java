package com.example.recommendfood;

public class Document {
    private String place_name;
    private String address_name;
    private String road_address_name;
    private String x;
    private String y;

    public String getPlaceName() {
        return place_name;
    }

    public void setPlaceName(String place_name) {
        this.place_name = place_name;
    }

    public String getAddressName() {
        return address_name;
    }

    public void setAddressName(String address_name) {
        this.address_name = address_name;
    }

    public String getRoadAddressName() {
        return road_address_name;
    }

    public void setRoadAddressName(String road_address_name) {
        this.road_address_name = road_address_name;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}