package com.example.maask.tourmanagementsystem.EventFile;

/**
 * Created by Maask on 2/4/2018.
 */

public class UserEventInfo {

    private String parentName;
    private String eventCreateData;
    private String eventName;
    private String eventStartLocation;
    private String eventDestination;
    private String departureDate;
    private String eventBudget;
    private String eventExpense;

    public UserEventInfo() {}

    public UserEventInfo(String parentName, String eventCreateData, String eventName, String eventStartLocation, String eventDestination, String departureDate, String eventBudget, String eventExpense) {
        this.parentName = parentName;
        this.eventCreateData = eventCreateData;
        this.eventName = eventName;
        this.eventStartLocation = eventStartLocation;
        this.eventDestination = eventDestination;
        this.departureDate = departureDate;
        this.eventBudget = eventBudget;
        this.eventExpense = eventExpense;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getEventCreateData() {
        return eventCreateData;
    }

    public void setEventCreateData(String eventCreateData) {
        this.eventCreateData = eventCreateData;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventStartLocation() {
        return eventStartLocation;
    }

    public void setEventStartLocation(String eventStartLocation) {
        this.eventStartLocation = eventStartLocation;
    }

    public String getEventDestination() {
        return eventDestination;
    }

    public void setEventDestination(String eventDestination) {
        this.eventDestination = eventDestination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getEventBudget() {
        return eventBudget;
    }

    public void setEventBudget(String eventBudget) {
        this.eventBudget = eventBudget;
    }

    public String getEventExpense() {
        return eventExpense;
    }

    public void setEventExpense(String eventExpense) {
        this.eventExpense = eventExpense;
    }
}
