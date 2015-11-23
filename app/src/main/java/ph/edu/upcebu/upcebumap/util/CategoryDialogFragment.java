package ph.edu.upcebu.upcebumap.util;

import android.app.ListFragment;

import ph.edu.upcebu.upcebumap.bean.Category;
import ph.edu.upcebu.upcebumap.interfaces.DialogClickListener;

/**
 * Created by user on 11/22/2015.
 */


public class CategoryDialogFragment extends ListFragment implements DialogClickListener {
    private void showDialog() {
        CategoryFragment dialog = new CategoryFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onItemClicked(Category category) {
        // do something
    }
}
