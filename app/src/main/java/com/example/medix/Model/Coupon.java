package com.example.medix.Model;

public class Coupon {

    private String coupon_id, code, type, discount, total,uses_total, code_uses_total, uses_customer, code_customer_uses_total, isGen, isCat, isProduct;
    private String category_id, name, product_id;
    private String parent_id, parent_cat_name;


    public Coupon() {
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUses_total() {
        return uses_total;
    }

    public void setUses_total(String uses_total) {
        this.uses_total = uses_total;
    }

    public String getCode_uses_total() {
        return code_uses_total;
    }

    public void setCode_uses_total(String code_uses_total) {
        this.code_uses_total = code_uses_total;
    }

    public String getUses_customer() {
        return uses_customer;
    }

    public void setUses_customer(String uses_customer) {
        this.uses_customer = uses_customer;
    }

    public String getCode_customer_uses_total() {
        return code_customer_uses_total;
    }

    public void setCode_customer_uses_total(String code_customer_uses_total) {
        this.code_customer_uses_total = code_customer_uses_total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsGen() {
        return isGen;
    }

    public void setIsGen(String isGen) {
        this.isGen = isGen;
    }

    public String getIsCat() {
        return isCat;
    }

    public void setIsCat(String isCat) {
        this.isCat = isCat;
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_cat_name() {
        return parent_cat_name;
    }

    public void setParent_cat_name(String parent_cat_name) {
        this.parent_cat_name = parent_cat_name;
    }

    public String getIsProduct() {
        return isProduct;
    }

    public void setIsProduct(String isProduct) {
        this.isProduct = isProduct;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
