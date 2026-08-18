package com.connorjensen.jobtracker.util;

public class Centering {
  private Centering() {}

  public static String center(String text, int width) {
    if (text == null) {
      text = "";
    } else {
      text = text.strip();
    }

    int padding = width - text.length();
    if (padding <= 0) {
      return text;
    }

    int left = padding / 2;
    return " ".repeat(left) + text + " ".repeat(padding - left);
  }
}
