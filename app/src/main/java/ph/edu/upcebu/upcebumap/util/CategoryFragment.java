package ph.edu.upcebu.upcebumap.util;

/**
 * Created by user on 11/22/2015.
 */

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ph.edu.upcebu.upcebumap.R;
import ph.edu.upcebu.upcebumap.bean.Category;
import ph.edu.upcebu.upcebumap.interfaces.DialogClickListener;
import ph.edu.upcebu.upcebumap.model.DBHelper;

public class CategoryFragment extends DialogFragment implements
        OnItemClickListener {
    ListView mylist;
    private DialogClickListener callback;
    private ArrayList<Category> modelsArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview_category, null, false);
        mylist = (ListView) view.findViewById(R.id.list_category);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        DBHelper db = new DBHelper(getActivity());
        modelsArrayList = db.getAllCategory();
        super.onActivityCreated(savedInstanceState);
        CategoryAdapter cadapter = new CategoryAdapter(getActivity(), this.modelsArrayList);
        mylist.setAdapter(cadapter);
        mylist.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        callback.onItemClicked(this.modelsArrayList.get(position));
        dismiss();
        Toast.makeText(getActivity(), this.modelsArrayList.get(position).getCategoryName(), Toast.LENGTH_SHORT)
                .show();
    }
}

