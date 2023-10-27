package com.example.medix.Model;

public class Favorites {

    private int id;
    private String product_id;
    private String product_name;
    private String quantity;
    private String price;
    private String image_link;
    private String menu_id;
    private String menu_name;

    private String cat_id;
    private String parent_cat_name, sub_cat_name, model;

    public Favorites() {
    }

    public Favorites(int id, String product_id, String product_name, String quantity, String price, String image_link, String menu_id, String menu_name) {
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.image_link = image_link;
        this.menu_id = menu_id;
        this.menu_name = menu_name;
    }

    public Favorites(String product_id, String product_name, String quantity, String price, String image_link, String menu_id, String menu_name) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.image_link = image_link;
        this.menu_id = menu_id;
        this.menu_name = menu_name;
    }



    public Favorites(String product_id, String cat_id, String product_name, String model, String image_link, String price, String quantity, String parent_cat_name, String sub_cat_name) {
        this.product_id = product_id;
        this.cat_id = cat_id;
        this.product_name = product_name;
        this.model = model;
        this.image_link = image_link;
        this.price = price;
        this.quantity = quantity;
        this.parent_cat_name = parent_cat_name;
        this.sub_cat_name = sub_cat_name;
    }

    public Favorites(int id, String product_id, String cat_id, String product_name, String model, String image_link, String price, String quantity, String parent_cat_name, String sub_cat_name) {
        this.id = id;
        this.product_id = product_id;
        this.cat_id = cat_id;
        this.product_name = product_name;
        this.model = model;
        this.image_link = image_link;
        this.price = price;
        this.quantity = quantity;
        this.parent_cat_name = parent_cat_name;
        this.sub_cat_name = sub_cat_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getParent_cat_name() {
        return parent_cat_name;
    }

    public void setParent_cat_name(String parent_cat_name) {
        this.parent_cat_name = parent_cat_name;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
