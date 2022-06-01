package com.example.vaadbaitv3;

public class CityData {
private String City_Name;
private String id_Street;
private String Street_name;

    public String getCity_Name() {
        return City_Name;
    }

    public void setCity_Name(String city_Name) {
        City_Name = city_Name;
    }

    public String getStreet_name() {
        return Street_name;
    }

    public void setStreet_name(String street_name) {
        Street_name = street_name;
    }

    @Override
    public String toString() {
        return "CityData{" +
                "City_Name='" + City_Name + '\'' +
                ", id_Street='" + id_Street + '\'' +
                ", Street_name='" + Street_name + '\'' +
                '}';
    }

    public String getId_Street() {
        return id_Street;
    }

    public void setId_Street(String id_Street) {
        this.id_Street = id_Street;
    }
}
