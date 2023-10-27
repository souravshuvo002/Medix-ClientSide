package com.example.medix.Model;

public class Country {
    private String country_id, name, iso_code_2, iso_code_3, address_format, postcode_required, status;

    public Country() {
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso_code_2() {
        return iso_code_2;
    }

    public void setIso_code_2(String iso_code_2) {
        this.iso_code_2 = iso_code_2;
    }

    public String getIso_code_3() {
        return iso_code_3;
    }

    public void setIso_code_3(String iso_code_3) {
        this.iso_code_3 = iso_code_3;
    }

    public String getAddress_format() {
        return address_format;
    }

    public void setAddress_format(String address_format) {
        this.address_format = address_format;
    }

    public String getPostcode_required() {
        return postcode_required;
    }

    public void setPostcode_required(String postcode_required) {
        this.postcode_required = postcode_required;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}