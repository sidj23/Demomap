package peaceinfotech.demomap.GetData;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peaceinfotech.demomap.R;


public class GetDirectionsData extends AsyncTask<Object,String,String> {

    String googleDirectionsData,url,duration,distance,click;
    GoogleMap mMap;
    LatLng latLng;
    public static final String END_LAT="endlat";
    public static final String END_LOG="endlog";
    private List<Polyline> polylineData=new ArrayList<>();


    public GetDirectionsData(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];
        latLng = (LatLng)objects[2];
        click=(String)objects[3];

        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googleDirectionsData=downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String,String> durationList = null;
        DataParser parser= new DataParser();
        durationList=parser.parseDuration(s);
        duration = durationList.get("duration");
        distance = durationList.get("distance");

//        Log.d("location", distance+","+duration);
        MarkerOptions markerOptions= new MarkerOptions();

        if(click.equals("rest")) {
            markerOptions.position(latLng);
            markerOptions.title("Duration: " + duration);
            markerOptions.snippet("Distance: " + distance);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
            mMap.addMarker(markerOptions).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        else if(click.equals("hosp")){
            markerOptions.position(latLng);
            markerOptions.title("Duration: " + duration);
            markerOptions.snippet("Distance: " + distance);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital));
            mMap.addMarker(markerOptions).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        else if(click.equals("school")){
            markerOptions.position(latLng);
            markerOptions.title("Duration: " + duration);
            markerOptions.snippet("Distance: " + distance);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.graduation));
            mMap.addMarker(markerOptions).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

        String[] directionlist=null;
        directionlist=parser.parseDirection(s);
        polyline(directionlist);
    }

    private void displayDirection(String[] directionlist) {

        PolylineOptions options=new PolylineOptions();
        Polyline polyline;

        int count=directionlist.length;


        for(int i=0;i<count;i++){

            options.addAll(PolyUtil.decode(directionlist[i]));

        }

        polyline=mMap.addPolyline(options);
        polyline.setColor(Color.RED);
        polyline.setWidth(10);


    }

    public void polyline(String [] directionlist){
        if (polylineData.size() > 0) {
            for (Polyline poly : polylineData) {
                poly.getPoints().clear();
            }
            polylineData=new ArrayList<>();
        }
        polylineData = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < directionlist.length; i++) {
            //In case of more than 5 alternative routes
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.RED);
            polyOptions.width(10);
            PolylineOptions options;
            options = polyOptions.addAll(PolyUtil.decode(directionlist[i]));
            Polyline polyline = mMap.addPolyline(options);
            polylineData.add(polyline);
        }
    }

}
