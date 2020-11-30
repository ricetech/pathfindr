package com.gng2101groupb32.pathfindr.ui.announcements;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gng2101groupb32.pathfindr.R;
import com.gng2101groupb32.pathfindr.db.Announcement;

/**
 * A fragment representing a list of Items.
 */
public class AnnouncementsListFragment extends Fragment implements AnnouncementsListener {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private AnnouncementsViewModel viewModel;
    private RecyclerView announcements;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public AnnouncementsListFragment() {
    }

    // TODO: Customize parameter initialization
    public static AnnouncementsListFragment newInstance(int columnCount) {
        AnnouncementsListFragment fragment = new AnnouncementsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Announcement.getLiveAnnouncements((announcements, error) -> recyclerView.setAdapter(
                    new AnnouncementsListAdapter(announcements, this)));
            // Add Dividers
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                                                                     DividerItemDecoration.VERTICAL));
        }

        // Init ViewModel
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity())
                .get(AnnouncementsViewModel.class);

        return view;
    }

    @Override
    public void onAnnouncementClick(Announcement announcement) {
        // Set selected announcement
        viewModel.select(announcement);

        // Go to fragment
        NavDirections directions = AnnouncementsListFragmentDirections
                .actionNavigationAnnouncementsToAnnouncementViewFragment();
        Navigation.findNavController(requireView()).navigate(directions);
    }

    @Override
    public void onAnnouncementDelete(Announcement announcement) {

    }
}