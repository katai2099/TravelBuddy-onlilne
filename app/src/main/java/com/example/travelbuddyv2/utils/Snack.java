package com.example.travelbuddyv2.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Snack {

    public Snack(View view, String message){
        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
    }

}
