package com.gng2101groupb32.pathfindr.ui.navigate;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Instruction;
import com.gng2101groupb32.pathfindr.db.Location;
import com.gng2101groupb32.pathfindr.db.Path;
import com.gng2101groupb32.pathfindr.ui.location_info.LocationViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("FieldCanBeLocal")
public class NavMainFragment extends Fragment implements BeaconConsumer {
    public static final int DEFAULT_RSSI = -1000;
    public static final int RSSI_THRESHOLD = -80;
    public static final String TAG = "NavMainFragment";

    private final HashMap<String, Integer> beaconRSSIMap = new HashMap<>();
    private BeaconManager beaconManager;
    private LocationViewModel locViewModel;

    // Current Destination
    private Location location;

    // Current Path
    private Path path;
    private int numInstructions;
    private Instruction currentInstruction;

    // Closest Beacon
    private String closestBeaconId;

    // ConstraintLayouts
    private ConstraintLayout layoutLoading; // Loading UI
    private ConstraintLayout layoutNav; // Nav UI

    // Loading UI
    private ProgressBar progressBarLoading;
    private TextView tvLoading;

    // Nav UI
    private ProgressBar progressBarNav;
    private TextView tvSummary;
    private ImageView ivNavIcon;
    private TextView tvDestination;

    private Button btnText;
    private Button btnTTS;

    // Exit Button (Always visible)
    private Button btnExit;

    // TTS
    private TextToSpeech tts;

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

    private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> Objects.equals(entry.getValue(), value))
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toSet());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(requireContext(), status -> {
            tts.setLanguage(Locale.US);
        });
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
        tvLoading = view.findViewById(R.id.nav_main_loading_text);

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

        // Beacon Initialization
        beaconManager = BeaconManager.getInstanceForApplication(requireContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(
                "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        ));
        beaconManager.bind(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Decide Path based on selected location
        // TODO: Remove Hard-coding
        String pathId;
        switch (this.location.getId()) {
            case "Kjl1QGi6EaKc4sqMynjM":
                // Main Entrance to Service Desk
                pathId = "RZDzndY4TodfnwBuN5GB";
                break;
            case "2DiNoX0HZ2pCtfo8rq9R":
                // Service Desk to Main Entrance
                pathId = "n6zXBVAL1L4Ud6a7L1XH";
                break;
            case "iLsh1otAB9GkDnD5PKm4":
                // Test Location A to Test Location B
                pathId = "q3YEdK3uqjb4LX1m9PT0";
                break;
            case "8P8rSjAsVuDOLxlQM4rM":
                // Test Location B to Test Location A
                pathId = "EI6YIC31JnqtJ5PVTWfK";
                break;
            default:
                pathId = "iLsh1otAB9GkDnD5PKm4";
                Toast.makeText(requireContext(), "Destination is invalid. Please try again.",
                               Toast.LENGTH_LONG).show();
                Log.e(TAG, "Invalid Destination: " + this.location.getId());
        }

        // Get Path
        OnSuccessListener<Path> pathOnSuccessListener = path -> {
            // Define actions after Path is collected
            this.path = path;
            this.numInstructions = this.path.getInstructions().size();
            progressBarNav.setMax(numInstructions);
            // Create HashMap of valid beacons and their RSSIs
            for (Instruction i : this.path.getInstructions()) {
                beaconRSSIMap.put(i.getBeacon().getId(), DEFAULT_RSSI);
            }
            // Assume the start beacon is included in the instructions - don't add it
            OnSuccessListener<Location> startLocSL = startLoc -> {
                closestBeaconId = startLoc.getBeacon().getId();
                // Update loading text
                tvLoading.setText(R.string.finding_beacon);
            };
            // Add End Beacon to Map
            OnSuccessListener<Location> endLocSL = endLoc ->
                    beaconRSSIMap.put(endLoc.getBeacon().getId(), DEFAULT_RSSI);
            OnFailureListener locFailureListener = e -> {
                Toast.makeText(requireContext(), "Error getting the Beacon. Please try again.",
                               Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error getting Beacon: ", e);
            };
            Location.getLocation(requireActivity(), endLocSL,
                                 locFailureListener, this.path.getEnd());
            Location.getLocation(requireActivity(), startLocSL,
                                 locFailureListener, this.path.getStart());

        };
        OnFailureListener pathFailureListener = e -> {
            Toast.makeText(requireContext(), "Error getting the Path. Please try again.",
                           Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error getting Path: ", e);
        };
        Path.getPath(requireActivity(), pathOnSuccessListener, pathFailureListener, pathId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        tts.shutdown();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier((beacons, region) -> {
            if (beacons.size() > 0) {
                for (Beacon beacon : beacons) {
                    beaconRSSIMap.put(beacon.getId1().toString(), beacon.getRssi());
                }
            }
            findClosestBeacon();
            if (!this.closestBeaconId.equals(this.location.getBeacon().getId())) {
                layoutLoading.setVisibility(View.GONE);
                Log.i(TAG, closestBeaconId);
                Instruction previousInstruction = currentInstruction;
                currentInstruction = path.getInstructions().stream().filter(instruction -> closestBeaconId.equals(instruction.getBeacon().getId())).findFirst().orElse(null);
                if (currentInstruction != null) {
                    // Advance Progress Bar
                    int numCurrentIns = Math.abs(path.getInstructions().indexOf(currentInstruction));
                    ObjectAnimator.ofInt(progressBarNav, "progress", numCurrentIns)
                                  .setDuration(300).start();
                    // Update TextViews
                    tvSummary.setText(currentInstruction.getSummary());
                    // Update Button OnClickListeners
                    btnText.setOnClickListener(view -> {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle(currentInstruction.getSummary());
                        builder.setMessage(currentInstruction.getVerbose());
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                    });
                    btnTTS.setOnClickListener(view -> readTTS());

                    // Update Icon
                    @DrawableRes int imgDrawable = R.drawable.ic_baseline_navigation_24;
                    double rotation = 0;
                    switch (currentInstruction.getIcon()) {
                        case STRAIGHT:
                            imgDrawable = R.drawable.ic_baseline_arrow_upward_24;
                            rotation = currentInstruction.getAngle();
                            break;
                        case T_LEFT:
                            imgDrawable = R.drawable.ic_baseline_turn_left_rot_180_24;
                            rotation = 180;
                            break;
                        case T_RIGHT:
                            imgDrawable = R.drawable.ic_baseline_turn_right_rot_180_24;
                            rotation = 180;
                            break;
                    }
                    ivNavIcon.setImageResource(imgDrawable);
                    ivNavIcon.setRotation((float) rotation);

                    // Notify User
                    if (currentInstruction != previousInstruction) {
                        readTTS();
                    }
                }
            } else {
                Log.i(TAG, "Done");
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

    private void findClosestBeacon() {
        int maxRSSI = Collections.max(beaconRSSIMap.values());
        if (maxRSSI >= RSSI_THRESHOLD) {
            Set<String> closestBeacons = getKeysByValue(beaconRSSIMap, maxRSSI);
            if (closestBeacons.size() == 1) {
                closestBeaconId = closestBeacons.iterator().next();
            }
            // Do nothing if:
            // All beacons are out of range
            // Multiple beacons are equally as close
            // No beacons are found
        }
    }

    private void readTTS() {
        tts.speak(currentInstruction.getVerbose(), TextToSpeech.QUEUE_FLUSH, null, null);
    }
}