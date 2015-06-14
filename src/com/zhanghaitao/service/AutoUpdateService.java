package com.zhanghaitao.service;

import com.zhanghaitao.receiver.AutoUpdateReceiver;
import com.zhanghaitao.util.MyLog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		System.out.println("service onstartCommand");
		MyLog.w("service onstartCommand");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}).start();
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int setTime = 1000*5;
		long triggerAtTime = SystemClock.elapsedRealtime() + setTime;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
//		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+setTime, pendingIntent);
		
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), setTime, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}

}
