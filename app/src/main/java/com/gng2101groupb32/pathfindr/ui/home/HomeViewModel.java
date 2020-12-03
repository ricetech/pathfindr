package com.gng2101groupb32.pathfindr.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to Pathfindr!\n\nPlease " +
                               "explore the app using the buttons below, or by using the navigation bar at the " +
                               "bottom of the screen.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}