package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Location implements FireStoreDoc {
    public static final String COLLECTION_NAME = "locations";

    @Exclude
    private String id;

    private String name;
    private String description;
    private String website;

    private DocumentReference beacon;

    public Location() {
    }

    public Location(String name, String description, String website, DocumentReference beacon) {
        this.name = name;
        this.description = description;
        this.website = website;
        this.beacon = beacon;
    }

    public static DocumentReference getRef(String id) {
        return DBUtils.getRef(COLLECTION_NAME, id);
    }

    public static void getLocation(
            Activity currentActivity,
            final OnSuccessListener<Location> successListener,
            final OnFailureListener failureListener,
            DocumentReference docRef) {
        DBUtils.getDoc(currentActivity, successListener, failureListener, docRef, Location.class);
    }

    public static void getLocation(
            Activity currentActivity,
            final OnSuccessListener<Location> successListener,
            final OnFailureListener failureListener,
            String id) {
        DocumentReference docRef = getRef(id);
        getLocation(currentActivity, successListener, failureListener, docRef);
    }

    public static void getLiveLocations(EventListener<List<Location>> eventListener) {
        DBUtils.getLiveCollection(eventListener, COLLECTION_NAME, Location.class);
    }

    @Override
    @Exclude
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public DocumentReference getBeacon() {
        return beacon;
    }

    public void setBeacon(DocumentReference beacon) {
        this.beacon = beacon;
    }
}
