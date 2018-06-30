package com.example.maask.tourmanagementsystem.DirectionFile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Maask on 1/30/2018.
 */

public class DirectionResponse {
    @SerializedName("geocoded_waypoints")
    @Expose
    private List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.GeocodedWaypoint> geocodedWaypoints = null;
    @SerializedName("routes")
    @Expose
    private List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Bounds {

        @SerializedName("northeast")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Northeast northeast;
        @SerializedName("southwest")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Southwest southwest;

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Northeast getNortheast() {
            return northeast;
        }

        public void setNortheast(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Northeast northeast) {
            this.northeast = northeast;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Southwest southwest) {
            this.southwest = southwest;
        }

    }


    public static class Distance {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }


    public static class Distance_ {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }


    public static class Duration {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }


    public static class Duration_ {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }


    public static class EndLocation {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }


    public static class EndLocation_ {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }



    public static class GeocodedWaypoint {

        @SerializedName("geocoder_status")
        @Expose
        private String geocoderStatus;
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @SerializedName("types")
        @Expose
        private List<String> types = null;

        public String getGeocoderStatus() {
            return geocoderStatus;
        }

        public void setGeocoderStatus(String geocoderStatus) {
            this.geocoderStatus = geocoderStatus;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

    }


    public static class Leg {

        @SerializedName("distance")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance distance;
        @SerializedName("duration")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration duration;
        @SerializedName("end_address")
        @Expose
        private String endAddress;
        @SerializedName("end_location")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation endLocation;
        @SerializedName("start_address")
        @Expose
        private String startAddress;
        @SerializedName("start_location")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation startLocation;
        @SerializedName("steps")
        @Expose
        private List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Step> steps = null;
        @SerializedName("traffic_speed_entry")
        @Expose
        private List<Object> trafficSpeedEntry = null;
        @SerializedName("via_waypoint")
        @Expose
        private List<Object> viaWaypoint = null;

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance getDistance() {
            return distance;
        }

        public void setDistance(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance distance) {
            this.distance = distance;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration getDuration() {
            return duration;
        }

        public void setDuration(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration duration) {
            this.duration = duration;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation endLocation) {
            this.endLocation = endLocation;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation startLocation) {
            this.startLocation = startLocation;
        }

        public List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Step> getSteps() {
            return steps;
        }

        public void setSteps(List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Step> steps) {
            this.steps = steps;
        }

        public List<Object> getTrafficSpeedEntry() {
            return trafficSpeedEntry;
        }

        public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry) {
            this.trafficSpeedEntry = trafficSpeedEntry;
        }

        public List<Object> getViaWaypoint() {
            return viaWaypoint;
        }

        public void setViaWaypoint(List<Object> viaWaypoint) {
            this.viaWaypoint = viaWaypoint;
        }

    }


    public static class Northeast {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }


    public static class OverviewPolyline {

        @SerializedName("points")
        @Expose
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

    }


    public static class Polyline {

        @SerializedName("points")
        @Expose
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

    }


    public static class Route {

        @SerializedName("bounds")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Bounds bounds;
        @SerializedName("copyrights")
        @Expose
        private String copyrights;
        @SerializedName("legs")
        @Expose
        private List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Leg> legs = null;
        @SerializedName("overview_polyline")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.OverviewPolyline overviewPolyline;
        @SerializedName("summary")
        @Expose
        private String summary;
        @SerializedName("warnings")
        @Expose
        private List<Object> warnings = null;
        @SerializedName("waypoint_order")
        @Expose
        private List<Object> waypointOrder = null;

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Bounds getBounds() {
            return bounds;
        }

        public void setBounds(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Bounds bounds) {
            this.bounds = bounds;
        }

        public String getCopyrights() {
            return copyrights;
        }

        public void setCopyrights(String copyrights) {
            this.copyrights = copyrights;
        }

        public List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Leg> legs) {
            this.legs = legs;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }

        public void setOverviewPolyline(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.OverviewPolyline overviewPolyline) {
            this.overviewPolyline = overviewPolyline;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<Object> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<Object> warnings) {
            this.warnings = warnings;
        }

        public List<Object> getWaypointOrder() {
            return waypointOrder;
        }

        public void setWaypointOrder(List<Object> waypointOrder) {
            this.waypointOrder = waypointOrder;
        }

    }


    public static class Southwest {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }


    public static class StartLocation {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }


    public static class StartLocation_ {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }


    public static class Step {

        @SerializedName("distance")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance_ distance;
        @SerializedName("duration")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration_ duration;
        @SerializedName("end_location")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation_ endLocation;
        @SerializedName("html_instructions")
        @Expose
        private String htmlInstructions;
        @SerializedName("polyline")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Polyline polyline;
        @SerializedName("start_location")
        @Expose
        private com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation_ startLocation;
        @SerializedName("travel_mode")
        @Expose
        private String travelMode;
        @SerializedName("maneuver")
        @Expose
        private String maneuver;

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance_ getDistance() {
            return distance;
        }

        public void setDistance(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Distance_ distance) {
            this.distance = distance;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration_ getDuration() {
            return duration;
        }

        public void setDuration(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Duration_ duration) {
            this.duration = duration;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation_ getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.EndLocation_ endLocation) {
            this.endLocation = endLocation;
        }

        public String getHtmlInstructions() {
            return htmlInstructions;
        }

        public void setHtmlInstructions(String htmlInstructions) {
            this.htmlInstructions = htmlInstructions;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Polyline getPolyline() {
            return polyline;
        }

        public void setPolyline(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.Polyline polyline) {
            this.polyline = polyline;
        }

        public com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation_ getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(com.example.maask.tourmanagementsystem.DirectionFile.DirectionResponse.StartLocation_ startLocation) {
            this.startLocation = startLocation;
        }

        public String getTravelMode() {
            return travelMode;
        }

        public void setTravelMode(String travelMode) {
            this.travelMode = travelMode;
        }

        public String getManeuver() {
            return maneuver;
        }

        public void setManeuver(String maneuver) {
            this.maneuver = maneuver;
        }

    }
}