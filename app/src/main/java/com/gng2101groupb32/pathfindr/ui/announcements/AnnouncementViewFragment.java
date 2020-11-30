package com.gng2101groupb32.pathfindr.ui.announcements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Announcement;
import com.gng2101groupb32.pathfindr.db.DBUtils;

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class AnnouncementViewFragment extends Fragment {
    private TextView annNameTV;
    private TextView annTimestampTV;
    private TextView annContentsTV;

    private Announcement announcement;

    private AnnouncementsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_announcement_view, container, false);

        this.annNameTV = view.findViewById(R.id.announcement_view_title);
        this.annTimestampTV = view.findViewById(R.id.announcement_view_timestamp);
        this.annContentsTV = view.findViewById(R.id.announcement_view_contents);

        this.viewModel = new ViewModelProvider(requireActivity()).get(AnnouncementsViewModel.class);

        // UI Update
        this.announcement = this.viewModel.getSelected().getValue();

        this.annNameTV.setText(this.announcement.getTitle());
        this.annTimestampTV.setText(DBUtils.timeSince(this.announcement.getTimestamp().toDate()));
        this.annContentsTV.setText(this.announcement.getContents());

        return view;
    }
}