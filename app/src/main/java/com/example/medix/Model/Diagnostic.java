package com.example.medix.Model;

public class Diagnostic {
    private String name, address,  discount, logo_image;
    private String diagnostic_center_id, phone, email, tagline, status, date_added;
    private String token;

    public Diagnostic() {
    }

    public Diagnostic(String name, String address, String discount) {
        this.name = name;
        this.address = address;
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getLogo_image() {
        return logo_image;
    }

    public void setLogo_image(String logo_image) {
        this.logo_image = logo_image;
    }

    public String getDiagnostic_center_id() {
        return diagnostic_center_id;
    }

    public void setDiagnostic_center_id(String diagnostic_center_id) {
        this.diagnostic_center_id = diagnostic_center_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
