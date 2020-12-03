package com.gng2101groupb32.pathfindr.db;

import com.google.firebase.firestore.DocumentReference;

public class Instruction {
    private DocumentReference beacon;
    private String summary;
    private String verbose;
    private NavIcon icon;
    private double angle;

    public Instruction() {

    }

    public Instruction(DocumentReference beacon, String summary, String verbose,
                       NavIcon icon, double angle) {
        this.beacon = beacon;
        this.summary = summary;
        this.verbose = verbose;
        this.icon = icon;
        this.angle = angle;
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

    public NavIcon getIcon() {
        return icon;
    }

    public void setIcon(NavIcon icon) {
        this.icon = icon;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
