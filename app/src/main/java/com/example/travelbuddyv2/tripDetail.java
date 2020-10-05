package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class tripDetail extends AppCompatActivity implements mainRecyclerAdapter.mainAdapterListener , childRecyclerAdapter.ChildAdapterListener {

    FloatingActionButton fabEditInformation;
    int tmpID;
    RecyclerView rcvTripDetail;
    List<tripSection> sectionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        Bundle extra = getIntent().getExtras();
        if(extra!=null)
        {
            tmpID = extra.getInt("TripID");
        }
        initData();
        Toast.makeText(this,new StringBuilder().append(tmpID).toString(),Toast.LENGTH_SHORT).show();
//        System.out.println(sectionList.get(0).getTripList().get(0).toString());
        rcvTripDetail = findViewById(R.id.rcvTripDetailList);
        rcvTripDetail.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerAdapter mainrecycler = new mainRecyclerAdapter(sectionList,this,this);
        rcvTripDetail.setAdapter(mainrecycler);



        fabEditInformation = findViewById(R.id.addNewTripDetail);
        fabEditInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(tripDetail.this,EditTrip.class);
                i.putExtra("TripIDfromTripDetail",tmpID);
                startActivity(i);
            }
        });
       // textView.setText(tmp);
    }


    private void initData() {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        String startDate , endDate;

        startDate = databaseHelper.getStartDateOfTrip(tmpID);
        endDate = databaseHelper.getEndDateOfTrip(tmpID);

        List<String> dateList = getDateInterval(startDate,endDate);

        for(int i=0;i<dateList.size();i++)
        {
            //System.out.println(dateList.get(i));
            String sectionName = dateList.get(i);
            List<tripModel> sectionItems = databaseHelper.getTripListOnACertainDate(sectionName,tmpID);
            sectionList.add(new tripSection(sectionName,sectionItems));
        }

    }

    private List<String> getDateInterval(String startDate , String endDate)  {
        SimpleDateFormat simpleDateFormat;
        Date start , end ;
        Calendar calendar;

        start = new Date();
        end = new Date();
        calendar = new GregorianCalendar();
        List<String> listOfdate = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            start = simpleDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(start);
        // while (calendar.getTime().before(endDate))
        while(calendar.getTime().before(end))
        {
            Date result = calendar.getTime();
            String tmp = simpleDateFormat.format(result);
            listOfdate.add(tmp);
            calendar.add(Calendar.DATE, 1);
        }
        String tmp = simpleDateFormat.format(calendar.getTime());
        //System.out.println(calendar.getTime().toString());
        listOfdate.add(tmp);

        return listOfdate;
    }

    @Override
    public void onTitleClicked(int position) {
        Toast.makeText(this,new StringBuilder().append(position).toString(),Toast.LENGTH_SHORT).show();
        System.out.println(position);
    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this,new StringBuilder().append(position).toString(),Toast.LENGTH_SHORT).show();
    }
    /*
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(tripDetail.this,"You have swipe " + new StringBuilder().append(viewHolder.getAdapterPosition()).toString(),Toast.LENGTH_SHORT).show();
        }
    }; */


}