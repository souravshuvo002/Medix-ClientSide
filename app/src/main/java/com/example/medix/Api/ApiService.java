package com.example.medix.Api;

import com.example.medix.Model.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    //get all banners
    @GET("getBanner")
    Call<Result> getBanners();

    // register call
    @FormUrlEncoded
    @POST("registerCustomer")
    Call<Result> registerCustomer(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("address") String address);


    // login call
    @FormUrlEncoded
    @POST("loginCustomer")
    Call<Result> loginCustomer(
            @Field("phone") String phone,
            @Field("password") String password);

    //get all diagnostic center name
    @GET("getAllDiagnosticCenter")
    Call<Result> getAllDiagnosticCenter();

    //get all test based on diagnostic_center_id
    @GET("getTestByCenterID/{diagnostic_center_id}")
    Call<Result> getTestByCenterID(@Path("diagnostic_center_id") String diagnostic_center_id);

    //get all diagnostic Test details
    @GET("getAllDiagnosticTest")
    Call<Result> getAllDiagnosticTest();

    //get all diagnostic Test details by limit
    @GET("getAllDiagnosticTestLimit")
    Call<Result> getAllDiagnosticTestLimit();

    // Add Review
    @FormUrlEncoded
    @POST("addTestReview")
    Call<Result> addTestReview(
            @Field("test_id") String test_id,
            @Field("customer_id") String customer_id,
            @Field("author") String author,
            @Field("text") String text,
            @Field("rating") String rating,
            @Field("status") String status);

    // getting test reviews
    @GET("getTestReviews/{test_id}")
    Call<Result> getTestReviews(@Path("test_id") String test_id);


    @GET()
    @Streaming
    Call<ResponseBody> downloadImage(@Url String fileUrl);

    // Uploading Prescription
    @Multipart
    @POST("uploadPrescription")
    Call<Result> uploadPrescription(
            @Part("customer_id") RequestBody customer_id,
            @Part("customer_name") RequestBody customer_name,
            @Part("customer_phone") RequestBody customer_phone,
            @Part("note") RequestBody note,
            @Part MultipartBody.Part file);

    // getting all uploaded prescription based on customer_id
    @GET("getPrescriptionByCustomerID/{customer_id}")
    Call<Result> getPrescriptionByCustomerID(@Path("customer_id") String customer_id);

    // Add test booking for admin
    @FormUrlEncoded
    @POST("addTestBookingAdmin")
    Call<Result> addTestBookingAdmin(
            @Field("center_id") String center_id,
            @Field("test_id") String test_id,
            @Field("customer_id") String customer_id,
            @Field("price") String price,
            @Field("booking_date") String booking_date,
            @Field("status") String status);

    // place order on oc_order
    @FormUrlEncoded
    @POST("addBookTest")
    Call<Result> addBookTest(
            @Field("invoice_prefix") String invoice_prefix,
            @Field("customer_id") String customer_id,
            @Field("customer_name") String customer_name,
            @Field("customer_email") String customer_email,
            @Field("customer_phone") String customer_phone,
            @Field("customer_address") String customer_address,
            @Field("payment_method") String payment_method,
            @Field("total") String total,
            @Field("book_status_id") String book_status_id);

    //Get Last inserted by of customer by customer_id
    @FormUrlEncoded
    @POST("getLastInsertedID")
    Call<Result> getLastInsertedID(
            @Field("customer_id") String customer_id);

    // Push test details to tbl_diag_test_booking
    @FormUrlEncoded
    @POST("addTestDetailsToCenter")
    Call<Result> addTestDetailsToCenter(
            @Field("test_book_id") String test_book_id,
            @Field("center_id") String center_id,
            @Field("test_id") String test_id,
            @Field("customer_id") String customer_id,
            @Field("quantity") String quantity,
            @Field("price") String price,
            @Field("total_price") String total_price,
            @Field("book_status_id") String book_status_id,
            @Field("booking_date") String booking_date);


    // add details for diagnostic home delivery
    @FormUrlEncoded
    @POST("addCenterHomeDelivery")
    Call<Result> addCenterHomeDelivery(
            @Field("test_book_id") String test_book_id,
            @Field("center_id") String center_id,
            @Field("charge") String charge);


    //Get all booking history of customer by customer_id
    @FormUrlEncoded
    @POST("getAllBookingForCustomer")
    Call<Result> getAllBookingForCustomer(
            @Field("customer_id") String customer_id);

    //get book details
    @FormUrlEncoded
    @POST("getBookDetails")
    Call<Result> getBookDetails(@Field("test_book_id") String test_book_id);

    //Get Book Details for test
    @FormUrlEncoded
    @POST("getTestBookDetails")
    Call<Result> getTestBookDetails(
            @Field("test_book_id") String test_book_id,
            @Field("customer_id") String customer_id);

    //updating token for customer
    @FormUrlEncoded
    @POST("updateCustomerToken")
    Call<Result> updateCustomerToken(
            @Field("customer_id") String customer_id,
            @Field("token") String token);

    //get diagnostic center token
    @GET("getDiagnosticToken/{diagnostic_center_id}")
    Call<Result> getDiagnosticToken(@Path("diagnostic_center_id") String diagnostic_center_id);










    // Medicine Shop

    // register call
    @FormUrlEncoded
    @POST("registerCustomer")
    Call<Result> registerCustomer2(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("telephone") String telephone,
            @Field("password") String password,
            @Field("ip") String ip);


    // login call
    @FormUrlEncoded
    @POST("loginCustomer")
    Call<Result> loginCustomer2(
            @Field("email") String email,
            @Field("password") String password);

    // Check user call
    @FormUrlEncoded
    @POST("checkUser")
    Call<Result> checkUser(
            @Field("phone") String phone);

    // Get customer info call
    @FormUrlEncoded
    @POST("getCustomerInfo")
    Call<Result> getCustomerInfo(
            @Field("email") String email);

    // update customer call
    @FormUrlEncoded
    @POST("updateCustomer/{customer_id}")
    Call<Result> updateCustomerInfo(@Path("customer_id") String customer_id,
                                    @Field("firstname") String firstname,
                                    @Field("lastname") String lastname,
                                    @Field("email") String email,
                                    @Field("telephone") String telephone,
                                    @Field("password") String password);

    /*//get all banners
    @GET("getBanner")
    Call<Result> getBanners();*/

    // getting all images of products
    @GET("getProductImages/{product_id}")
    Call<Result> getProductImages(@Path("product_id") String product_id);


    //get all Country data
    @GET("getCountry")
    Call<Result> getCountry();

    //get all state/region based on country_id
    @GET("getStateRegion/{country_id}")
    Call<Result> getStateRegion(@Path("country_id") String country_id);

    //get all MainCategory's
    @GET("getMenu")
    Call<Result> getMenu();

    @GET("getMainCategory")
    Call<Result> getAllCategory();

    //get all MainCategory's by Limit
    @GET("getMainCategoryByLimit")
    Call<Result> getMainCategoryByLimit();


    //get all MainCategory's by Limit
    @GET("getMenuByLimit")
    Call<Result> getMenuByLimit();

    //get all Sub Cat based on menu_id
    @GET("getSubCategoryById/{parent_id}")
    Call<Result> getSubCategoryById(@Path("parent_id") String menu_id);

    // getting product by sub cat and main cat: cat_id
    @GET("getProductBySubMainCatId/{category_id}")
    Call<Result> getProductBySubCatId(@Path("category_id") String category_id);

    // Add Review
    @FormUrlEncoded
    @POST("addReview")
    Call<Result> addReview(
            @Field("product_id") String product_id,
            @Field("customer_id") String customer_id,
            @Field("author") String author,
            @Field("text") String text,
            @Field("rating") String rating,
            @Field("status") String status);

    // getting product reviews
    @GET("getReviews/{product_id}")
    Call<Result> getReviews(@Path("product_id") String product_id);

    // getting product review
    @GET("getProductReviews/{product_id}")
    Call<Result> getProductReviewsForCat(@Path("product_id") String product_id);


    // Check wishlist
    @FormUrlEncoded
    @POST("checkWishlist")
    Call<Result> checkWishlist(
            @Field("customer_id") String customer_id,
            @Field("product_id") String product_id);

    // add to wishlist
    @FormUrlEncoded
    @POST("addWishlist")
    Call<Result> addWishlist(
            @Field("customer_id") String customer_id,
            @Field("product_id") String product_id);

    @FormUrlEncoded
    // removing cutomer wishlist
    @POST("removeWishlist")
    Call<Result> removeWishlist(@Field("customer_id") String customer_id,
                                @Field("product_id") String product_id);

    //get all wish list of customer by customer_id
    @GET("getCustomerWishList/{customer_id}")
    Call<Result> getCustomerWishList(@Path("customer_id") String customer_id);

    //get all Drinks based on menu_id
    @GET("getDrinkByMenuId/{menu_id}")
    Call<Result> getDrinkByMenuId(@Path("menu_id") String menu_id);


    // place order on oc_order
    @FormUrlEncoded
    @POST("addOrder")
    Call<Result> addOrder(
            @Field("invoice_no") String invoice_no,
            @Field("invoice_prefix") String invoice_prefix,
            @Field("store_id") String store_id,
            @Field("store_name") String store_name,
            @Field("store_url") String store_url,
            @Field("customer_id") String customer_id,
            @Field("customer_group_id") String customer_group_id,
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("telephone") String telephone,
            @Field("fax") String fax,
            @Field("custom_field") String custom_field,
            @Field("payment_firstname") String payment_firstname,
            @Field("payment_lastname") String payment_lastname,
            @Field("payment_company") String payment_company,
            @Field("payment_address_1") String payment_address_1,
            @Field("payment_address_2") String payment_address_2,
            @Field("payment_city") String payment_city,
            @Field("payment_postcode") String payment_postcode,
            @Field("payment_country") String payment_country,
            @Field("payment_country_id") String payment_country_id,
            @Field("payment_zone") String payment_zone,
            @Field("payment_zone_id") String payment_zone_id,
            @Field("payment_address_format") String payment_address_format,
            @Field("payment_custom_field") String payment_custom_field,
            @Field("payment_method") String payment_method,
            @Field("payment_code") String payment_code,
            @Field("shipping_firstname") String shipping_firstname,
            @Field("shipping_lastname") String shipping_lastname,
            @Field("shipping_company") String shipping_company,
            @Field("shipping_address_1") String shipping_address_1,
            @Field("shipping_address_2") String shipping_address_2,
            @Field("shipping_city") String shipping_city,
            @Field("shipping_postcode") String shipping_postcode,
            @Field("shipping_country") String shipping_country,
            @Field("shipping_country_id") String shipping_country_id,
            @Field("shipping_zone") String shipping_zone,
            @Field("shipping_zone_id") String shipping_zone_id,
            @Field("shipping_address_format") String shipping_address_format,
            @Field("shipping_custom_field") String shipping_custom_field,
            @Field("shipping_method") String shipping_method,
            @Field("shipping_code") String shipping_code,
            @Field("comment") String comment,
            @Field("total") double total,
            @Field("order_status_id") String order_status_id,
            @Field("affiliate_id") String affiliate_id,
            @Field("commission") String commission,
            @Field("marketing_id") String marketing_id,
            @Field("tracking") String tracking,
            @Field("language_id") String language_id,
            @Field("currency_id") String currency_id,
            @Field("currency_code") String currency_code,
            @Field("currency_value") String currency_value,
            @Field("ip") String ip,
            @Field("forwarded_ip") String forwarded_ip,
            @Field("user_agent") String user_agent,
            @Field("accept_language") String accept_language);

    //Get Last inserted by of customer by customer_id
    @FormUrlEncoded
    @POST("getLasteInsertedID")
    Call<Result> getLasteInsertedID(
            @Field("customer_id") String customer_id);

    //Get all order of customer by customer_id
    @FormUrlEncoded
    @POST("getAllOrderForCustomer")
    Call<Result> getAllOrderForCustomer(
            @Field("customer_id") String customer_id);

    // Push product details to oc_order_product
    @FormUrlEncoded
    @POST("addOrderProductDetails")
    Call<Result> addOrderProductDetails(
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("name") String name,
            @Field("model") String model,
            @Field("quantity") String quantity,
            @Field("price") String price,
            @Field("total") String total,
            @Field("tax") String tax,
            @Field("reward") String reward);

    // Push product details to oc_purpletree_vendor_orders
    @FormUrlEncoded
    @POST("addOrderProductDetailsForVendor")
    Call<Result> addOrderProductDetailsForVendor(
            @Field("seller_id") String seller_id,
            @Field("product_id") String product_id,
            @Field("order_id") String order_id,
            @Field("shipping") String shipping,
            @Field("quantity") String quantity,
            @Field("unit_price") String unit_price,
            @Field("total_price") String total_price,
            @Field("order_status_id") String order_status_id,
            @Field("seen") String seen);

    // Push product history to oc_purpletree_vendor_orders_history
    @FormUrlEncoded
    @POST("addOrderProductHistoryForVendor")
    Call<Result> addOrderProductHistoryForVendor(
            @Field("seller_id") String seller_id,
            @Field("order_id") String order_id,
            @Field("order_status_id") String order_status_id,
            @Field("comment") String comment,
            @Field("notify") String notify);

    // Push product history to oc_order_history
    @FormUrlEncoded
    @POST("addOrderProductGeneralHistory")
    Call<Result> addOrderProductGeneralHistory(
            @Field("order_id") String order_id,
            @Field("order_status_id") String order_status_id,
            @Field("notify") String notify,
            @Field("comment") String comment);

    //get order details
    @FormUrlEncoded
    @POST("getOrderDetails")
    Call<Result> getOrderDetails(@Field("order_id") String order_id);

    //get Order Details for products
    @FormUrlEncoded
    @POST("getProductOrderDetails")
    Call<Result> getProductOrderDetails(@Field("order_id") String order_id);


    // place order
    @FormUrlEncoded
    @POST("placeOrder")
    Call<Result> placeOrder(
            @Field("id_order") String id_order,
            @Field("phone") String phone,
            @Field("name") String name,
            @Field("total_price") String total_price,
            @Field("address") String address,
            @Field("order_status") String order_status,
            @Field("product_id") String product_id,
            @Field("product_name") String product_name,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_image_link") String product_image_link,
            @Field("product_menu_id") String product_menu_id,
            @Field("product_menu_name") String product_menu_name);


    //get order details
    @FormUrlEncoded
    @POST("getOrderByID")
    Call<Result> getOrderByID(@Field("id_order") String id_order);

    //get order details
    @FormUrlEncoded
    @POST("getSellerInfoForProduct")
    Call<Result> getSellerInfoForProduct(@Field("product_id") String product_id);


    // place order
    @FormUrlEncoded
    @POST("checkDrinkItemExistance")
    Call<Result> checkDrinkItemExistance(
            @Field("id") int id,
            @Field("name") String name,
            @Field("price") String price,
            @Field("menu_id") int menu_id);

    // Get user all Cart Details
    @FormUrlEncoded
    @POST("getAllOrder")
    Call<Result> getAllOrder(
            @Field("phone") String phone);

    //get all product
    @GET("getAllProduct")
    Call<Result> getAllProduct();

    //get all product
    @GET("getAllLatestProduct/{limit}")
    Call<Result> getAllLatestProductLimit(@Path("limit") String limit);

    //get all hot deal product by limit
    @GET("getAllHotDealProductLimit")
    Call<Result> getAllHotDealProductLimit();

    //get all hot deal product
    @GET("getAllHotDealProduct")
    Call<Result> getAllHotDealProduct();


    //getting all related product by product_id
    @GET("getRelatedProduct/{product_id}")
    Call<Result> getRelatedProduct(@Path("product_id") String product_id);


    //get food comments based on food_id
    @GET("getFoodCommentsByFoodId/{id_food}")
    Call<Result> getFoodCommentsByFoodId(@Path("id_food") String id_food);

    // post review for user
    @FormUrlEncoded
    @POST("submitReview")
    Call<Result> submitReview(
            @Field("id_food") int id_food,
            @Field("food_name") String food_name,
            @Field("rate_value") String rate_value,
            @Field("comments") String comments,
            @Field("id_menu") int id_menu,
            @Field("menu_name") String menu_name,
            @Field("user_name") String user_name,
            @Field("comments_date") String comments_date);

    // post order total, subtotal and shipping cost
    @FormUrlEncoded
    @POST("addOrderTotalHistory")
    Call<Result> addOrderTotalHistory(
            @Field("order_id") String order_id,
            @Field("code") String code,
            @Field("title") String title,
            @Field("value") String value,
            @Field("sort_order") String sort_order);

    // Check Coupon Code
    @FormUrlEncoded
    @POST("CheckCouponCode")
    Call<Result> checkCouponCode(
            @Field("code") String code,
            @Field("customer_id") String customer_id);

    // push to oc_coupon_history
    @FormUrlEncoded
    @POST("AddCouponHistory")
    Call<Result> addCouponHistory(
            @Field("coupon_id") String coupon_id,
            @Field("order_id") String order_id,
            @Field("customer_id") String customer_id,
            @Field("amount") String amount);


    //get category_id by coupon_id
    @GET("getCategoryByCouponID/{coupon_id}")
    Call<Result> getCategoryByCouponID(@Path("coupon_id") String coupon_id);

    //get product_id by coupon_id
    @GET("getProductByCouponID/{coupon_id}")
    Call<Result> getProductByCouponID(@Path("coupon_id") String coupon_id);

    //get category_id by product_id
    @GET("getCategoryByProductID/{product_id}")
    Call<Result> getCategoryByProductID(@Path("product_id") String product_id);

    // Get parent_cat by cat_id
    @FormUrlEncoded
    @POST("getParentCatByCatID")
    Call<Result> getParentCatByCatID(
            @Field("category_id") String category_id);
}
