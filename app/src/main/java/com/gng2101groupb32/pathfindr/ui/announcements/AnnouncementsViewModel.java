package com.gng2101groupb32.pathfindr.ui.announcements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gng2101groupb32.pathfindr.db.Announcement;

public class AnnouncementsViewModel extends ViewModel {
    private final MutableLiveData<Announcement> selectedAnnouncement = new MutableLiveData<>();

    public void select(Announcement announcement) {
        selectedAnnouncement.setValue(announcement);
    }

    public LiveData<Announcement> getSelected() {
        return selectedAnnouncement;
    }
}
