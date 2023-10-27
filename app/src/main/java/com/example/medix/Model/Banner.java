package com.example.medix.Model;

public class Banner {

    private String id;
    private String name;
    private String link;

    private String product_image_id, product_id;
    private String banner_image_id, banner_id, title, image;

    public Banner() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBanner_image_id() {
        return banner_image_id;
    }

    public void setBanner_image_id(String banner_image_id) {
        this.banner_image_id = banner_image_id;
    }

    public String getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(String banner_id) {
        this.banner_id = banner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_image_id() {
        return product_image_id;
    }

    public void setProduct_image_id(String product_image_id) {
        this.product_image_id = product_image_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}