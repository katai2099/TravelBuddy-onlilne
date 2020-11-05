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

import java.sql.Time;
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
    mainRecyclerAdapter mainrecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        Bundle extra = getIntent().getExtras();
        if(extra!=null)
        {
            tmpID = extra.getInt("TripID");
        }
        DatabaseHelper db = new DatabaseHelper(this);
        String title = db.getTripName(tmpID);
        this.setTitle(title);
       // initData();
        Toast.makeText(this,new StringBuilder().append(tmpID).toString(),Toast.LENGTH_SHORT).show();
//        System.out.println(sectionList.get(0).getTripList().get(0).toString());
        rcvTripDetail = findViewById(R.id.rcvTripDetailList);
        rcvTripDetail.setLayoutManager(new LinearLayoutManager(this));
        mainrecycler = new mainRecyclerAdapter(sectionList,this,this);
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
            sectionList.get(i).sortTrip();
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
        Toast.makeText(this,new StringBuilder().append(position).append(" Parent Clicked").toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteClickedTripDetail(int id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.DeleteTripDetail(id);
        //Item is not remove from list; !!!! Fix here , it need to be removed from the list
        deleteTripDetailFromList(id);
        mainrecycler.notifyDataSetChanged();
       //rcvTripDetail.setAdapter(mainrecycler);
    }

    @Override
    public void onItemClicked(int position) {
        //Toast.makeText(this,new StringBuilder().append(position).append(" Child Clicked").toString(),Toast.LENGTH_SHORT).show();
      //  DatabaseHelper databaseHelper = new DatabaseHelper(this);
       // databaseHelper.DeleteTripDetail(position);

        //we do this so we can get reference of listID in childView recyCler view
        mainrecycler.tmp_id_from_child = position;

    }

    @Override
    public void onItemClickedToEdit(int position) {
        Intent i = new Intent(tripDetail.this,EditTripDetailWithAdditionalData.class);
        i.putExtra("extra",position);
        startActivity(i);
    }


    public void deleteTripDetailFromList(int id){
        for(int i=0;i<sectionList.size();i++)
        {
            for(int j=0;j<sectionList.get(i).getTripList().size();j++)
            {
                if(id==sectionList.get(i).getTripList().get(j).getId())
                    sectionList.get(i).getTripList().remove(j);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        sectionList.clear();
        initData();
        mainrecycler.notifyDataSetChanged();
        Toast.makeText(this,"Resume",Toast.LENGTH_SHORT).show();
    }
}