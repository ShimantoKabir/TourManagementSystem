package com.example.maask.tourmanagementsystem.GeoFenceFile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.example.maask.tourmanagementsystem.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeoFencingIntentService extends IntentService {

    public GeoFencingIntentService() {
        super("GeoFencingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int transitionType = event.getGeofenceTransition();
        List<Geofence> triggeringGeoFence = event.getTriggeringGeofences();
        String placeName = "";
        String status = "";
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            status = "Entered";
        }else if(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            status = "Exit";
        }

        ArrayList<String> triggeringGeoFencingIds = new ArrayList<>();
        for (Geofence geofence: triggeringGeoFence) {
            triggeringGeoFencingIds.add(geofence.getRequestId());
        }

        String notificationString = placeName + TextUtils.join(",",triggeringGeoFencingIds);

        sendNotification(notificationString,status);

    }

    private void sendNotification(String notificationString,String status) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentTitle(status);
        builder.setLights(Color.GREEN,1000,500);
        builder.setContentText(notificationString);
        builder.setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(11,builder.build());

    }

}
