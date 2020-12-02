package com.gng2101groupb32.pathfindr.ui.location_info;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Location;

/**
 * A fragment representing a list of Items.
 */
public class LocationsListFragment extends Fragment implements LocationsListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private LocationViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public LocationsListFragment() {
    }

    public static LocationsListFragment newInstance(int columnCount) {
        LocationsListFragment fragment = new LocationsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Location.getLiveLocations((locations, error) -> recyclerView.setAdapter(
                    new LocationsListRecyclerViewAdapter(locations, this)
            ));
            // Add Dividers
            recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                                                                     DividerItemDecoration.VERTICAL));
        }
        // Init ViewModel
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity())
                .get(LocationViewModel.class);

        return view;
    }

    @Override
    public void onLocationClick(Location location) {
        // Set selected Location
        viewModel.select(location);

        // Navigate to fragment
        NavDirections directions = LocationsListFragmentDirections
                .actionNavigationLocListToLocationViewFragment();
        Navigation.findNavController(requireView()).navigate(directions);
    }

    @Override
    public void onLocationDelete(Location location) {

    }
}