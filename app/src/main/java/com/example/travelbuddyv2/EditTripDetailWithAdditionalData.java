package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class EditTripDetailWithAdditionalData extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialogStartTime , timePickerDialogEndTime;
    EditText timePicker1 , timePicker2 , datePicker , DestinationField;
    Calendar calendar;
    Button btnSubmit;
    int tmpID;
    tripModel tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip_detail_with_additional_data);
        Bundle extra = getIntent().getExtras();

        if(extra!=null)
        {
            tmpID = extra.getInt("extra");
        }

        databaseHelper = new DatabaseHelper(this);
        Toast.makeText(this,new StringBuilder().append(tmpID).toString(),Toast.LENGTH_SHORT).show();

        btnSubmit = findViewById(R.id.btnSubmitEditWithAdditionalData);
        timePicker1 = findViewById(R.id.Timepicker1WithAdditionalData);
        timePicker1.setInputType(InputType.TYPE_NULL);
        timePicker2 = findViewById(R.id.Timepicker2WithAdditionalData);
        timePicker2.setInputType(InputType.TYPE_NULL);
        datePicker = findViewById(R.id.currentDatePickerWithAdditionalData);
        datePicker.setInputType(InputType.TYPE_NULL);
        DestinationField = findViewById(R.id.etDestinationWithAdditionalData);

        DestinationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });

        tmp = databaseHelper.getEditDetail(tmpID);
       // System.out.println(tmp.toString());
        timePicker1.setText(tmp.getStartTime());
        timePicker2.setText(tmp.getEndTime());
        datePicker.setText(tmp.getCurrentDate());
        DestinationField.setText(tmp.getDestination());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp.setDestination(DestinationField.getText().toString());
                databaseHelper.updateTripDetail(tmp);
            }
        });
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}