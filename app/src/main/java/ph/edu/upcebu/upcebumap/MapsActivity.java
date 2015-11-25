package ph.edu.upcebu.upcebumap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;
import java.util.Stack;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;
import ph.edu.upcebu.upcebumap.model.Landmark;
import ph.edu.upcebu.upcebumap.util.Constant;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, ActionMode.Callback {
    private GoogleMap mMap;
    private ActionMode mActionMode;
    private Stack<LatLng> mSelectedPoints;
    private Polygon mMutablePolygon;
    private Marker mTemporaryMarker;
    private DBHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDB = new DBHelper(this);
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
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    private void showUPCebu() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraPosition position = new CameraPosition.Builder()
                        .target(Constant.UP_CEBU_POSITION)
                        .zoom(17.5f)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
//                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
    }

    private void showMarker(Landmark lm) {
        mMap.addMarker(new MarkerOptions().position(lm.getLatlng()).title(lm.getTitle()));
    }

    private void showMarker(LatLng latlng) {
        mMap.addMarker(new MarkerOptions().position(latlng));
    }

    private void showTemporaryMarker(LatLng latlng) {
        mTemporaryMarker = mMap.addMarker(new MarkerOptions().position(latlng));
    }

    private void showBuildingMarkers() {
        for (Landmark lm : Landmark.Buildings()) {
            showMarker(lm);
        }

        for (Land land : mDB.getAllLandmark()) {
            showBoundary(land.getLatLngs());
            showMarker(calculateMarkerCoordinate(land.getLatLngs()));
        }
    }

    private void showActivityAreaMarkers() {
        for (Landmark lm : Landmark.ActivityAreas()) {
            showMarker(lm);
        }
    }

    private void showTemporaryBoundary(List<LatLng> boundaries) {
        if (mMutablePolygon != null) {
            mMutablePolygon.remove();
        }

        if (mTemporaryMarker != null) {
            mTemporaryMarker.remove();
        }

        PolygonOptions po = new PolygonOptions().addAll(boundaries);
        po.strokeWidth(1);
        po.fillColor(Color.GRAY);
        po.strokeColor(Color.DKGRAY);
        mMutablePolygon = mMap.addPolygon(po);

        if (mMutablePolygon.getPoints().size() > 3) {
            showTemporaryMarker(calculateMarkerCoordinate(mMutablePolygon.getPoints()));
        }
    }

    private void showBoundary(List<LatLng> boundaries) {
        PolygonOptions po = new PolygonOptions().addAll(boundaries);
        mMap.addPolygon(po);
    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mActionMode != null) {
            return;
        }

        mSelectedPoints = new Stack<LatLng>();
        mSelectedPoints.push(latLng);
        showTemporaryBoundary(mSelectedPoints);
        startActionMode(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mActionMode != null) {
            mSelectedPoints.push(latLng);
            showTemporaryBoundary(mSelectedPoints);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.context_menu_maps, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.contextMenuMapsUndo:
                if (mSelectedPoints.empty()) {
                    return true;
                }
                mSelectedPoints.pop();
                showTemporaryBoundary(mSelectedPoints);
                return true;
            case R.id.contextMenuMapsSave:
                long lid = mDB.insertLandmark("", "", mTemporaryMarker.getPosition().latitude, mTemporaryMarker.getPosition().longitude);
                long sid = mDB.insertShape(lid, "", "", "", 0);
                for (LatLng latlng : mSelectedPoints) {
                    mDB.insertBoundary(sid, latlng.latitude, latlng.longitude, 0);
                }
                showBuildingMarkers();
                mActionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mSelectedPoints.removeAllElements();
        mTemporaryMarker = null;
        mMutablePolygon = null;
        mActionMode = null;
    }

    private LatLng calculateMarkerCoordinate(List<LatLng> coordinates) {
        double x = 0;
        double y = 0;
        for (LatLng latlng : coordinates) {
            x += latlng.latitude;
            y += latlng.longitude;
        }
        double len = coordinates.size();
        return new LatLng(x / len, y / len);
    }

    private void showSaveBoundaryDialog() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mDB.close();
    }
}
