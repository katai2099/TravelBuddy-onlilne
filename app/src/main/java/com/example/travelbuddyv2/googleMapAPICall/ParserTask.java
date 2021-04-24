package com.example.travelbuddyv2.googleMapAPICall;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public class  ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
    GoogleMap map ;
    public ParserTask(GoogleMap map) {
        this.map = map;
    }
    @Override
    protected List<HashMap<String, String>> doInBackground(String... strings) {
        //create json parser class
        JsonParser jsonParser = new JsonParser();
        //initialize hash map list
        List<HashMap<String,String>> mapList = null;
        //initialize json object
        try {
            JSONObject object ;
            if(strings[0]!=null){
               object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            }
            //parse json object
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapList;
    }
    @Override
    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
        map.clear();
        if(hashMaps!=null) {
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                String placeID = hashMapList.get("placeID");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                options.snippet(placeID);
                map.addMarker(options);
            }
        }
    }
}