package peaceinfotech.demomap.GetData;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import peaceinfotech.demomap.R;

public class GetNearbyPlacesData extends AsyncTask<Object,String,String> {

    String googlePlacesData,url;
    GoogleMap mMap;
    String click;
    LatLng latLngend;
    Double endlatitude,endlongitude;
    Boolean bclick = false;
    Context context;
    public static final String endlat="endlat";
    public static final String endlog="endlog";
    public static final String clickbool="clickbool";

    public interface AsyncResponse {
        void processFinish(Double endlat,Double endlog,Boolean clikon);
    }

    public AsyncResponse delegate = null;

    public GetNearbyPlacesData (AsyncResponse delegate){
        this.delegate = delegate;
    }



    @Override
    protected String doInBackground(Object... objects) {

        mMap=(GoogleMap)objects[0];
        url=(String)objects[1];
        click=(String)objects[2];
        DownloadUrl downloadUrl=new DownloadUrl();
        try {
            googlePlacesData=downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String,String>> nearbyPlaceList=null;
        DataParser parser=new DataParser();
        nearbyPlaceList=parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);

    }

    private void showNearbyPlaces(List<HashMap<String,String>>nearbyPlaceList){

        for (int i=0;i<nearbyPlaceList.size();i++){

            MarkerOptions markerOptions= new MarkerOptions();
            HashMap<String,String>googlePlace=nearbyPlaceList.get(i);

            String placeName=googlePlace.get("place_name");
            String vicinity=googlePlace.get("vicinity");
            double lat=Double.parseDouble(googlePlace.get("lat"));
            double lng=Double.parseDouble(googlePlace.get("lng"));


            Log.d("SID", "onResponse: " + click);

            if(click.equals("rest")) {
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                markerOptions.snippet(vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        latLngend=marker.getPosition();
                        endlatitude=latLngend.latitude;
                        endlongitude=latLngend.longitude;
                        bclick=true;

                        delegate.processFinish(endlatitude,endlongitude,bclick);
                        //   Log.d("location123", endLatitudeLongitude.getEndLat()+"/"+endLatitudeLongitude.getEndLog()+"/"+endLatitudeLongitude.getClick());
                        return true;
                    }
                });
            }
            else if (click.equals("hosp")){

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                markerOptions.snippet(vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        latLngend=marker.getPosition();
                        endlatitude=latLngend.latitude;
                        endlongitude=latLngend.longitude;
                        bclick=true;
                        delegate.processFinish(endlatitude,endlongitude,bclick);
                        return true;
                    }
                });

            }
            else if(click.equals("school")){

                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName);
                markerOptions.snippet(vicinity);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.graduation));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        latLngend=marker.getPosition();
                        endlatitude=latLngend.latitude;
                        endlongitude=latLngend.longitude;
                        bclick=true;
                        delegate.processFinish(endlatitude,endlongitude,bclick);
                        return true;
                    }
                });
            }
        }
    }
}
