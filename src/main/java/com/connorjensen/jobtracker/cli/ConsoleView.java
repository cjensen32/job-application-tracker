package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;

public class ConsoleView {
  private final PrintStream consoleStream;
  private final TextTable textTable;

  public ConsoleView(PrintStream viewStream, TextTable textTable) {
    this.consoleStream = viewStream;
    this.textTable = textTable;
  }

  public static void menu() {}
}
