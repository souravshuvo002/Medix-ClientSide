package com.example.medix.Model;

public class Cart {
    private int id;
    private String test_id, test_name, test_short_name, center_id, center_name, center_address, price, quantity;

    // Shop

    private String product_id;
    private String product_name;
    private String image_link;
    private String menu_id;
    private String menu_name;

    private String cat_id;
    private String parent_cat_name, sub_cat_name, model, cat_name;
    private String store_name, seller_id;

    public Cart() {
    }

    public Cart(int id, String test_id, String test_name, String test_short_name, String center_id, String center_name, String center_address, String price, String quantity) {
        this.id = id;
        this.test_id = test_id;
        this.test_name = test_name;
        this.test_short_name = test_short_name;
        this.center_id = center_id;
        this.center_name = center_name;
        this.center_address = center_address;
        this.price = price;
        this.quantity = quantity;
    }

    public Cart(String test_id, String test_name, String test_short_name, String center_id, String center_name, String center_address, String price, String quantity) {
        this.test_id = test_id;
        this.test_name = test_name;
        this.test_short_name = test_short_name;
        this.center_id = center_id;
        this.center_name = center_name;
        this.center_address = center_address;
        this.price = price;
        this.quantity = quantity;
    }

    public Cart(String product_id, String product_name, String quantity, String price, String image_link, String menu_id, String menu_name) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.image_link = image_link;
        this.menu_id = menu_id;
        this.menu_name = menu_name;
    }

    public Cart(int id, String product_id, String product_name, String quantity, String price, String image_link, String menu_id, String menu_name) {
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.price = price;
        this.image_link = image_link;
        this.menu_id = menu_id;
        this.menu_name = menu_name;
    }

    public Cart(String product_id, String cat_id, String product_name, String model, String image_link, String price, String quantity, String parent_cat_name, String sub_cat_name, String store_name, String seller_id) {
        this.product_id = product_id;
        this.cat_id = cat_id;
        this.product_name = product_name;
        this.model = model;
        this.image_link = image_link;
        this.price = price;
        this.quantity = quantity;
        this.parent_cat_name = parent_cat_name;
        this.sub_cat_name = sub_cat_name;
        this.store_name = store_name;
        this.seller_id = seller_id;
    }

    public Cart(int id, String product_id, String cat_id, String product_name, String model, String image_link, String price, String quantity, String parent_cat_name, String sub_cat_name, String store_name, String seller_id) {
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
        this.store_name = store_name;
        this.seller_id = seller_id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public String getTest_short_name() {
        return test_short_name;
    }

    public void setTest_short_name(String test_short_name) {
        this.test_short_name = test_short_name;
    }

    public String getCenter_id() {
        return center_id;
    }

    public void setCenter_id(String center_id) {
        this.center_id = center_id;
    }

    public String getCenter_name() {
        return center_name;
    }

    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    public String getCenter_address() {
        return center_address;
    }

    public void setCenter_address(String center_address) {
        this.center_address = center_address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
