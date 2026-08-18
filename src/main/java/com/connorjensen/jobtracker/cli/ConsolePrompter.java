package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsolePrompter {
  private Scanner scanner;
  private PrintStream output;

  public ConsolePrompter(Scanner scanner, PrintStream output) {
    this.scanner = scanner;
    this.output = output;
  }
}
