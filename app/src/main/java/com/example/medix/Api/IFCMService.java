package com.example.medix.Api;

import com.example.medix.Model.DataMessage;
import com.example.medix.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAIrQyPfQ:APA91bE0A0clLCpGQdNvH3Hooa0OGNi9q5gxtegOdqEzAUT_xMS1h9BZAhHoQ2TkmQ8_J-xnMVCg8nP38PjkJgQVibD70jYGAKeDiEN9stk8LdKj8nJ7vQdlPkZ6Z2NNgaaKwNrhGEZL"

    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}