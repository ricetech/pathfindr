package com.gng2101groupb32.pathfindr.ui.location_info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gng2101groupb32.pathfindr.db.Location;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<Location> selectedLocation = new MutableLiveData<>();

    public void select(Location location) {
        selectedLocation.setValue(location);
    }

    public LiveData<Location> getSelected() {
        return selectedLocation;
    }
}
