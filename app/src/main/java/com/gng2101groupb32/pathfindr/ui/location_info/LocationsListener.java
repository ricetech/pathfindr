package com.gng2101groupb32.pathfindr.ui.location_info;

import com.gng2101groupb32.pathfindr.db.Location;

public interface LocationsListener {
    void onLocationClick(Location location);

    void onLocationDelete(Location location);
}
