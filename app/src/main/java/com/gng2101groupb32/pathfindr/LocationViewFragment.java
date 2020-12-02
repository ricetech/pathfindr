package com.gng2101groupb32.pathfindr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gng2101groupb32.pathfindr.db.Location;
import com.gng2101groupb32.pathfindr.ui.location_info.LocationViewModel;

@SuppressWarnings("FieldCanBeLocal")
public class LocationViewFragment extends Fragment {
    private TextView locNameTV;
    private TextView locWebsiteTV;
    private TextView locDescriptionTV;

    private Location location;

    private LocationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater
                .inflate(R.layout.fragment_location_view, container, false);

        this.locNameTV = view.findViewById(R.id.location_view_name);
        this.locWebsiteTV = view.findViewById(R.id.location_view_website);
        this.locDescriptionTV = view.findViewById(R.id.location_view_description);

        this.viewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        // UI Update

        this.location = this.viewModel.getSelected().getValue();

        this.locNameTV.setText(this.location.getName());
        this.locWebsiteTV.setText(this.location.getWebsite());
        this.locDescriptionTV.setText(this.location.getDescription());

        return view;
    }
}