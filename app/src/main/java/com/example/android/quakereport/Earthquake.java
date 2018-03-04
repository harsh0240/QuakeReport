package com.example.android.quakereport;

/**
 * Created by harsh24 on 27/2/18.
 */

public class Earthquake {

    private String eqMag;
    private String eqLoc;
    private String eqTime;

    public Earthquake(String Mag, String Loc, String Time){
        eqMag = Mag;
        eqLoc = Loc;
        eqTime = Time;
    }

    public void setEqMag(String eqMag){
        this.eqMag = eqMag;
    }

    public String getEqMag() {
        return eqMag;
    }

    public void setEqLoc(String eqLoc) {
        this.eqLoc = eqLoc;
    }

    public String getEqLoc() {
        return eqLoc;
    }

    public void setEqTime(String eqTime) {
        this.eqTime = eqTime;
    }

    public String getEqTime() {
        return eqTime;
    }
}
