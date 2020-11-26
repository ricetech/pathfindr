package com.gng2101groupb32.pathfindr.ui.announcements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Announcement;
import com.gng2101groupb32.pathfindr.db.DBUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Announcement}.
 */
public class AnnouncementsListAdapter extends RecyclerView.Adapter<AnnouncementsListAdapter.ViewHolder> {

    private final List<Announcement> mValues;

    public AnnouncementsListAdapter(List<Announcement> items) {
        mValues = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_announcement_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Collections.sort(mValues, (a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()));
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mTimeframeView.setText(DBUtils.timeSince(mValues.get(position).getTimestamp().toDate()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTimeframeView;
        public Announcement mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.announcement_name);
            mTimeframeView = (TextView) view.findViewById(R.id.announcement_timeframe);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mTimeframeView.getText() + "'";
        }
    }
}