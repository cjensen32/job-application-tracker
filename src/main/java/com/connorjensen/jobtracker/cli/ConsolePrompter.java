package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsolePrompter {
  private final Scanner promptScanner;
  private final PrintStream promptStream;

  public ConsolePrompter(Scanner promptScanner, PrintStream promptStream) {
    this.promptScanner = promptScanner;
    this.promptStream = promptStream;
  }

}
