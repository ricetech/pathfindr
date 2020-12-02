package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Beacon implements FireStoreDoc {
    public static final String COLLECTION_NAME = "beacons";

    @Exclude
    private String id;

    private String name;
    private String internalName;

    public Beacon() {
    }

    public Beacon(String name, String internalName) {
        this.name = name;
        this.internalName = internalName;
    }

    public static DocumentReference getRef(String id) {
        return DBUtils.getRef(COLLECTION_NAME, id);
    }

    public static void getBeacon(
            Activity currentActivity,
            final OnSuccessListener<Beacon> successListener,
            final OnFailureListener failureListener,
            DocumentReference docRef) {
        DBUtils.getDoc(currentActivity, successListener, failureListener, docRef, Beacon.class);
    }

    public static void getBeacon(
            Activity currentActivity,
            final OnSuccessListener<Beacon> successListener,
            final OnFailureListener failureListener,
            String id) {
        DocumentReference docRef = getRef(id);
        getBeacon(currentActivity, successListener, failureListener, docRef);
    }

    public static void getLiveBeacons(EventListener<List<Beacon>> eventListener) {
        DBUtils.getLiveCollection(eventListener, COLLECTION_NAME, Beacon.class);
    }

    @Exclude
    @Override
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

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

}
