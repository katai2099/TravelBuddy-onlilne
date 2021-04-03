package com.example.travelbuddyv2.retrofit;

public class PushNotification {
    NotificationData data;
    String to;

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }
}
