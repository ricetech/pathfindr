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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.gng2101groupb32.pathfindr.R;
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
public class NavMainFragment extends Fragment implements BeaconConsumer {
    public static final String TAG = "NavMainFragment";
    private BeaconManager beaconManager;
    private List<PathfindrBeacon> pBeacons = new ArrayList<>();
    private final HashMap<String, Integer> beaconRSSIMap = new HashMap<>();
    private LocationViewModel locViewModel;

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
                beaconRSSIMap.put(uuid, -1000);
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
        locViewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity())
                .get(LocationViewModel.class);
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