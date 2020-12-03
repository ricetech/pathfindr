package com.gng2101groupb32.pathfindr.ui.announcements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Announcement;
import com.gng2101groupb32.pathfindr.db.DBUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

interface AnnouncementsListener {
    void onAnnouncementClick(Announcement announcement);

    void onAnnouncementDelete(Announcement announcement);
}

/**
 * {@link RecyclerView.Adapter} that can display a {@link Announcement}.
 */
public class AnnouncementsListAdapter extends RecyclerView.Adapter<AnnouncementsListAdapter.ViewHolder> {

    private final List<Announcement> mValues;
    private final AnnouncementsListener listener;

    public AnnouncementsListAdapter(List<Announcement> items,
                                    AnnouncementsListener listener) {
        this.mValues = items;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.fragment_announcement_list_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mValues.sort((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()));
        holder.announcement = mValues.get(position);
        holder.nameTV.setText(mValues.get(position).getTitle());
        holder.timeframeTV.setText(DBUtils.timeSince(mValues.get(position).getTimestamp().toDate()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View view;
        public final TextView nameTV;
        public final TextView timeframeTV;
        private final Button btnOpen;
        private final WeakReference<AnnouncementsListener> listenerRef;
        private Announcement announcement;

        public ViewHolder(View view, AnnouncementsListener listener) {
            super(view);
            this.view = view;
            nameTV = (TextView) view.findViewById(R.id.announcement_name);
            timeframeTV = (TextView) view.findViewById(R.id.announcement_timeframe);

            btnOpen = this.view.findViewById(R.id.announcement_open);
            btnOpen.setOnClickListener(this);

            listenerRef = new WeakReference<>(listener);
        }

        @Override
        public void onClick(View view) {
            listenerRef.get().onAnnouncementClick(announcement);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + timeframeTV.getText() + "'";
        }
    }
}