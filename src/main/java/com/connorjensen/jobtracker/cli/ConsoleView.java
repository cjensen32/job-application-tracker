package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;

public class ConsoleView {
  private PrintStream output;
  private TextTable table;

  public ConsoleView(PrintStream output, TextTable table) {
    this.output = output;
    this.table = table;
  }
}
