package com.example.travelbuddyv2;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.ChildTripDetailAdapter;
import com.example.travelbuddyv2.adapter.DayAdapter;
import com.example.travelbuddyv2.adapter.GroupTripAdapter;
import com.example.travelbuddyv2.adapter.ParentGroupTripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.TripSection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class GroupTripDetailFragment extends Fragment implements DayAdapter.DayAdapterCallback,
        ParentGroupTripDetailAdapter.ParentGroupTripDetailAdapterCallback ,
        ChildTripDetailAdapter.ChildTripDetailAdapterCallBack {

    private final String tag = "GROUPDETAILFRAGMENT";

    private String tripID, tripOwner;

    List<TripSection> tripSectionList;
    ParentGroupTripDetailAdapter parentGroupTripDetailAdapter;
    RecyclerView rcvGroupTripDetailView,rcvDays;
    List<String> dayList;
    DayAdapter dayAdapter;

    private boolean hasPermission = false;




   // private GroupTripAdapter.AdapterCallBack adapterCallBack;

    public GroupTripDetailFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        if(bundle!=null){

            tripID = bundle.getString("TRIP_STRING_ID");
            tripOwner = bundle.getString("TRIP_OWNER");

        }else{
            Log.d(tag,"NULL");
        }

        checkUserPermission();

        View root = inflater.inflate(R.layout.fragment_group_trip_detail, container, false);
        tripSectionList = new ArrayList<>();

        parentGroupTripDetailAdapter = new ParentGroupTripDetailAdapter(tripSectionList,this,this);

        rcvGroupTripDetailView = root.findViewById(R.id.rcvFragmentGroupTripDetail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvGroupTripDetailView.setLayoutManager(layoutManager);
        rcvGroupTripDetailView.setAdapter(parentGroupTripDetailAdapter);

        //
        dayList = new ArrayList<>();
        rcvDays = root.findViewById(R.id.rcvFragmentGroupTripDetailToSelectedList);
        rcvDays.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        dayAdapter = new DayAdapter(dayList,this);

        rcvDays.setAdapter(dayAdapter);

        fillDateInterval();

        return root;
    }


    private void fillDateInterval(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(tripOwner)
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
                    //Add to tripSection
                    TripSection tripSection = new TripSection(tmp,tmpDestination);
                    tripSectionList.add(tripSection);

                    //add to DayList
                    String day = "Day " + (dayCount+1);
                    dayList.add(day);
                    dayCount++;
                }
                parentGroupTripDetailAdapter.notifyDataSetChanged();
                dayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void checkUserPermission(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(tripOwner)
                .child(tripID)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                if (member!=null&&member.getPermission().equals("edit")) {
                    parentGroupTripDetailAdapter.updateUserPermission(true);
                    hasPermission = true;
                } else {
                    parentGroupTripDetailAdapter.updateUserPermission(false);
                    hasPermission = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onListClicked(int position) {
        rcvGroupTripDetailView.smoothScrollToPosition(position);
    }

    @Override
    public void addNewAttractionClicked(int position) {
        Intent i = new Intent(getContext(),MapsActivity.class);
        Toast.makeText(getContext(),"You may proceed",Toast.LENGTH_SHORT).show();
        i.putExtra("tripStringID",tripID);
        i.putExtra("dateOfTrip",tripSectionList.get(position).getDate());
        i.putExtra("tripOwner",tripOwner);
        i.putExtra("isCurrentUserAMember",true);
        startActivity(i);
    }

    @Override
    public void changeStartTimeClicked(final int position) {
        if(hasPermission) {
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    resetStartTimeOfCurrentDate(position, hourOfDay, minute);

                }
            }, 0, 0, true);
            timePickerDialog.setTitle("modify stay period");
            timePickerDialog.show();
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteDestinationClick(final String date, String destinationStringID) {
        if(hasPermission){
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                    .child(tripOwner)
                    .child(tripID)
                    .child(date)
                    .child(destinationStringID);
            //first check if it is the last child of the node
            final DatabaseReference checkNumberOfChild = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                    .child(tripOwner)
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
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteDestinationClicked(String date, String destinationStringID, int position) {
        if(hasPermission) {
            List<Destination> destinationsOfCurrentDate = new ArrayList<>();
            for (int i = 0; i < tripSectionList.size(); i++) {
                if (tripSectionList.get(i).getDate().equals(date)) {
                    destinationsOfCurrentDate = tripSectionList.get(i).getDestinations();
                }
            }
            List<Destination> destinationsOfCurrentDateReplica = new ArrayList<>(destinationsOfCurrentDate);
            Destination deletedOne = destinationsOfCurrentDateReplica.get(position);
            boolean updateTheRest = false;
            onDeleteDestinationClick(date, destinationStringID);
            if (destinationsOfCurrentDateReplica.size() != 1 && position != destinationsOfCurrentDateReplica.size() - 1) {
                Destination toUpdateDestination = destinationsOfCurrentDateReplica.get(position + 1);
                toUpdateDestination.setExtraDay(deletedOne.getExtraDay());
                toUpdateDestination.setDecreased(deletedOne.isDecreased());
                toUpdateDestination.setIncreased(deletedOne.isIncreased());
                toUpdateDestination.setStartTime(deletedOne.getStartTime());
                Helper.changeStayPeriodOfDestination(toUpdateDestination.getStartDate(), toUpdateDestination.getStartTime(), 0, (int) toUpdateDestination.getDuration(), toUpdateDestination);
                updateToFirebaseAfterPeriodChanged(toUpdateDestination);
                updateTheRest = true;
            }
            if (updateTheRest && destinationsOfCurrentDateReplica.size() != 2) {
                Log.d(tag, "update the rest");
                for (int i = position + 2; i < destinationsOfCurrentDateReplica.size(); i++) {
                    Destination lastDestination = destinationsOfCurrentDateReplica.get(i - 1);
                    Destination toUpdateDestination = destinationsOfCurrentDateReplica.get(i);
                    toUpdateDestination.setExtraDay(lastDestination.getExtraDay());
                    toUpdateDestination.setDecreased(lastDestination.isDecreased());
                    toUpdateDestination.setIncreased(lastDestination.isIncreased());
                    toUpdateDestination.setStartTime(lastDestination.getEndTime());
                    if (lastDestination.isDecreased() && toUpdateDestination.isIncreased())
                        toUpdateDestination.setExtraDay(toUpdateDestination.getExtraDay() + 1);
                    else if (lastDestination.isIncreased() && toUpdateDestination.isIncreased()) {
                        toUpdateDestination.setExtraDay(toUpdateDestination.getExtraDay() + 1);
                    }
                    Helper.changeStayPeriodOfDestination(toUpdateDestination.getStartDate(), toUpdateDestination.getStartTime(), 0, (int) toUpdateDestination.getDuration(), toUpdateDestination);
                    updateToFirebaseAfterPeriodChanged(toUpdateDestination);
                }
            }
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDurationEditingClicked(final int position, final String curDate) {
        if(hasPermission) {
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    changeStayPeriodOfDestination(hourOfDay, minute, position, curDate);

                }
            }, 0, 0, true);
            timePickerDialog.setTitle("modify stay period");
            timePickerDialog.show();
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }


    private void changeStayPeriodOfDestination(int hour, int minute, int position,String currentDate){
        List<Destination> destinations = new ArrayList<>();
        for(TripSection list:tripSectionList){
            if(list.getDate().equals(currentDate))
                destinations = list.getDestinations();
        }
        String startTime = destinations.get(position).getStartTime();
        Helper.changeStayPeriodOfDestination(currentDate,startTime,hour,minute,destinations.get(position));
        updateToFirebaseAfterPeriodChanged(destinations.get(position));
        for(int i=position+1;i<destinations.size();i++){
            Destination lastDestination = destinations.get(i-1);
            destinations.get(i).setExtraDay(lastDestination.getExtraDay());
            destinations.get(i).setStartTime(lastDestination.getEndTime());
            String currentDateOfTheTrip = destinations.get(i).getStartDate();
            String startTimeOfTheTrip = destinations.get(i).getStartTime();
            long duration = destinations.get(i).getDuration();
            int extraDay = destinations.get(i).getExtraDay();
            if(lastDestination.isDecreased() && destinations.get(i).isIncreased())
                destinations.get(i).setExtraDay(extraDay+1);
            else if(lastDestination.isIncreased() && destinations.get(i).isIncreased()){
                destinations.get(i).setExtraDay(extraDay+1);
            }
            Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,startTimeOfTheTrip,0,(int)(duration),destinations.get(i));
            updateToFirebaseAfterPeriodChanged(destinations.get(i));
        }
    }

    private void updateToFirebaseAfterPeriodChanged(Destination destination){
        String destinationStringID = destination.getDestinationStringID();
        String destinationCurDate = destination.getStartDate();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(tripOwner)
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

    private void resetStartTimeOfCurrentDate(int position,int hourOfDay,int minute){
        List<Destination> currentDestinationsOfThisDate = tripSectionList.get(position).getDestinations();
        for(int i=0;i<currentDestinationsOfThisDate.size();i++){
            if(i==0){
                String currentDateOfTheTrip = tripSectionList.get(position).getDate();
                Destination lastDestination = currentDestinationsOfThisDate.get(i);
                String hour = String.valueOf(hourOfDay);
                String min = String.valueOf(minute);
                StringBuilder builder = new StringBuilder();
                builder.append(hour).append(":").append(min);
                String actualClock = builder.toString();
                String resClock = Helper.changeInputTimeFormat(actualClock);
                //        Log.d(tag,"time is " + resClock);
                lastDestination.setStartTime(resClock);
                long duration = lastDestination.getDuration();
                Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,lastDestination.getStartTime(),0,(int)duration,lastDestination);
                updateToFirebaseAfterPeriodChanged(lastDestination);
            }else{
                Destination lastDestination = currentDestinationsOfThisDate.get(i-1);
                currentDestinationsOfThisDate.get(i).setExtraDay(lastDestination.getExtraDay());
                currentDestinationsOfThisDate.get(i).setStartTime(lastDestination.getEndTime());
                String currentDateOfTheTrip = tripSectionList.get(position).getDate();
                String startTimeOfTheTrip = currentDestinationsOfThisDate.get(i).getStartTime();
                int extraDay = currentDestinationsOfThisDate.get(i).getExtraDay();
                if(lastDestination.isDecreased() && currentDestinationsOfThisDate.get(i).isIncreased())
                    currentDestinationsOfThisDate.get(i).setExtraDay(extraDay+1);
                else if(lastDestination.isIncreased() && currentDestinationsOfThisDate.get(i).isIncreased()){
                    currentDestinationsOfThisDate.get(i).setExtraDay(extraDay+1);
                }
                Log.d(tag,"extra dayyyyy is " + extraDay);
                long duration = currentDestinationsOfThisDate.get(i).getDuration();
                Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,startTimeOfTheTrip,0,(int)(duration),currentDestinationsOfThisDate.get(i));
                Log.d(tag,"extra dayyyyy is " + currentDestinationsOfThisDate.get(i).getExtraDay());
                updateToFirebaseAfterPeriodChanged(currentDestinationsOfThisDate.get(i));
            }
        }
    }
}