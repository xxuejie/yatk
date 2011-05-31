package com.kbs.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

public class ConnectUtils
{
    public boolean wifiEnabled
        (Context ctx, boolean use3g)
    {
        if (use3g) {
            return
                Settings.System.getInt
                (ctx.getContentResolver(),
                 "airplane_mode_on", 0) == 0;
        }
        else {
            return
                Settings.Secure.getInt
                (ctx.getContentResolver(),
                 "wifi_disabled", 0) == 0;
        }            
    }

    // This one uses reflection to poke at some
    // new APIs added to the nook to the core framework
    // ConnectivytManager class to enable waking up
    // the wifi.

    public WifiLock newWifiLock
        (Context ctx, String tag, boolean use3g)
    {
        if (!wifiEnabled(ctx, use3g)) {
            return null;
        }

        ConnectivityManager cm = (ConnectivityManager)
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }

        Object actuallock = newlock(cm, tag);
        if (actuallock == null) {
            return null;
        }
        return new WifiLock(actuallock);
    }

    public boolean acquire(WifiLock wl)
    { return acquirelock(wl.m_lock); }

    public boolean acquire(WifiLock wl, long timeout)
    { return acquirelock(wl.m_lock, timeout); }

    public boolean release(WifiLock wl)
    {
        if (wl == null) { return true; }
        // play it safe
        if (isheld(wl.m_lock)) {
            return releaselock(wl.m_lock);
        }
        return true;
    }

    public boolean setReferenceCounted
        (WifiLock wl, boolean v)
    { return setref(wl.m_lock, v); }

    public boolean isHeld(WifiLock wl)
    { return isheld(wl.m_lock); }

    public final static class WifiLock
    {
        public WifiLock(Object l)
        { m_lock = l; }
        private final Object m_lock;
    }

    public boolean waitForService
        (Context ctx, long timeout, boolean use3g)
    {
        if (!wifiEnabled(ctx, use3g)) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager)
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        long cur =  System.currentTimeMillis();

        if (dontBotherMe(cur)) {
            Log.d(TAG, "Wont yet recheck network after last failure");
            return false;
        }

        long max = cur + timeout;
        long sleepinterval;
        if (timeout > 2000L) {
            sleepinterval = 2000L;
        }
        else {
            sleepinterval = timeout;
        }

        Log.d(TAG, "Waiting for network connection...");
        while (!isConnected(cm, use3g)) {
            try { Thread.currentThread().sleep(sleepinterval); }
            catch (InterruptedException iex) { return false; }
            cur = System.currentTimeMillis();
            if (cur > max) {
                botherMeAfter(cur + BOTHER_MSEC);
                return false;
            }
        }
        return true;
    }

    /// private methods only from here
    private synchronized void botherMeAfter(long msec)
    { s_botherme = msec; }
    private synchronized boolean dontBotherMe(long now)
    { return (now < s_botherme); }

    private boolean isConnected
        (ConnectivityManager cm, boolean use3g)
    {
        NetworkInfo ni = cm.getActiveNetworkInfo();

        // return true; // for emulator

        if (ni == null) { return false; }
        if (!use3g) {
            if (ni.getType() != ConnectivityManager.TYPE_WIFI) {
                return false;
            }
        }
        return ni.isConnected();
    }

    private Object newlock
        (ConnectivityManager cm, String tag)
    {
        try {
            Method wakelockmethed =
                cm.getClass().getMethod
                ("newWakeLock", Integer.TYPE, String.class);

            // 1 == radio_on_wake
            return wakelockmethed.invoke
                (cm, new Integer(1), tag);
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to create new lock", th);
            return null;
        }
    }

    private boolean isheld(Object lock)
    {
        try {
            Method isheldmethod =
                lock.getClass().getMethod("isHeld");
            Boolean retv = (Boolean) isheldmethod.invoke(lock);
            return retv.booleanValue();
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to find isheld-status", th);
            return false; // pretend it is not held.
        }
    }

    private boolean acquirelock(Object lock)
    {
        try {
            Method acquiremethod =
                lock.getClass().getMethod("acquire");
            acquiremethod.invoke(lock);
            return true;
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to acquire lock", th);
            return false;
        }
    }

    private boolean acquirelock(Object lock, long msec)
    {
        try {
            Method acquiremethod =
                lock.getClass().getMethod("acquire", Long.TYPE);
            acquiremethod.invoke(lock, new Long(msec));
            return true;
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to acquire lock", th);
            return false;
        }
    }

    private boolean setref(Object lock, boolean v)
    {
        try {
            Method acquiremethod =
                lock.getClass().getMethod("setReferenceCounted", Boolean.TYPE);
            acquiremethod.invoke(lock, Boolean.valueOf(v));
            return true;
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to set refcounting", th);
            return false;
        }
    }

    private boolean releaselock(Object lock)
    {
        try {
            Method releasemethod =
                lock.getClass().getMethod("release");
            releasemethod.invoke(lock, new Object[0]);
            return true;
        }
        catch (Throwable th) {
            Log.d(TAG, "Sorry, unable to release lock", th);
            return false;
        }
    }

    private final static String TAG = "connect-utils";
    private static long s_botherme = 0L;
    private final static long BOTHER_MSEC = 30*1000;
}
