package com.example.medix.Model;

public class Test {

    private String test_id, center_id, category_id, name, short_name, long_desc, short_desc, status, date_added;
    private String cat_name, price, discount_price, dis_start_date, dis_end_date;
    private String address, diagnostic_center_name;
    public Test() {
    }

    public Test(String cat_name, String name, String short_name, String price) {
        this.cat_name = cat_name;
        this.name = name;
        this.short_name = short_name;
        this.price = price;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getCenter_id() {
        return center_id;
    }

    public void setCenter_id(String center_id) {
        this.center_id = center_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
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

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getDis_start_date() {
        return dis_start_date;
    }

    public void setDis_start_date(String dis_start_date) {
        this.dis_start_date = dis_start_date;
    }

    public String getDis_end_date() {
        return dis_end_date;
    }

    public void setDis_end_date(String dis_end_date) {
        this.dis_end_date = dis_end_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiagnostic_center_name() {
        return diagnostic_center_name;
    }

    public void setDiagnostic_center_name(String diagnostic_center_name) {
        this.diagnostic_center_name = diagnostic_center_name;
    }
}
