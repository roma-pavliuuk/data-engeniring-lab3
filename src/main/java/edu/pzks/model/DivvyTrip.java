package edu.pzks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DivvyTrip {
    @JsonProperty("trip_id")
    private String tripId;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("bikeid")
    private String bikeId;

    @JsonProperty("tripduration")
    private String tripDuration;

    @JsonProperty("from_station_id")
    private String fromStationId;

    @JsonProperty("from_station_name")
    private String fromStationName;

    @JsonProperty("to_station_id")
    private String toStationId;

    @JsonProperty("to_station_name")
    private String toStationName;

    @JsonProperty("usertype")
    private String userType;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("birthyear")
    private String birthYear;


    public DivvyTrip() {
    }

    public static DivvyTrip fromCsvLine(String[] fields) {
        DivvyTrip trip = new DivvyTrip();
        trip.tripId = getField(fields, 0);
        trip.startTime = getField(fields, 1);
        trip.endTime = getField(fields, 2);
        trip.bikeId = getField(fields, 3);
        trip.tripDuration = getField(fields, 4);
        trip.fromStationId = getField(fields, 5);
        trip.fromStationName = getField(fields, 6);
        trip.toStationId = getField(fields, 7);
        trip.toStationName = getField(fields, 8);
        trip.userType = getField(fields, 9);
        trip.gender = getField(fields, 10);
        trip.birthYear = getField(fields, 11);
        return trip;
    }

    private static String getField(String[] fields, int idx) {
        return (fields != null && idx < fields.length) ? fields[idx].trim() : "";
    }


    public String getTripId() {
        return tripId;
    }

    public void setTripId(String v) {
        this.tripId = v;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String v) {
        this.startTime = v;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String v) {
        this.endTime = v;
    }

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String v) {
        this.bikeId = v;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(String v) {
        this.tripDuration = v;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(String v) {
        this.fromStationId = v;
    }

    public String getFromStationName() {
        return fromStationName;
    }

    public void setFromStationName(String v) {
        this.fromStationName = v;
    }

    public String getToStationId() {
        return toStationId;
    }

    public void setToStationId(String v) {
        this.toStationId = v;
    }

    public String getToStationName() {
        return toStationName;
    }

    public void setToStationName(String v) {
        this.toStationName = v;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String v) {
        this.userType = v;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String v) {
        this.gender = v;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String v) {
        this.birthYear = v;
    }

    @Override
    public String toString() {
        return "DivvyTrip{tripId='" + tripId + "', from='" + fromStationName +
                "', to='" + toStationName + "', userType='" + userType + "'}";
    }
}
