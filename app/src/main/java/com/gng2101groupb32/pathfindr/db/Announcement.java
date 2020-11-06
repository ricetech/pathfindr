package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class Announcement implements FireStoreDoc {
    private static final String ANNOUNCEMENTS_COLLECTION_NAME = "announcements";

    private String title;
    private String content;
    private String id;
    @ServerTimestamp
    private Timestamp timestamp; // Set automatically by FireStore, hence the annotation

    public Announcement() {
    }

    public Announcement(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Retrieves all services from Firebase Cloud FireStore and returns them as a List of Service
     * objects through the onSuccessListener.
     * Requires the user to be logged in.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the List back through.
     * @param failureListener - The listener to pass any errors through.
     */
    public static void getAllAnnouncements(Activity currentActivity,
                                           OnSuccessListener<List<Announcement>> successListener,
                                           OnFailureListener failureListener) {
        DBUtils.getCollection(currentActivity, successListener, failureListener,
                ANNOUNCEMENTS_COLLECTION_NAME, Announcement.class);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
