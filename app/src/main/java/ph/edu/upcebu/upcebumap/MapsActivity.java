package ph.edu.upcebu.upcebumap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import ph.edu.upcebu.upcebumap.model.Landmark;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showUPCebu();
        showBuildingMarkers();
        showActivityAreaMarkers();
    }

    private void showUPCebu() {
        LatLngBounds UPCebu = new LatLngBounds(new LatLng(10.323931, 123.897503), new LatLng(10.321075, 123.900249));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(UPCebu, 0));
    }

    private void showMarker(Landmark lm) {
        mMap.addMarker(new MarkerOptions().position(lm.getLatlng()).title(lm.getTitle()));
    }

    private void showBuildingMarkers() {
        for (Landmark lm : Landmark.Buildings()) {
            showMarker(lm);
        }
    }

    private void showActivityAreaMarkers() {
        for (Landmark lm : Landmark.ActivityAreas()) {
            showMarker(lm);
        }
    }

}
