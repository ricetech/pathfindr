package com.gng2101groupb32.pathfindr.ui.location_info;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Location;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class LocationsListRecyclerViewAdapter extends RecyclerView.Adapter<LocationsListRecyclerViewAdapter.ViewHolder> {

    private final List<Location> locations;
    private final LocationsListener listener;

    public LocationsListRecyclerViewAdapter(List<Location> locations, LocationsListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.fragment_locations_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Collections.sort(locations, (loc1, loc2) -> loc1.getName().compareTo(loc2.getName()));
        holder.location = locations.get(position);
        holder.nameTV.setText(locations.get(position).getName());
        holder.descriptionTV.setText(locations.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView nameTV;
        public final TextView descriptionTV;

        private final Button btnOpen;
        private final WeakReference<LocationsListener> listenerRef;

        public Location location;

        public ViewHolder(View view, LocationsListener listener) {
            super(view);
            mView = view;
            nameTV = (TextView) view.findViewById(R.id.location_name);
            descriptionTV = (TextView) view.findViewById(R.id.location_description);

            btnOpen = this.mView.findViewById(R.id.location_open);
            btnOpen.setOnClickListener(this);

            listenerRef = new WeakReference<>(listener);
        }

        @Override
        public void onClick(View view) {
            listenerRef.get().onLocationClick(location);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + descriptionTV.getText() + "'";
        }
    }
}