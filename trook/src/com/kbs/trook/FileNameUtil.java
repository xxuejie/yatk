// Copyright 2011 Google Inc. All Rights Reserved.

package com.kbs.trook;

import java.util.ArrayList;
import java.util.List;

public class FileNameUtil {
  private static final List<Character> FILTER_LIST = new ArrayList<Character>();
  private static final char REPLACE_CHAR = '_';
  private static final String START_FILTER_STRING = "作者:";
  private static final String DEFAULT_AUTHOR = "Unknown";

  static {
    FILTER_LIST.add('/');
    FILTER_LIST.add('\\');
    FILTER_LIST.add('?');
    FILTER_LIST.add('%');
    FILTER_LIST.add('*');
    FILTER_LIST.add(':');
    FILTER_LIST.add('|');
    FILTER_LIST.add('"');
    FILTER_LIST.add('<');
    FILTER_LIST.add('>');
    FILTER_LIST.add('.');
    FILTER_LIST.add(' ');
  }

  public static String regularFileName(String fileName) {
    if ((fileName == null) || (fileName.length() == 0)) {
      return DEFAULT_AUTHOR;
    }

    fileName = fileName.trim();
    if (fileName.startsWith(START_FILTER_STRING)) {
      fileName = fileName.substring(START_FILTER_STRING.length());
    }

    for (char c : FILTER_LIST) {
      fileName = fileName.replace(c, REPLACE_CHAR);
    }

    fileName = fileName.replaceAll("_+", "_");

    if (fileName.startsWith("_")) {
      fileName = fileName.substring(1);
    }
    if (fileName.endsWith("_")) {
      fileName = fileName.substring(0, fileName.length() - 1);
    }

    if (fileName.length() == 0) {
      fileName = DEFAULT_AUTHOR;
    }

    return fileName;
  }
}
