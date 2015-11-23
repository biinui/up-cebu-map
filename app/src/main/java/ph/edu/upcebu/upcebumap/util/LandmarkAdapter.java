package ph.edu.upcebu.upcebumap.util;

/**
 * Created by user on 11/22/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ph.edu.upcebu.upcebumap.R;
import ph.edu.upcebu.upcebumap.bean.Land;

public class LandmarkAdapter extends ArrayAdapter<Land> {

    private final Context context;
    private final ArrayList<Land> modelsArrayList;

    public LandmarkAdapter(Context context, ArrayList<Land> modelsArrayList) {
        super(context, R.layout.item_landmark, modelsArrayList);
        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        rowView = inflater.inflate(R.layout.item_landmark, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
        TextView titleView = (TextView) rowView.findViewById(R.id.item_name);
        TextView categoryView = (TextView) rowView.findViewById(R.id.item_category);
        TextView idHidden = (TextView) rowView.findViewById(R.id.item_landmark_id_hidden);

        // 4. Set the text for textView
        imgView.setImageResource(this.getImageId(modelsArrayList.get(position).getCategory().getIcon()));
        titleView.setText(modelsArrayList.get(position).getTitle());
        categoryView.setText(modelsArrayList.get(position).getCategory().getCategoryName());
        idHidden.setText(modelsArrayList.get(position).getId());

        // 5. retrn rowView
        return rowView;
    }

    public int getImageId(String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
