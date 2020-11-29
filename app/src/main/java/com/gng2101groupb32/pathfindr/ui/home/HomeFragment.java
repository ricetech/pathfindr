package com.gng2101groupb32.pathfindr.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.gng2101groupb32.pathfindr.R;

@SuppressWarnings("FieldCanBeLocal")
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button btnAnnouncements;
    private Button btnNavigate;
    private Button btnLocInfo;
    private Button btnHelp;
    private Button btnSettings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        btnAnnouncements = root.findViewById(R.id.home_button_announcements);
        btnNavigate = root.findViewById(R.id.home_button_navigate);
        btnLocInfo = root.findViewById(R.id.home_button_loc_info);
        btnHelp = root.findViewById(R.id.home_button_help);
        btnSettings = root.findViewById(R.id.home_button_settings);

        btnAnnouncements.setOnClickListener(view -> {
            NavDirections directions = HomeFragmentDirections
                    .actionNavigationHomeToNavigationAnnouncements();
            Navigation.findNavController(root).navigate(directions);
        });

        btnNavigate.setOnClickListener(view -> {
            NavDirections directions = HomeFragmentDirections
                    .actionNavigationHomeToNavigationNavList();
            Navigation.findNavController(root).navigate(directions);
        });

        btnLocInfo.setOnClickListener(view -> {
            NavDirections directions = HomeFragmentDirections
                    .actionNavigationHomeToNavigationLocList();
            Navigation.findNavController(root).navigate(directions);
        });

        btnHelp.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Error: This feature is not implemented yet!",
                           Toast.LENGTH_LONG).show();
        });

        btnSettings.setOnClickListener(view -> {
            NavDirections directions = HomeFragmentDirections
                    .actionNavigationHomeToNavigationSettings();
            Navigation.findNavController(root).navigate(directions);
        });

        return root;
    }
}