package com.gng2101groupb32.pathfindr.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to Pathfindr!\n\nThis app is a work in progress.\nPlease " +
                "explore the app using the buttons below.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}