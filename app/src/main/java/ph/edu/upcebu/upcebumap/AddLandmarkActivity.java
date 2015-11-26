package ph.edu.upcebu.upcebumap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;

public class AddLandmarkActivity extends AppCompatActivity {
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_landmark);
        Intent intent = getIntent();
        DBHelper db = new DBHelper(this);
        int position = -1;
        type = intent.getStringExtra("type");
        Button changeCategory = (Button) findViewById(R.id.change_category);
        Button addBoundaries = (Button) findViewById(R.id.add_boundaries);
        Button submit = (Button) findViewById(R.id.submit_landmark);
        EditText title = (EditText) findViewById(R.id.landmark_name);
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
            addBoundaries.setText("Edit Boundaries");
        } else {
            getSupportActionBar().setTitle("Add Landmark");
        }

        changeCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });

        addBoundaries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Land landmark = new Land();

                // Set all in landmark

                DBHelper db = new DBHelper(getApplicationContext());
                if (AddLandmarkActivity.this.type.equals("add")) {
                    db.addLandmark(landmark);
                } else {
                    db.editLandmark(landmark);
                }

            }
        });


    }

    public int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
