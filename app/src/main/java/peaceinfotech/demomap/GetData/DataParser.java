package peaceinfotech.demomap.GetData;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson){

        HashMap<String,String> googlePlacesMap=new HashMap<>();
        String placeName="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";

        try{
            if(!googlePlaceJson.isNull("name")){
                placeName=googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity")){
                vicinity=googlePlaceJson.getString("vicinity");
            }
            latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlacesMap.put("place_name",placeName);
            googlePlacesMap.put("vicinity",vicinity);
            googlePlacesMap.put("lat",latitude);
            googlePlacesMap.put("lng",longitude);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return googlePlacesMap;
    }


    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
    {
        int count=jsonArray.length();
        List<HashMap<String,String>> placeList=new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for(int i=0;i<count;i++){
            try{
                placeMap=getPlace((JSONObject)jsonArray.get(i));
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;

    }

    public List<HashMap<String,String>> parse(String jsonData){
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    private HashMap<String,String> getDuration(JSONObject googleDirectionsJson){

        HashMap<String,String> googleDirectionMap= new HashMap<>();
        String duration="";
        String distance="";

        try {
            duration=googleDirectionsJson.getJSONObject("duration").getString("text");
            distance=googleDirectionsJson.getJSONObject("distance").getString("text");

//            Log.d("location", distance+","+duration);

            googleDirectionMap.put("duration",duration);
            googleDirectionMap.put("distance",distance);


        } catch (JSONException e) {

        }

        return googleDirectionMap;

    }


    public HashMap<String,String> parseDuration(String jsonData){

        JSONObject jsonobj = null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsonData);
            jsonobj=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDuration(jsonobj);

    }

    public String getPath(JSONObject gpathjson){

        String polyline = null;
        try {
            polyline=gpathjson.getJSONObject("polyline") .getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polyline;
    }

    public String[] getPaths(JSONArray gstepjson){
        int count=gstepjson.length();
        String[] polylines=new String[count];

        for(int i=0;i<count;i++){
            try {
                polylines[i]=getPath(gstepjson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }


    public String[] parseDirection(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;

        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }
}
