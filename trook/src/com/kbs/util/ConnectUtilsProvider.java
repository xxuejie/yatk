package com.kbs.util;

import android.os.Build;

public class ConnectUtilsProvider {
  public ConnectUtils get() {
    if ("sdk".equals(Build.PRODUCT)) {
      // emulator
      return new EmulatorConnectUtils();
    } else {
      // real machine
      return new ConnectUtils();
    }
  }
}
