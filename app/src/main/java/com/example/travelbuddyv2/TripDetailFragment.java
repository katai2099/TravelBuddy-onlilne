package com.example.travelbuddyv2;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.ChildTripDetailAdapter;
import com.example.travelbuddyv2.adapter.DayAdapter;
import com.example.travelbuddyv2.adapter.ParentTripDetailAdapter;
import com.example.travelbuddyv2.adapter.TripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSOutput;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TripDetailFragment extends Fragment implements DayAdapter.DayAdapterCallback, ChildTripDetailAdapter.ChildTripDetailAdapterCallBack {

    private final String tag = "TRIP_DETAIL_FRAGMENT" ;

    List<TripSection> tripSectionList;
    List<String> dayList;
    RecyclerView rcvTripDetail , rcvDays;
    ParentTripDetailAdapter parentTripDetailAdapter;
    DayAdapter dayAdapter;



    String tripID ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View root = inflater.inflate(R.layout.fragment_trip_detail, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null){
            tripID = bundle.getString("TRIP_STRING_ID");
           //  Toast.makeText(getContext(),bundle.getString("TRIP_STRING_ID"),Toast.LENGTH_SHORT).show();
        }else{
             Toast.makeText(getContext(),"NULL",Toast.LENGTH_SHORT).show();
        }





        tripSectionList = new ArrayList<>();

        //fillList();

        rcvTripDetail = root.findViewById(R.id.rcvFragmentTripDetailList);

        rcvTripDetail.setLayoutManager(new LinearLayoutManager(getActivity()));

        parentTripDetailAdapter = new ParentTripDetailAdapter(tripSectionList,tripID,this);
        rcvTripDetail.setAdapter(parentTripDetailAdapter);

        //Day adapter initialization
        dayList = new ArrayList<>();
        rcvDays = root.findViewById(R.id.rcvFragmentTripDetailToSelectedList);
        rcvDays.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        dayAdapter = new DayAdapter(dayList,this);

        rcvDays.setAdapter(dayAdapter);

        fillDateInterval();

        return root;
    }


    private void fillDateInterval(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripSectionList.clear();
                dayList.clear();
                int dayCount = 0 ;
                for(DataSnapshot data: snapshot.getChildren()){
                    String tmp = data.getKey();
                    Log.d(tag,tmp);
                    List<Destination> tmpDestination = new ArrayList<>();
                   for(DataSnapshot childData: data.getChildren())
                    {
                        Log.d(tag,childData.toString());
                        Destination destination = childData.getValue(Destination.class);
                        tmpDestination.add(destination);
                    }
                   //add to TripSection
                    TripSection tripSection = new TripSection(tmp,tmpDestination);
                    tripSectionList.add(tripSection);

                    //add to DayList
                    String day = "Day " + (dayCount+1);
                    dayList.add(day);
                    dayCount++;
                }
                parentTripDetailAdapter.notifyDataSetChanged();
                dayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    @Override
    public void onListClicked(int position) {
       // rcvTripDetail.scrollToPosition(position);
        rcvTripDetail.smoothScrollToPosition(position);
    }


    @Override
    public void onDeleteDestinationClick(final String date, String destinationStringID) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID)
                .child(date)
                .child(destinationStringID);

        //first check if it is the last child of the node

        final DatabaseReference checkNumberOfChild = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID)
                .child(date);

        checkNumberOfChild.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>1){
                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Delete success",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    checkNumberOfChild.setValue("");
                }
            }
        });

    }

    @Override
    public void onDurationEditingClicked(final int position, final String curDate) {




        final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                changeStayPeriodOfDestination(hourOfDay,minute,position,curDate);

            }
        },0,0,true);


        timePickerDialog.setTitle("modify stay period");


        timePickerDialog.show();
    }

    private void changeStayPeriodOfDestination(int hour, int minute, int position,String currentDate){

        //need to calculate new extraDay , get new EndDate



        List<Destination> destinations = new ArrayList<>();

        for(TripSection list:tripSectionList){
            if(list.getDate().equals(currentDate))
                destinations = list.getDestinations();
        }

        String startTime = destinations.get(position).getStartTime();


        SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toTime = new SimpleDateFormat("HH:mm");

        Date date = new Date();


        try{
            date = toDate.parse(currentDate);
        }catch(ParseException e){
            System.out.println(e);
        }

        Calendar curDate = Calendar.getInstance();
        curDate.setTime(date);

        Date time = new Date();

        try{
            time = toTime.parse(startTime);
        }catch(ParseException e){
            System.out.println(e);
        }

        Calendar curTime = Calendar.getInstance();
        curTime.setTime(time);

        curDate.set(Calendar.HOUR_OF_DAY, curTime.get(Calendar.HOUR_OF_DAY));
        curDate.set(Calendar.MINUTE,curTime.get(Calendar.MINUTE));
        curDate.set(Calendar.MILLISECOND,0);

        curDate.add(Calendar.HOUR_OF_DAY, hour);
        curDate.add(Calendar.MINUTE,minute);

        Date res = curDate.getTime();

        String finalTime = toTime.format(res);

        int extraDay = destinations.get(position).getExtraDay();

        String curDateOfDestination  = destinations.get(position).getStartDate();
        Log.d(tag,"There is the problem down there");
        //here
        int  extraDayAfterPeriodChanged = Helper.calculateExtraDay(curDateOfDestination,startTime,extraDay,hour,minute);

        if(extraDayAfterPeriodChanged==1 ) {
            if(!destinations.get(position).isIncreased()) {
                extraDayAfterPeriodChanged += extraDay;
                destinations.get(position).setIncreased(true);
                destinations.get(position).setDecreased(false);
            }
            else{
                extraDayAfterPeriodChanged = extraDay;
            }
        }else {
            if(extraDay!=0 && !destinations.get(position).isDecreased()) {
                Log.d(tag,"HERE");
                extraDayAfterPeriodChanged = (extraDay - 1);
                destinations.get(position).setIncreased(false);
                destinations.get(position).setDecreased(true);
            }
            else if(!destinations.get(position).isDecreased() && extraDay==0){
                Log.d(tag,"Here2");
                extraDayAfterPeriodChanged = 0;
                destinations.get(position).setIncreased(false);
                destinations.get(position).setDecreased(true);
            }
            else {
                extraDayAfterPeriodChanged = extraDay;
            }
        }

        destinations.get(position).setEndTime( finalTime) ;
        destinations.get(position).setDuration((hour*60) + minute);
        destinations.get(position).setExtraDay(extraDayAfterPeriodChanged);

        updateToFirebaseAfterPeriodChanged(destinations.get(position));

        for(int i=position+1;i<destinations.size();i++){
            Destination lastDestination = destinations.get(i-1);

            destinations.get(i).setExtraDay(lastDestination.getExtraDay());
            destinations.get(i).setStartTime(lastDestination.getEndTime());
            String currentDateOfTheTrip = destinations.get(i).getStartDate();
            String startTimeOfTheTrip = destinations.get(i).getStartTime();
            long duration = destinations.get(i).getDuration();

            Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,startTimeOfTheTrip,0,(int)(duration),destinations.get(i));

            updateToFirebaseAfterPeriodChanged(destinations.get(i));

        }

    }


    private void updateToFirebaseAfterPeriodChanged(Destination destination){

        String destinationStringID = destination.getDestinationStringID();
        String destinationCurDate = destination.getStartDate();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID)
                .child(destinationCurDate)
                .child(destinationStringID);

        reference.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getContext(),"Update done",Toast.LENGTH_SHORT).show();
            }
        });

    }


}