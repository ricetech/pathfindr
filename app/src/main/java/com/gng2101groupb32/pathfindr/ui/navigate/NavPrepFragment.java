package com.gng2101groupb32.pathfindr.ui.navigate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gng2101groupb32.pathfindr.R;

import org.jetbrains.annotations.NotNull;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class NavPrepFragment extends Fragment {
    public static final String TAG = "NavPrepFragment";
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnNext;

    public NavPrepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment.
     *
     * @return A new instance of fragment NavPrepFragment.
     */
    public static NavPrepFragment newInstance() {
        NavPrepFragment fragment = new NavPrepFragment();
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
        View view = inflater.inflate(R.layout.fragment_nav_prep, container, false);

        btnNext = view.findViewById(R.id.nav_prep_next);
        btnNext.setOnClickListener(v -> {
            // Check for location permissions
            if (ContextCompat
                    .checkSelfPermission(requireContext(),
                                         Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue
                Log.d("NPF", "onCreateView: Perms OK");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
                alertDialogBuilder.setTitle("This app requires access to your location");
                alertDialogBuilder.setMessage(
                        "In order to detect the beacons that this app uses to provide you with " +
                                "navigation directions this app requires access to your location." +
                                "Please grant location permissions in order to use the navigation " +
                                "functionality. If you'd like to grant permissions, please select" +
                                "'OK'. Otherwise, tap 'Cancel'. Note that if you cancel, you will " +
                                "be unable to access any app features which require your location.");
                alertDialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    switch (which) {
                        case BUTTON_NEGATIVE:
                            Navigation.findNavController(requireView()).navigateUp();
                        case BUTTON_POSITIVE:
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                               PERMISSION_REQUEST_FINE_LOCATION);
                    }
                });
                alertDialogBuilder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    switch (which) {
                        case BUTTON_NEGATIVE:
                            Navigation.findNavController(requireView()).navigateUp();
                        case BUTTON_POSITIVE:
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                               PERMISSION_REQUEST_FINE_LOCATION);
                    }
                });
                alertDialogBuilder.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                   PERMISSION_REQUEST_FINE_LOCATION);
            }
        });

        return view;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Fine location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Functionality limited");
                    builder.setMessage(
                            "Since location access has not been granted, this app will not be able to " +
                                    "discover beacons. In order to navigate in this app, you must" +
                                    "grant the app location access through system settings." +
                                    "Otherwise, you will be restricted to using the functions of this " +
                                    "app that do not require location access.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> Navigation.findNavController(requireView()).navigateUp());
                    builder.show();
                }
            }
        }
    }
}