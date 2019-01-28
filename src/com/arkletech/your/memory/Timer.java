package com.arkletech.your.memory;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Timer {
	static final String TAG="MyAndroidDebugTAG";
	final int MSG_START_TIMER = 0;
	final int MSG_STOP_TIMER = 1;
	final int MSG_UPDATE_TIMER = 2;
	final long REFRESH_RATE =900;	// allow the time to process the event
	
	long startTime=0L;
	long stopTime=0L;
	long elapsedTime=0L;
	long refreshRate=REFRESH_RATE;
	boolean once=false;
	boolean timerIsTicking=false;
	OnTickListener listener;
	Timer self=this;
	
	Timer(OnTickListener l, long rate, boolean one_shot) {
		Log.d(TAG, "Timer Constructor("+l+", "+rate+", "+one_shot+")");
		listener = l;
		refreshRate = rate;
		once = one_shot;
	}
	
	Timer(OnTickListener l) {
		Log.d(TAG, "Timer Constructor("+l+")");
		listener = l;
	}
	
	public void setRefreshRate(long rate) {
		refreshRate = rate;
	}
	
	public boolean isTimerRunning() {
		return timerIsTicking;
	}
		
	public boolean start() {
		Log.d(TAG, "Timer start(): timerIsTicking="+timerIsTicking);
		boolean lastState = timerIsTicking;
		if (!timerIsTicking) {
			handler.sendEmptyMessage(MSG_START_TIMER);
			startTime = System.currentTimeMillis();
			timerIsTicking = true;
		}
		return lastState;
	}
	
	public boolean stop() {
		Log.d(TAG, "Timer stop(): timerIsTicking="+timerIsTicking+", once="+once);
		boolean lastState = timerIsTicking;
		if (timerIsTicking) {
			if (once == false)
				handler.sendEmptyMessage(MSG_STOP_TIMER);
			stopTime = System.currentTimeMillis();
			elapsedTime += stopTime - startTime;
			timerIsTicking = false;
		}
		return lastState;
	}

	public void reset() {
		Log.d(TAG, "Timer reset(): timerIsTicking="+timerIsTicking+", once="+once);
		if (timerIsTicking)
			startTime = System.currentTimeMillis();
		else
			startTime = 0L;
		stopTime = 0L;
		elapsedTime = 0L;
		handler.sendEmptyMessage(MSG_STOP_TIMER);
	}
	
	public Long getElapsedTime() {
		if (timerIsTicking) {
			stopTime = System.currentTimeMillis();
			elapsedTime += stopTime - startTime;
			startTime = stopTime;
		}
		
		return elapsedTime / 1000;	// return time in seconds
	}
	
	public long setElapsedTime(long t) {
		long tmp = elapsedTime;
		elapsedTime = t * 1000;
		return tmp;
	}
	
	public void setOntickListener(OnTickListener l) {
		listener = l;
	}
	
	Handler handler = new Handler()
	{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
	            case MSG_START_TIMER:
//	            	Log.d(TAG, "handleMessage() = MSG_START_TIMER");
	            	if (once == true) {
	            		handler.sendEmptyMessageDelayed(MSG_STOP_TIMER, refreshRate);
	            	}
	            	else {
	            		handler.sendEmptyMessage(MSG_UPDATE_TIMER);
	            	}
	                break;
	
	            case MSG_UPDATE_TIMER:
//	            	Log.d(TAG, "handleMessage() = MSG_UPDATE_TIMER");
	                listener.tickListener(self, getElapsedTime());
	                handler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, refreshRate); // updated every second, 
	                break;
	            case MSG_STOP_TIMER:
//	            	Log.d(TAG, "handleMessage() = MSG_STOP_TIMER");
	            	if (once == true)
	            		stop();
	            	handler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
	                listener.tickListener(self, getElapsedTime());
	                break;
	
	            default:
	                break;
            }
        }
    };

}
