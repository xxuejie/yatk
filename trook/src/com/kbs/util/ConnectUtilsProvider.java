package com.kbs.util;


public class ConnectUtilsProvider {
  public ConnectUtils get() {
    return new EmulatorConnectUtils();
  }
}
