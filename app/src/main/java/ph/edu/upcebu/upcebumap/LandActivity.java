package ph.edu.upcebu.upcebumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.model.DBHelper;
import ph.edu.upcebu.upcebumap.util.LandmarkAdapter;

public class LandActivity extends AppCompatActivity {
    int selectedPosition = -1;
    ImageView add, edit, delete, search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        DBHelper db = new DBHelper(this);
        final ArrayList<Land> land = db.getAllLandmark();
        add = (ImageView) findViewById(R.id.btn_add_landmark);
        edit = (ImageView) findViewById(R.id.btn_edit_landmark);
        delete = (ImageView) findViewById(R.id.btn_delete_landmark);
        search = (ImageView) findViewById(R.id.btn_search_landmark);
        edit.setEnabled(false);
        edit.setClickable(false);
        delete.setEnabled(false);
        delete.setClickable(false);
        // 1. pass context and data to the custom adapter
        LandmarkAdapter adapter = new LandmarkAdapter(this, land);
        // if extending Activity 2. Get ListView from activity_main.xml
        ListView listView = (ListView) findViewById(R.id.list_landmarks);
        // 3. setListAdapter
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                parent.setSelection(position);
                parent.requestFocus();
                LandActivity.this.selectedPosition = position;
                LandActivity.this.edit.setEnabled(true);
                LandActivity.this.edit.setClickable(true);
                LandActivity.this.delete.setEnabled(true);
                LandActivity.this.delete.setClickable(true);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddLandmarkActivity.class);
                i.putExtra("type", "add");
                startActivity(i);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddLandmarkActivity.class);
                i.putExtra("type", "edit");
                i.putExtra("position", selectedPosition);
                startActivity(i);
            }
        });
    }
}
