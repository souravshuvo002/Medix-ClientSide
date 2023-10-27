package com.example.medix.Common;
import com.example.medix.Api.FCMClient;
import com.example.medix.Api.IFCMService;
import com.example.medix.Model.SingleProduct;
import com.example.medix.Model.Test;
import com.example.medix.Model.User;

public class Common {

    public static String Customer_email = "";
    public static String Customer_phone = "";
    public static String CUSTOMER_ID = "";
    public static User currentUser;
    public static String CENTER_ID = "";
    public static double report_charge = 40.00;
    public static Test singleTest;
    public static String sellDate = "";
    public static String PRESCRIPTION_IMAGE_URL = "";

    // Shop
    public static String menu_id = "";
    public static String product_name = "";
    public static String product_price = "";
    public static String submenu_name = "";
    public static final String food_id = "1";
    public static int id_menu = 1;
    public static double SHIIPING_CHARGE = 40.0000;
    public static int isCatCodeApplied = 0;

    public static SingleProduct singleProduct;
    public static String tempImageLink = "";



    private static final String FCM_API = "https://fcm.googleapis.com/";

    public static IFCMService getFCMService(){
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("1"))
            return "Pending";
        else if(code.equals("2"))
            return "Accepted";
        else if(code.equals("3"))
            return "Rejected";
        else if(code.equals("4"))
            return "Completed";
        else
            return "No result";
    }

    public static String convertCodeToShopStatus(String code)
    {
        if(code.equals("1"))
            return "Pending";
        else if(code.equals("2"))
            return "Processing";
        else if(code.equals("3"))
            return "Shipped";
        else if(code.equals("5"))
            return "Complete";
        else if(code.equals("7"))
            return "Canceled";
        else if(code.equals("8"))
            return "Denied";
        else if(code.equals("9"))
            return "Canceled Reversal";
        else if(code.equals("10"))
            return "Failed";
        else if(code.equals("11"))
            return "Refunded";
        else if(code.equals("12"))
            return "Reversed";
        else if(code.equals("13"))
            return "Chargeback";
        else if(code.equals("14"))
            return "Expired";
        else if(code.equals("15"))
            return "Processed";
        else if(code.equals("16"))
            return "Processed";
        else
            return "No result";
    }
}


