package com.kbs.util;

public class UrlUtils {
  public final static String getHref(String baseUri, String link) {
    if ((link == null) || (link.length() == 0)) {
      return baseUri;
    }
    if (link.toLowerCase().startsWith("http")) {
      return link;
    }

    if (!link.startsWith("/")) {
      if (!baseUri.endsWith("/")) {
        baseUri += "/";
      }
      return baseUri + link;
    }

    int startIndex = baseUri.indexOf("//");
    if (startIndex == -1) {
      return link;
    }

    int endIndex = baseUri.indexOf("/", startIndex + 2);
    if (endIndex != -1) {
      baseUri = baseUri.substring(0, endIndex);
    }

    return baseUri + link;
  }
}
