package com.kbs.util;

import android.content.Context;


public class EmulatorConnectUtils extends ConnectUtils {

  @Override
  public boolean wifiEnabled(Context ctx, boolean use3g) {
    return super.wifiEnabled(ctx, use3g);
  }

  @Override
  public WifiLock newWifiLock(Context ctx, String tag, boolean use3g) {
    return new WifiLock(new Integer(1));
  }

  @Override
  public boolean acquire(WifiLock wl) {
    return true;
  }

  @Override
  public boolean acquire(WifiLock wl, long timeout) {
    return true;
  }

  @Override
  public boolean release(WifiLock wl) {
    return true;
  }

  @Override
  public boolean setReferenceCounted(WifiLock wl, boolean v) {
    return true;
  }

  @Override
  public boolean isHeld(WifiLock wl) {
    return true;
  }

  @Override
  public boolean waitForService(Context ctx, long timeout, boolean use3g) {
    return true;
  }

}
