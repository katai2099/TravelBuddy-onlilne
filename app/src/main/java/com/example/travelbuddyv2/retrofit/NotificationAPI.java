package com.example.travelbuddyv2.retrofit;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjja6Wk0:APA91bHTVxN7b50NBS7QwAPuJSOnxnrw6HmyHG6lXR5v6zb3SDMbSvENisfzjyo4SSWlkDIjKC86LDMBPyx3pSAOGBUrLGfrteU4jOvhpuGjh0XI-5uNEFxRRZqbrR89wgvB1zNGV5_b"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body PushNotification body);

}
