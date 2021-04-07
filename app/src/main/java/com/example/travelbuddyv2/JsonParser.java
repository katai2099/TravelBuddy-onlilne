package com.example.travelbuddyv2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    private HashMap<String,String> parseJsonObject(JSONObject jsonObject){
        HashMap<String,String> dataList = new HashMap<>();
        try {
            String placeID = jsonObject.getString("place_id");
            String name = jsonObject.getString("name");
            String latitude = jsonObject.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String longitude = jsonObject.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);
            dataList.put("placeID",placeID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            try{
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return dataList;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;

        try {
            jsonArray = object.getJSONArray("results");
            JsonParser jsonParser = new JsonParser();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonArray != null;
        return parseJsonArray(jsonArray);
    }

}
