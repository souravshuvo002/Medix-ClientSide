package com.example.medix.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("banner")
    private List<Banner> banners;

    @SerializedName("user")
    private User user;

    @SerializedName("alldiagnostic")
    private List<Diagnostic> allDiagnosticCenter;

    @SerializedName("testByDiagnostic")
    private List<Test> allTestByDiagnostic;

    @SerializedName("alldiagnosticTest")
    private List<Test> allDiagnosticTest;

    @SerializedName("testReviews")
    private List<Rating> ratingList;

    @SerializedName("singleProductReviews")
    private List<Test> singleProductsReviewList;

    @SerializedName("uploadPrescription")
    private List<Prescription> allUploadedPrescription;

    @SerializedName("lastInsertID")
    private Book lastBookID;

    @SerializedName("allBook")
    private List<Book> allBookList;

    @SerializedName("bookDetails")
    private Book bookDetails;

    @SerializedName("bookTestDetails")
    private List<Book> allbookTestDetails;

    @SerializedName("diagToken")
    private Diagnostic diagnosticToken;



    // Shop
    @SerializedName("category")
    private List<MainCategory> menus;

    @SerializedName("getAllLatestProductLimit")
    private List<SingleProduct> allLatestProductLimit;

    @SerializedName("HotDealProduct")
    private List<SingleProduct> allHotDealProduct;

    @SerializedName("allProduct")
    private List<SingleProduct> allProductList;

    @SerializedName("allRelatedProduct")
    private List<SingleProduct> allRelatedProduct;

    @SerializedName("productImages")
    private List<Banner> productImagesList;

    @SerializedName("productReviews")
    private List<Rating> productratingList;

    @SerializedName("coupon")
    private Coupon couponDetails;

    @SerializedName("couponCat")
    private List<Coupon> allCategoryByCouponId;

    @SerializedName("couponPro")
    private List<Coupon> allProductByCouponId;

    @SerializedName("catByProduct")
    private List<MainCategory> allCategoryByProduct;

    @SerializedName("lastOrder")
    private Orders last_order_id;

    @SerializedName("country")
    private List<Country> countryList;

    @SerializedName("stateRegion")
    private List<StateRegion> stateRegionList;

    @SerializedName("allOrder")
    private List<Orders> allOrder;

    @SerializedName("orderDetails")
    private Orders orderDetailsList;

    @SerializedName("orderProductDetails")
    private List<Orders> orderProductDetails;

    @SerializedName("productBySubMainCatID")
    private List<SingleProduct> singleProductList;

    @SerializedName("subCatByParentID")
    private List<SubCategory> subCategories;

    @SerializedName("getCustomerWishList")
    private List<WishList> wishListList;


    public Result(Boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Diagnostic> getAllDiagnosticCenter() {
        return allDiagnosticCenter;
    }

    public void setAllDiagnosticCenter(List<Diagnostic> allDiagnosticCenter) {
        this.allDiagnosticCenter = allDiagnosticCenter;
    }

    public List<Test> getAllTestByDiagnostic() {
        return allTestByDiagnostic;
    }

    public void setAllTestByDiagnostic(List<Test> allTestByDiagnostic) {
        this.allTestByDiagnostic = allTestByDiagnostic;
    }

    public List<Test> getAllDiagnosticTest() {
        return allDiagnosticTest;
    }

    public void setAllDiagnosticTest(List<Test> allDiagnosticTest) {
        this.allDiagnosticTest = allDiagnosticTest;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    public List<Test> getSingleProductsReviewList() {
        return singleProductsReviewList;
    }

    public void setSingleProductsReviewList(List<Test> singleProductsReviewList) {
        this.singleProductsReviewList = singleProductsReviewList;
    }

    public List<Prescription> getAllUploadedPrescription() {
        return allUploadedPrescription;
    }

    public void setAllUploadedPrescription(List<Prescription> allUploadedPrescription) {
        this.allUploadedPrescription = allUploadedPrescription;
    }

    public Book getLastBookID() {
        return lastBookID;
    }

    public void setLastBookID(Book lastBookID) {
        this.lastBookID = lastBookID;
    }

    public List<Book> getAllBookList() {
        return allBookList;
    }

    public void setAllBookList(List<Book> allBookList) {
        this.allBookList = allBookList;
    }

    public Book getBookDetails() {
        return bookDetails;
    }

    public void setBookDetails(Book bookDetails) {
        this.bookDetails = bookDetails;
    }

    public List<Book> getAllbookTestDetails() {
        return allbookTestDetails;
    }

    public void setAllbookTestDetails(List<Book> allbookTestDetails) {
        this.allbookTestDetails = allbookTestDetails;
    }

    public Diagnostic getDiagnosticToken() {
        return diagnosticToken;
    }

    public void setDiagnosticToken(Diagnostic diagnosticToken) {
        this.diagnosticToken = diagnosticToken;
    }

    public List<MainCategory> getMenus() {
        return menus;
    }

    public void setMenus(List<MainCategory> menus) {
        this.menus = menus;
    }

    public List<SingleProduct> getAllLatestProductLimit() {
        return allLatestProductLimit;
    }

    public void setAllLatestProductLimit(List<SingleProduct> allLatestProductLimit) {
        this.allLatestProductLimit = allLatestProductLimit;
    }

    public List<SingleProduct> getAllHotDealProduct() {
        return allHotDealProduct;
    }

    public void setAllHotDealProduct(List<SingleProduct> allHotDealProduct) {
        this.allHotDealProduct = allHotDealProduct;
    }

    public List<SingleProduct> getAllProductList() {
        return allProductList;
    }

    public void setAllProductList(List<SingleProduct> allProductList) {
        this.allProductList = allProductList;
    }

    public List<SingleProduct> getAllRelatedProduct() {
        return allRelatedProduct;
    }

    public void setAllRelatedProduct(List<SingleProduct> allRelatedProduct) {
        this.allRelatedProduct = allRelatedProduct;
    }

    public List<Banner> getProductImagesList() {
        return productImagesList;
    }

    public void setProductImagesList(List<Banner> productImagesList) {
        this.productImagesList = productImagesList;
    }

    public List<Rating> getProductratingList() {
        return productratingList;
    }

    public void setProductratingList(List<Rating> productratingList) {
        this.productratingList = productratingList;
    }

    public Coupon getCouponDetails() {
        return couponDetails;
    }

    public void setCouponDetails(Coupon couponDetails) {
        this.couponDetails = couponDetails;
    }

    public List<Coupon> getAllCategoryByCouponId() {
        return allCategoryByCouponId;
    }

    public void setAllCategoryByCouponId(List<Coupon> allCategoryByCouponId) {
        this.allCategoryByCouponId = allCategoryByCouponId;
    }

    public List<Coupon> getAllProductByCouponId() {
        return allProductByCouponId;
    }

    public void setAllProductByCouponId(List<Coupon> allProductByCouponId) {
        this.allProductByCouponId = allProductByCouponId;
    }

    public List<MainCategory> getAllCategoryByProduct() {
        return allCategoryByProduct;
    }

    public void setAllCategoryByProduct(List<MainCategory> allCategoryByProduct) {
        this.allCategoryByProduct = allCategoryByProduct;
    }

    public Orders getLast_order_id() {
        return last_order_id;
    }

    public void setLast_order_id(Orders last_order_id) {
        this.last_order_id = last_order_id;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public List<StateRegion> getStateRegionList() {
        return stateRegionList;
    }

    public void setStateRegionList(List<StateRegion> stateRegionList) {
        this.stateRegionList = stateRegionList;
    }

    public List<Orders> getAllOrder() {
        return allOrder;
    }

    public void setAllOrder(List<Orders> allOrder) {
        this.allOrder = allOrder;
    }

    public Orders getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(Orders orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

    public List<Orders> getOrderProductDetails() {
        return orderProductDetails;
    }

    public void setOrderProductDetails(List<Orders> orderProductDetails) {
        this.orderProductDetails = orderProductDetails;
    }

    public List<SingleProduct> getSingleProductList() {
        return singleProductList;
    }

    public void setSingleProductList(List<SingleProduct> singleProductList) {
        this.singleProductList = singleProductList;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public List<WishList> getWishListList() {
        return wishListList;
    }

    public void setWishListList(List<WishList> wishListList) {
        this.wishListList = wishListList;
    }
}
