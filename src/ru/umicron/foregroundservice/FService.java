package ru.umicron.foregroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FService extends Service {
private boolean isRunning = false;

@Override
public int onStartCommand(Intent intent, int flags, int startId) {

  if (!isRunning) {
      Log.w(getClass().getName(), "Got to play()!");
      isRunning=true;
      Notification note=new Notification(R.drawable.ic_launcher, "Can you hear the music?", System.currentTimeMillis());
      Intent i=new Intent(this, ForegroundService.class);
      i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
      PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
      note.setLatestEventInfo(this, "Fake Player", "Now Playing: \"Ummmm, Nothing\"", pi);
      note.flags|=Notification.FLAG_NO_CLEAR;
      startForeground(1337, note);
    }
  return(START_NOT_STICKY);
}

@Override
public void onDestroy() {
  stop();
}

@Override
public IBinder onBind(Intent intent) {
  return(null);
}

private void stop() {
  if (isRunning) {
    Log.w(getClass().getName(), "Got to stop()!");
    isRunning=false;
    stopForeground(true);
  }
}

}
