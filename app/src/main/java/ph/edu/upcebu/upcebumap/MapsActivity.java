package ph.edu.upcebu.upcebumap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;
import ph.edu.upcebu.upcebumap.model.Landmark;
import ph.edu.upcebu.upcebumap.util.Constant;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import static com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, OnMapLongClickListener, OnMapClickListener, ActionMode.Callback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnMarkerClickListener, OnInfoWindowClickListener {
    private GoogleMap mMap;
    private ActionMode mActionMode;
    private Stack<LatLng> mSelectedPoints;
    private Polygon mMutablePolygon;
    private Marker mTemporaryMarker;
    private DBHelper mDB;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private PopulateSearchResults searchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDB = new DBHelper(this);
        createLocationRequest();
        buildGoogleApiClient();

        mSearchView = (SearchView) findViewById(R.id.searchView);

        List<String> empty = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, empty);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                               @Override
                                               public boolean onQueryTextSubmit(String query) {
                                                   search("");
                                                   String title = (String) mListView.getItemAtPosition(0);

                                                   if (title == null) {
                                                       return false;
                                                   }

                                                   Cursor cursor = mDB.getLandmarkLikeTitle(title);
                                                   cursor.moveToFirst();
                                                   double lat = cursor.getDouble(cursor.getColumnIndex(DBHelper.LANDMARK_COLUMN_XPOS));
                                                   double lng = cursor.getDouble(cursor.getColumnIndex(DBHelper.LANDMARK_COLUMN_YPOS));
                                                   if (mMap != null) {
                                                       CameraPosition cameraPosition = new CameraPosition.Builder()
                                                               .target(new LatLng(lat, lng))
                                                               .zoom(19f)
                                                               .build();
                                                       mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                   }
                                                   return false;
                                               }

                                               @Override
                                               public boolean onQueryTextChange(String newText) {
                                                   search(newText);
                                                   return false;
                                               }
                                           }

        );

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search("");
                String title = (String) mListView.getItemAtPosition(position);
                Cursor cursor = mDB.getLandmarkWithTitle(title);
                cursor.moveToFirst();
                double lat = cursor.getDouble(cursor.getColumnIndex(DBHelper.LANDMARK_COLUMN_XPOS));
                double lng = cursor.getDouble(cursor.getColumnIndex(DBHelper.LANDMARK_COLUMN_YPOS));
                if (mMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat, lng))
                            .zoom(19f)
                            .build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
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
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void showUPCebu() {
        mMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
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

    private void showMarker(Land land) {
        LatLng position = calculateMarkerCoordinate(land.getLatLngs());
        StringBuilder snippet = new StringBuilder();

        for (String room : land.getRooms()) {
            snippet.append(room);
            snippet.append('\n');
        }

        String title = land.getTitle();
        if (title.isEmpty()) title = "Unknown";

        MarkerOptions mo = new MarkerOptions().position(position).title(title);

        if (snippet.length() > 0) {
            snippet.deleteCharAt(snippet.length() - 1);
            mo.snippet(snippet.toString());
        }

        mMap.addMarker(mo);
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
//            showMarker(calculateMarkerCoordinate(land.getLatLngs()));
            showMarker(land);
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
        po.fillColor(Color.LTGRAY);
        po.strokeColor(Color.DKGRAY);
        po.strokeWidth(1);
        mMutablePolygon = mMap.addPolygon(po);

        if (mMutablePolygon.getPoints().size() > 0) {
            showTemporaryMarker(calculateMarkerCoordinate(mMutablePolygon.getPoints()));
        }
    }

    private void showBoundary(List<LatLng> boundaries) {
        PolygonOptions po = new PolygonOptions().addAll(boundaries);
        po.strokeColor(Color.LTGRAY);
        po.fillColor(Color.WHITE);
        po.strokeWidth(0.5f);
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
                Intent intent = new Intent(this, AddLandmarkActivity.class);
                intent.putExtra(AddLandmarkActivity.LAT, mTemporaryMarker.getPosition().latitude);
                intent.putExtra(AddLandmarkActivity.LNG, mTemporaryMarker.getPosition().longitude);
                AddLandmarkActivity.BOUNDARIES = mMutablePolygon.getPoints();
                startActivity(intent);
                mActionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mSelectedPoints.removeAllElements();
        mTemporaryMarker.remove();
        mTemporaryMarker = null;
        mMutablePolygon.remove();
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
    protected void onStart() {
        super.onStart();
        if (mMap != null) {
            showUPCebu();
            showBuildingMarkers();
            showActivityAreaMarkers();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateYouAreHereMarker();
    }

    private void updateYouAreHereMarker() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO go to Land directory dialog or activity
    }

    private void search(String query) {
        if (searchTask != null) {
            searchTask.isCancelled();
        }

        searchTask = new PopulateSearchResults(query);
        searchTask.execute();
    }

    private class PopulateSearchResults extends AsyncTask<Void, Void, List<String>> {
        private final String query;

        public PopulateSearchResults(String query) {
            this.query = query;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            if (query.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> lands = new ArrayList<>();
            Cursor cursor = mDB.getAllLandmarksWithTitleLike(query);
            cursor.moveToFirst();
            while (!isCancelled() && !cursor.isAfterLast()) {
                String title = cursor.getString(cursor.getColumnIndex(DBHelper.LANDMARK_COLUMN_TITLE));
                lands.add(title);
                cursor.moveToNext();
            }
            return lands;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            if (isCancelled()) {
                return;
            }

            mAdapter.clear();
            mAdapter.addAll(results);
            mAdapter.notifyDataSetChanged();
        }
    }
}