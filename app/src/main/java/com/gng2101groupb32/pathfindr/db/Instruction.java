package com.gng2101groupb32.pathfindr.db;

import com.google.firebase.firestore.DocumentReference;

public class Instruction {
    private DocumentReference beacon;
    private String summary;
    private String verbose;
    private NavIcon navIcon;
    private double angle;

    public Instruction() {

    }

    public Instruction(DocumentReference beacon, String summary, String verbose,
                       NavIcon navIcon, double angle) {
        this.beacon = beacon;
        this.summary = summary;
        this.verbose = verbose;
        this.navIcon = navIcon;
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

    public NavIcon getNavIcon() {
        return navIcon;
    }

    public void setNavIcon(NavIcon navIcon) {
        this.navIcon = navIcon;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
