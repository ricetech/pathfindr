package com.gng2101groupb32.pathfindr.ui.navigate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gng2101groupb32.pathfindr.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavMainFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_main, container, false);
    }
}