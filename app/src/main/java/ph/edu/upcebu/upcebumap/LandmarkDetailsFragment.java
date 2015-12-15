package ph.edu.upcebu.upcebumap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.bean.Office;
import ph.edu.upcebu.upcebumap.model.DBHelper;
import ph.edu.upcebu.upcebumap.util.ExpandListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandmarkDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandmarkDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarkDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Land land;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ExpandListAdapter ExpAdapter;
    private ArrayList<Office> ExpListItems;
    private ExpandableListView ExpandList;
    private AlertDialog.Builder builder;
    private Office selected;

    public LandmarkDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LandmarkDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandmarkDetailsFragment newInstance(Land land) {
        LandmarkDetailsFragment fragment = new LandmarkDetailsFragment();
        fragment.land = land;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_landmark_details, container, false);

        TextView title = (TextView) v.findViewById(R.id.textTitle);
        TextView category = (TextView) v.findViewById(R.id.textCategory);

        title.setText(land.getTitle());
        category.setText(land.getCategory().getCategoryName());

        DBHelper db = new DBHelper(getActivity().getApplicationContext());
        final ArrayList<Office> office = db.getAllOfficeOfBuilding(land.getId());

        ExpandList = (ExpandableListView) v.findViewById(R.id.exp_list);
        ExpListItems = office;
        ExpAdapter = new ExpandListAdapter(getActivity().getApplicationContext(), ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        ExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    ExpandList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        ExpandList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                selected = ExpListItems.get(pos);
                final CharSequence[] items = {"Edit", "Delete"};

                builder = new AlertDialog.Builder(getActivity());
                //builder.setTitle("Options for " + file.getName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        if (item == 0) {
                            if (selected.getType().equals("room")) {
                                Intent i = new Intent(getActivity().getApplicationContext(), AddRoomActivity.class);
                                i.putExtra("position", selected.getId());
                                i.putExtra("building", selected.getBuildName());
                                getActivity().startActivity(i);
                            } else {
                                Intent i = new Intent(getActivity().getApplicationContext(), AddOfficeActivity.class);
                                i.putExtra("position", selected.getId());
                                i.putExtra("building", selected.getBuildName());
                                getActivity().startActivity(i);
                            }
                        } else if (item == 1) {
                            DBHelper db = new DBHelper(getActivity().getApplicationContext());
                            db.deleteItem(DBHelper.ROOM_TABLE_NAME, DBHelper.ROOM_COLUMN_ID, selected.getId());
                            db.close();
                            Toast.makeText(getActivity().getApplicationContext(), "Information successfully deleted", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getActivity().getApplicationContext(), DrawerActivity.class);
                            i.putExtra("item", 3);
                            startActivity(i);

                        }
                    }
                }).show();
                return true;
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
