package com.example.travelbuddyv2;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.ChildTripDetailAdapter;
import com.example.travelbuddyv2.adapter.DayAdapter;
import com.example.travelbuddyv2.adapter.ParentGroupTripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.TripSection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//store information of group trip, called when member click on the group
public class GroupTripDetailFragment extends Fragment implements DayAdapter.DayAdapterCallback,
        ParentGroupTripDetailAdapter.ParentGroupTripDetailAdapterCallback ,
        ChildTripDetailAdapter.ChildTripDetailAdapterCallBack {

    private final String tag = "GROUPDETAILFRAGMENT";
    private boolean firstLoad = true;
    private String tripID, tripOwner;
    private boolean isDayListClicked = false;
    List<TripSection> tripSectionList;
    ParentGroupTripDetailAdapter parentGroupTripDetailAdapter;
    RecyclerView rcvGroupTripDetail,rcvDays;
    List<String> dayList;
    DayAdapter dayAdapter;
    View updatingText;
    ProgressBar progressBar,updatingProgressBar;
    int updatingDestinationSize, updatingCurrentPosition;
    //variable to check if any data is being updated
    boolean isUpdate = false;
    //variable to check if current member has permission to edit the trip
    private boolean hasPermission = false;

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
        Bundle bundle = getArguments();
        if(bundle!=null){
            tripID = bundle.getString("TRIP_STRING_ID");
            tripOwner = bundle.getString("TRIP_OWNER");
        }else{
            Log.d(tag,"NULL");
        }
        //first we add value evenet listener to check if current member has permission to edit or not
        checkUserPermission();
        View root = inflater.inflate(R.layout.fragment_group_trip_detail, container, false);
        tripSectionList = new ArrayList<>();
        parentGroupTripDetailAdapter = new ParentGroupTripDetailAdapter(tripSectionList,this,this);
        rcvGroupTripDetail = root.findViewById(R.id.rcvFragmentGroupTripDetail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvGroupTripDetail.setLayoutManager(layoutManager);
        rcvGroupTripDetail.setAdapter(parentGroupTripDetailAdapter);
        dayList = new ArrayList<>();
        rcvDays = root.findViewById(R.id.rcvFragmentGroupTripDetailToSelectedList);
        rcvDays.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        dayAdapter = new DayAdapter(dayList,this);
        rcvDays.setAdapter(dayAdapter);
        progressBar = root.findViewById(R.id.simpleProgressBar);
        updatingText = root.findViewById(R.id.UpdatingText);
        updatingProgressBar = root.findViewById(R.id.updatingProgressBar);
        registerOnScrollListener();
        fillDateInterval();
        return root;
    }

    //onScroll listener is used to change the color text in daylist adapter based on current scroll position
    private void registerOnScrollListener(){
        rcvGroupTripDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isDayListClicked) {
                    int position = ((LinearLayoutManager) rcvGroupTripDetail.getLayoutManager())
                            .findFirstVisibleItemPosition();
                    for (int i = 0; i < dayList.size(); i++) {
                        if (i != position) {
                            TextView tmp = rcvDays.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tvDayRow);
                            tmp.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                        }
                    }
                    TextView v = rcvDays.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tvDayRow);
                    v.setTextColor(Color.parseColor("#0faaae"));
                }
                isDayListClicked = false;
            }
        });

    }

    //initialize data on the list
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
                if(firstLoad){
                    progressBar.setVisibility(View.GONE);
                    firstLoad = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(firstLoad){
                    progressBar.setVisibility(View.GONE);
                    firstLoad = false;
                }
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

    //we change the text color of daylist adapter when user click on them
    @Override
    public void onListClicked(int position) {
        isDayListClicked = true;
        LinearLayoutManager linear =  (LinearLayoutManager) rcvGroupTripDetail.getLayoutManager();
        assert linear != null;
        linear.scrollToPositionWithOffset(position,0);
        for(int i=0;i<dayList.size();i++){
            if(i!=position){
                TextView tmp = rcvDays.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tvDayRow);
                tmp.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            }
        }
        TextView v = rcvDays.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tvDayRow);
        v.setTextColor(Color.parseColor("#0faaae"));
    }

    // move maps activity when user click on attraction button
    @Override
    public void addNewAttractionClicked(int position) {
        Intent i = new Intent(getContext(),MapsActivity.class);
        i.putExtra("tripStringID",tripID);
        i.putExtra("dateOfTrip",tripSectionList.get(position).getDate());
        i.putExtra("tripOwner",tripOwner);
        i.putExtra("isCurrentUserAMember",true);
        startActivity(i);
    }

    //display timepicker dialog when user click on start time button
    @Override
    public void changeStartTimeClicked(final int position) {
        if(hasPermission) {
            if(!isUpdate) {
                Destination firstDestinationOfTheDate = tripSectionList.get(position).getDestinations().get(0);
                final int hour = Helper.hourToInt(firstDestinationOfTheDate.getStartTime());
                final int min = Helper.minuteToInt(firstDestinationOfTheDate.getStartTime());
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (!(hour == hourOfDay && min == minute)) {
                            resetStartTimeOfCurrentDate(position, hourOfDay, minute);
                            updatingText.setVisibility(View.VISIBLE);
                            updatingText.bringToFront();
                            updatingProgressBar.setVisibility(View.VISIBLE);
                            updatingProgressBar.bringToFront();
                        }
                    }
                }, hour, min, true);
                timePickerDialog.setTitle("Edit Start Time");
                timePickerDialog.show();
            }
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    //remove the attraction from trip detail node
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
                        Toast.makeText(getContext(),"Delete success",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    //remove the attraction from the list and update current date attraction list (extra day,start date,end date)
    @Override
    public void onDeleteDestinationClicked(String date, String destinationStringID, int position) {
        if(hasPermission) {
            if(!isUpdate) {
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
                // if there are two attraction in list, after delete the first one we have to update second one data
                if (destinationsOfCurrentDateReplica.size() != 1 && position != destinationsOfCurrentDateReplica.size() - 1) {
                    isUpdate = true;
                    updatingProgressBar.setVisibility(View.VISIBLE);
                    updatingProgressBar.bringToFront();
                    updatingText.setVisibility(View.VISIBLE);
                    updatingText.bringToFront();
                    updatingDestinationSize = destinationsOfCurrentDateReplica.size();
                    Destination toUpdateDestination = destinationsOfCurrentDateReplica.get(position + 1);
                    toUpdateDestination.setExtraDay(deletedOne.getExtraDay());
                    toUpdateDestination.setDecreased(deletedOne.isDecreased());
                    toUpdateDestination.setIncreased(deletedOne.isIncreased());
                    toUpdateDestination.setStartTime(deletedOne.getStartTime());
                    Helper.changeStayPeriodOfDestination(toUpdateDestination.getStartDate(), toUpdateDestination.getStartTime(), 0, (int) toUpdateDestination.getDuration(), toUpdateDestination);
                    updatingCurrentPosition = position + 1;
                    updateToFirebaseAfterPeriodChanged(toUpdateDestination);
                    updateTheRest = true;
                }
                //in case there are more than 2 attractions in the list we have to update them all
                if (updateTheRest && destinationsOfCurrentDateReplica.size() != 2) {
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
                        updatingCurrentPosition = i;
                        updateToFirebaseAfterPeriodChanged(toUpdateDestination);
                    }
                }
            }
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    public List<Destination> getListOfSpecificDate(String date){
        List<Destination> destinations = new ArrayList<>();
        for(TripSection list:tripSectionList){
            if(list.getDate().equals(date))
                destinations = list.getDestinations();
        }
        return destinations;
    }

    //display timepicker dialog when user click on clock icon
    @Override
    public void onDurationEditingClicked(final int position, final String curDate) {
        if(hasPermission) {
            if(!isUpdate) {
                Destination selectedAttraction = getListOfSpecificDate(curDate).get(position);
                final int hour = Helper.minutesToHour(selectedAttraction.getDuration());
                final int min = Helper.minutesToMinute(selectedAttraction.getDuration());
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (!(hourOfDay == hour && minute == min)) {
                            changeStayPeriodOfDestination(hourOfDay, minute, position, curDate);
                            updatingText.setVisibility(View.VISIBLE);
                            updatingText.bringToFront();
                            updatingProgressBar.setVisibility(View.VISIBLE);
                            updatingProgressBar.bringToFront();
                        }
                    }
                }, hour, min, true);
                timePickerDialog.setTitle("Modify stay period");
                timePickerDialog.show();
            }
        }else{
            Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttractionClicked(String placeID, String placeName, Double lat, Double lng) {
        Intent i = new Intent(getContext(),AttractionDetailActivity.class);
        i.putExtra("PLACEID",placeID);
        i.putExtra("PLACENAME",placeName);
        i.putExtra("PLACELAT",lat);
        i.putExtra("PLACELNG",lng);
        startActivity(i);
    }

    //change stay period of the destination based on user picked time
    private void changeStayPeriodOfDestination(int hour, int minute, int position,String currentDate){
        isUpdate = true;
        List<Destination> destinations = new ArrayList<>();
        for(TripSection list:tripSectionList){
            if(list.getDate().equals(currentDate))
                destinations = list.getDestinations();
        }
        String startTime = destinations.get(position).getStartTime();
        Helper.changeStayPeriodOfDestination(currentDate,startTime,hour,minute,destinations.get(position));
        updatingDestinationSize = destinations.size();
        updatingCurrentPosition = position;
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
            updatingCurrentPosition = i;
            updateToFirebaseAfterPeriodChanged(destinations.get(i));
        }
    }

    //we want to sync the current destination with firebase database after its value has been updated
    private void updateToFirebaseAfterPeriodChanged(Destination destination){
        final int size = updatingDestinationSize-1;
        final int currentPos = updatingCurrentPosition;
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
                if(currentPos == size){
                    isUpdate = false;
                    updatingText.setVisibility(View.GONE);
                    updatingProgressBar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isUpdate = false;
                updatingText.setVisibility(View.GONE);
                updatingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // reset start time of current date trip_detail (update all destination start time , end time, extra day)
    private void resetStartTimeOfCurrentDate(int position,int hourOfDay,int minute){
        isUpdate = true;
        List<Destination> currentDestinationsOfThisDate = tripSectionList.get(position).getDestinations();
        updatingDestinationSize = currentDestinationsOfThisDate.size();
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
                lastDestination.setStartTime(resClock);
                long duration = lastDestination.getDuration();
                Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,lastDestination.getStartTime(),0,(int)duration,lastDestination);
                updatingCurrentPosition = i;
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
                long duration = currentDestinationsOfThisDate.get(i).getDuration();
                Helper.changeStayPeriodOfDestination(currentDateOfTheTrip,startTimeOfTheTrip,0,(int)(duration),currentDestinationsOfThisDate.get(i));
                updatingCurrentPosition = i;
                updateToFirebaseAfterPeriodChanged(currentDestinationsOfThisDate.get(i));
            }
        }
    }
}