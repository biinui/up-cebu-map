package ph.edu.upcebu.upcebumap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ph.edu.upcebu.upcebumap.bean.Category;
import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;
import ph.edu.upcebu.upcebumap.model.Landmark;
import ph.edu.upcebu.upcebumap.util.Constant;
import ph.edu.upcebu.upcebumap.util.SpinnerAdapter;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import static com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, OnMapLongClickListener, OnMapClickListener, ActionMode.Callback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnMarkerClickListener, View.OnClickListener {
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";
    public static List<LatLng> BOUNDARIES;
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
    private View mView;
    private Dialog mDialog;
    private LayoutInflater mInflater;
    private AlertDialog.Builder builder;
    private Spinner dropdown;
    private EditText name;
    private AlertDialog dialog;
    private ArrayList<Category> category;
    private SpinnerAdapter categoryspinner;
    private Category selectedCategory;

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
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, empty);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.list_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                startActivity(i);
            }
        });


        mInflater = (LayoutInflater) getBaseContext().getSystemService(
                LAYOUT_INFLATER_SERVICE);

        mView = mInflater.inflate(R.layout.activity_add_landmark, null);

        if (mView.getParent() != null)
            ((ViewGroup) mView.getParent()).removeView(mView);
        // mDialog = new Dialog(this,0); // context, theme
        dropdown = (Spinner) mView.findViewById(R.id.category_spinner);
        category = mDB.getAllCategory();
        categoryspinner = new SpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, category);
        dropdown.setAdapter(categoryspinner);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                selectedCategory = categoryspinner.getItem(position);
                // Here you can do the action you want to...
                //Toast.makeText(MapsActivity.this, "ID: " + selectedCategory.getId() + "\nName: " + selectedCategory.getCategoryName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });


        builder = new AlertDialog.Builder(MapsActivity.this);

        builder.setView(this.mView);
        builder.setCancelable(false);
        //builder.setMessage("Test for preventing dialog close");
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });
        dialog = builder.create();
        //final AlertDialog dialog = builder.create();


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
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                i.putExtra(DrawerActivity.LAND_TITLE, marker.getTitle());
                startActivity(i);
            }
        });
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
        MarkerOptions marker = new MarkerOptions().position(lm.getLatlng()).title(lm.getTitle());

        mMap.addMarker(marker);
    }

    public int getImageId(String imageName) {
        return this.getApplication().getResources().getIdentifier("drawable/" + imageName, null, this.getApplication().getPackageName());
    }

    private void showMarker(LatLng latlng) {
        mMap.addMarker(new MarkerOptions().position(latlng));
    }

    private void showMarker(Land land) {
        LatLng position = calculateMarkerCoordinate(land.getLatLngs());
//        StringBuilder snippet = new StringBuilder();

//        for (String room : land.getRooms()) {
//            snippet.append(room);
//            snippet.append('\n');
//        }

        String title = land.getTitle();
        if (title.isEmpty()) title = "Unknown";

        MarkerOptions mo = new MarkerOptions().position(position).title(title);
        mo.icon(BitmapDescriptorFactory.fromResource(getImageId(land.getCategory().getIcon())));
//        if (snippet.length() > 0) {
//            snippet.deleteCharAt(snippet.length() - 1);
//            mo.snippet(snippet.toString());
//        }

        mMap.addMarker(mo);
    }

    private void showTemporaryMarker(LatLng latlng) {
        mTemporaryMarker = mMap.addMarker(new MarkerOptions().position(latlng));
    }

    private void showBuildingMarkers() {
//        for (Landmark lm : Landmark.Buildings()) {
//            showMarker(lm);
//        }


        List<Land> list = mDB.getAllLandmark();
        if (list != null && list.size() > 0) {
            for (Land land : list) {
                List<LatLng> bound = land.getLatLngs();
                if (bound != null) {
                    showBoundary(bound);
                    //            showMarker(calculateMarkerCoordinate(land.getLatLngs()));
                    showMarker(land);
                    bound = null;
                }
            }
        }
    }

    private void showActivityAreaMarkers() {
//        for (Landmark lm : Landmark.ActivityAreas()) {
//            showMarker(lm);
//        }
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
        if (boundaries.size() > 0) {
            PolygonOptions po = new PolygonOptions().addAll(boundaries);
            po.strokeColor(Color.LTGRAY);
            po.fillColor(Color.WHITE);
            po.strokeWidth(0.5f);
            mMap.addPolygon(po);
        }
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
            //Toast.makeText(MapsActivity.this, "ID: " + Arrays.toString(mSelectedPoints.toArray()),Toast.LENGTH_SHORT).show();
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
                dialog.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(MapsActivity.this);


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

    private void search(String query) {
        if (searchTask != null) {
            searchTask.isCancelled();
        }

        searchTask = new PopulateSearchResults(query);
        searchTask.execute();
    }

    @Override
    public void onClick(View v) {
        Dialog f = dialog;
        EditText nname = (EditText) f.findViewById(R.id.landmark_name);
        Spinner spinnerCategory = (Spinner) f.findViewById(R.id.category_spinner);
        if (nname == null || nname.getText().toString().equals("")) {
            Toast toast = Toast.makeText(MapsActivity.this, "Landmark name should not be empty", Toast.LENGTH_LONG);
            toast.show();
        } else {
            DBHelper db = new DBHelper(getApplicationContext());

            // Set all in landmark
            String t = nname.getText().toString();
            String category = selectedCategory.getCategoryName();
            double lat = mTemporaryMarker.getPosition().latitude;
            double lng = mTemporaryMarker.getPosition().longitude;
            String latitude = "";
            long lid = db.insertLandmark(t, category.trim(), lat, lng);
            long sid = db.insertShape(lid, "", "", "", 0);
            List<LatLng> list = mSelectedPoints;
            for (LatLng latlng : list) {
                db.insertBoundary(sid, latlng.latitude, latlng.longitude, 0);
                latitude += " " + latlng.latitude;
            }
            nname.setText("");
            spinnerCategory.setSelection(0);
            Toast toast = Toast.makeText(MapsActivity.this, "Information successfully added.", Toast.LENGTH_SHORT);
            //Toast.makeText(MapsActivity.this, "ID: " + Arrays.toString(mSelectedPoints.toArray()),Toast.LENGTH_SHORT).show();
            showBuildingMarkers();
            showActivityAreaMarkers();
            mActionMode.finish();
            dialog.dismiss();
        }
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