package com.gng2101groupb32.pathfindr.ui.navigate;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Location;
import com.gng2101groupb32.pathfindr.db.PathfindrBeacon;
import com.gng2101groupb32.pathfindr.ui.location_info.LocationViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("FieldCanBeLocal")
public class NavMainFragment extends Fragment implements BeaconConsumer {
    public static final int DEFAULT_RSSI = -1000;
    public static final String TAG = "NavMainFragment";

    private BeaconManager beaconManager;
    private List<PathfindrBeacon> pBeacons = new ArrayList<>();
    private final HashMap<String, Integer> beaconRSSIMap = new HashMap<>();
    private LocationViewModel locViewModel;

    // Current Destination
    private Location location;

    // ConstraintLayouts
    private ConstraintLayout layoutLoading; // Loading UI
    private ConstraintLayout layoutNav; // Nav UI

    // Loading UI
    private ProgressBar progressBarLoading;

    // Nav UI
    private ProgressBar progressBarNav;
    private TextView tvSummary;
    private ImageView ivNavIcon;
    private TextView tvDestination;

    private Button btnText;
    private Button btnTTS;

    // Exit Button (Always visible)
    private Button btnExit;

    public NavMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment NavMainFragment.
     */
    public static NavMainFragment newInstance() {
        NavMainFragment fragment = new NavMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Beacon Initialization
        beaconManager = BeaconManager.getInstanceForApplication(requireContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(
                "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        ));
        beaconManager.bind(this);

        // Get Beacons to populate beaconRSSIMap
        OnSuccessListener<List<PathfindrBeacon>> beaconOnSuccessListener = pathfindrBeacons -> {
            pBeacons = pathfindrBeacons;
            for (PathfindrBeacon beacon : pBeacons) {
                String uuid = beacon.getId();
                beaconRSSIMap.put(uuid, DEFAULT_RSSI);
            }
        };
        OnFailureListener beaconOnFailureListener = e -> {
            Toast.makeText(requireContext(), "Unable to fetch beacons. Please try again.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Unable to fetch beacons: ", e);
        };
        PathfindrBeacon.getBeacons(requireActivity(), beaconOnSuccessListener, beaconOnFailureListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_main, container, false);

        // Link DB Elements
        locViewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity())
                .get(LocationViewModel.class);
        location = locViewModel.getSelected().getValue();

        // Link UI elements
        // ConstraintLayouts
        layoutLoading = view.findViewById(R.id.nav_main_loading);
        layoutNav = view.findViewById(R.id.nav_list_nav);

        // Loading UI
        progressBarLoading = view.findViewById(R.id.nav_main_loading_bar);

        // Nav UI
        progressBarNav = view.findViewById(R.id.nav_main_progress_bar);
        tvSummary = view.findViewById(R.id.nav_main_summary);
        ivNavIcon = view.findViewById(R.id.nav_main_icon);
        tvDestination = view.findViewById(R.id.nav_main_destination);

        btnText = view.findViewById(R.id.nav_main_text);
        btnTTS = view.findViewById(R.id.nav_main_tts);

        // Exit Button (Always visible)
        btnExit = view.findViewById(R.id.nav_main_exit);
        btnExit.setOnClickListener(v -> Navigation
                .findNavController(requireView())
                .navigate(NavMainFragmentDirections.actionNavMainFragmentToNavigationNavList()));

        // Set UI elements that can be set right now
        tvDestination.setText(location.getName());
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        beaconRSSIMap.put(beacon.getId1().toString(), beacon.getRssi());
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}