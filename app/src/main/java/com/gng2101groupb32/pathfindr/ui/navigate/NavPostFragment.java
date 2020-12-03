package com.gng2101groupb32.pathfindr.ui.navigate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Location;
import com.gng2101groupb32.pathfindr.ui.location_info.LocationViewModel;

public class NavPostFragment extends Fragment {
    public NavPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment NavPostFragment.
     */
    public static NavPostFragment newInstance() {
        NavPostFragment fragment = new NavPostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_post, container, false);

        // Link DB Elements
        LocationViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity())
                .get(LocationViewModel.class);
        Location location = viewModel.getSelected().getValue();

        // Update UI
        TextView tvHeader = view.findViewById(R.id.nav_post_header);
        tvHeader.setText(getString(R.string.nav_post_text_header, location.getName()));

        return view;
    }
}