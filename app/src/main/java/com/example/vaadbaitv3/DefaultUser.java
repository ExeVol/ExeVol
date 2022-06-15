package com.example.vaadbaitv3;

public class DefaultUser {
    public static final int TYPE_KEY1=1;
    public static final int TYPE_KEY2=2;
    public static final int TYPE_KEY3=3;
    private String Name;
    private String Email;
    private String Password;
    private String Phone;
    private String city2;
    private String Street2;
    private String num_address2;
    private String storage;

    public DefaultUser() {
    }

    private int type_guest;
    private String key;

    @Override
    public String toString() {
        return "DefaultUser{" +
                "Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Phone='" + Phone + '\'' +
                ", city2='" + city2 + '\'' +
                ", Street2='" + Street2 + '\'' +
                ", num_address2='" + num_address2 + '\'' +
                ", storage='" + storage + '\'' +
                ", type_guest=" + type_guest +
                ", key='" + key + '\'' +
                '}';
    }

    public DefaultUser(String name, String email, String password, String phone, String city2, String street2, String num_address2, String storage, int type_guest, String key) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        this.city2 = city2;
        Street2 = street2;
        this.num_address2 = num_address2;
        this.storage = storage;
        this.type_guest = type_guest;
        this.key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getStreet2() {
        return Street2;
    }

    public void setStreet2(String street2) {
        Street2 = street2;
    }

    public String getNum_address2() {
        return num_address2;
    }

    public void setNum_address2(String num_address2) {
        this.num_address2 = num_address2;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public int getType_guest() {
        return type_guest;
    }

    public void setType_guest(int type_guest) {
        this.type_guest = type_guest;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

