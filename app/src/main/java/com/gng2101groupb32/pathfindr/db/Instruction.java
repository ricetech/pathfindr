package com.gng2101groupb32.pathfindr.db;

import com.google.firebase.firestore.DocumentReference;

public class Instruction {
    private DocumentReference beacon;
    private String summary;
    private String verbose;

    public Instruction() {

    }

    public Instruction(DocumentReference beacon, String summary, String verbose) {
        this.beacon = beacon;
        this.summary = summary;
        this.verbose = verbose;
    }

    public DocumentReference getBeacon() {
        return beacon;
    }

    public void setBeacon(DocumentReference beacon) {
        this.beacon = beacon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVerbose() {
        return verbose;
    }

    public void setVerbose(String verbose) {
        this.verbose = verbose;
    }
}
