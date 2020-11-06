package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Announcement implements FireStoreDoc {
    private static final String ANNOUNCEMENTS_COLLECTION_NAME = "announcements";

    private String title;
    private String body;
    private String id;
    @ServerTimestamp
    private Date timestamp; // Set automatically by FireStore, hence the annotation

    public Announcement() {
    }

    public Announcement(String title, String body) {
        this.title = title;
        this.body = body;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
