package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.Query;

import java.util.List;

public class Path implements FireStoreDoc {
    public static final String COLLECTION_NAME = "paths";

    @Exclude
    private String id;

    private String name;
    private String description;

    private DocumentReference start;
    private DocumentReference end;

    private List<Instruction> instructions;

    public Path() {

    }

    public Path(String name, String description, DocumentReference start,
                DocumentReference end, List<Instruction> instructions) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.instructions = instructions;
    }

    public static DocumentReference getRef(String id) {
        return DBUtils.getRef(COLLECTION_NAME, id);
    }

    public static void getPath(
            Activity currentActivity,
            final OnSuccessListener<Path> successListener,
            final OnFailureListener failureListener,
            DocumentReference docRef) {
        DBUtils.getDoc(currentActivity, successListener, failureListener, docRef, Path.class);
    }

    public static void getPath(
            Activity currentActivity,
            final OnSuccessListener<Path> successListener,
            final OnFailureListener failureListener,
            String id) {
        DocumentReference docRef = getRef(id);
        getPath(currentActivity, successListener, failureListener, docRef);
    }

    public static void findPaths(
            EventListener<List<Path>> eventListener,
            DocumentReference start,
            DocumentReference end) {
        Query query = DBUtils.getCollectionRef(COLLECTION_NAME).whereEqualTo("start", start)
                             .whereEqualTo("end", end);
        DBUtils.queryLive(eventListener, query, Path.class);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getStart() {
        return start;
    }

    public void setStart(DocumentReference start) {
        this.start = start;
    }

    public DocumentReference getEnd() {
        return end;
    }

    public void setEnd(DocumentReference end) {
        this.end = end;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }
}
