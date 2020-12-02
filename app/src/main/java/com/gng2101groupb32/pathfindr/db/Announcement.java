package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class Announcement implements FireStoreDoc {
    private static final String COLLECTION_NAME = "announcements";

    private String title;
    private String contents;
    // id is used to store the document id clientside and should not be synced to FireStore.
    @Exclude
    private String id;
    @ServerTimestamp
    private Timestamp timestamp; // Set automatically by FireStore, hence the annotation

    public Announcement() {
    }

    public Announcement(String title, String contents) {
        this.title = title;
        this.contents = contents;
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
                              COLLECTION_NAME, Announcement.class);
    }

    public static void getLiveAnnouncements(EventListener<List<Announcement>> eventListener) {
        DBUtils.getLiveCollection(eventListener, COLLECTION_NAME, Announcement.class);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: " + this.id + ", TITLE: " + this.title + ", CONTENT: " + this.contents +
                ", TIMESTAMP: " + this.timestamp.toString();
    }
}
