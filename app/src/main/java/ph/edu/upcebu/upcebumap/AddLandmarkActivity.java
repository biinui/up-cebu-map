package ph.edu.upcebu.upcebumap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;

public class AddLandmarkActivity extends AppCompatActivity {
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";
    public static List<LatLng> BOUNDARIES;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_landmark);
        Intent intent = getIntent();
        final DBHelper db = new DBHelper(this);
        int position = -1;
        type = intent.getStringExtra("type");
        final Spinner spinnerCategory = (Spinner) findViewById(R.id.category_spinner);

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.categories)));

//        Button submit = (Button) findViewById(R.id.submit_landmark);
        final EditText title = (EditText) findViewById(R.id.landmark_name);
        ImageView icon = (ImageView) findViewById(R.id.item_category_icon);
        TextView category = (TextView) findViewById(R.id.item_category_name);
        TextView hidden = (TextView) findViewById(R.id.item_category_id_hidden);

        if (type != null && type.equals("edit")) {
            position = Integer.parseInt(intent.getStringExtra("position"));
            getSupportActionBar().setTitle("Edit Landmark");
            ArrayList<Land> alllandmarks = db.getAllLandmark();
            Land landmark = alllandmarks.get(position);
            title.setText(landmark.getTitle());
            icon.setImageResource(getImageId(this, landmark.getCategory().getIcon()));
            category.setText(landmark.getCategory().getCategoryName());
        } else {
            getSupportActionBar().setTitle("Add Landmark");
        }

//        submit.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
////                Land landmark = new Land();
//                DBHelper db = new DBHelper(getApplicationContext());
//                // Set all in landmark
//                String t = title.getText().toString();
//                String category = spinnerCategory.getSelectedItem().toString();
//                double lat = getIntent().getDoubleExtra(LAT, 0);
//                double lng = getIntent().getDoubleExtra(LNG, 0);
//                long lid = db.insertLandmark(t, category, lat, lng);
//                long sid = db.insertShape(lid, "", "", "", 0);
//                for (LatLng latlng : BOUNDARIES) {
//                    db.insertBoundary(sid, latlng.latitude, latlng.longitude, 0);
//                }
//                finish();
//            }
//        });


    }

    public int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
