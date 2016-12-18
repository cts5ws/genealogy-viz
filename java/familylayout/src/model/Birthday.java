package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Stores birthday information for an individual
 */
public class Birthday {

    //date of birth
    private @JsonProperty("DATE") String date;

    //location of birth
    private @JsonProperty("PLAC") String place;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
