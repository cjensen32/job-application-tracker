package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

public class ConsoleView {
  private final PrintStream consoleStream;
  private final TextTable textTable;

  public ConsoleView(PrintStream viewStream, TextTable textTable) {
    this.consoleStream = viewStream;
    this.textTable = textTable;
  }

  void showHeader() {
    this.consoleStream.println("Job Application Tracker");
  }

  void showMenu() {
    // Options as list
    List<String> options =
        new ArrayList<>(
            List.of(
                "0 Create application",
                "1 List applications",
                "2 Filter applications by status",
                "3 Edit application",
                "4 Delete application",
                "5 Quit"));

    // Print options, with trailing entry line and space
    for (String option : options) {
      this.consoleStream.println(option);
    }
    this.consoleStream.print("> ");
  }

  void showApplications(List<Application> applications, Optional<Status> status) {
    if (applications.isEmpty() && status.isEmpty()) {
      this.consoleStream.println("No applications to list");
    } else if (applications.isEmpty()) {
      showFoundNoStatusMatches(status.get());
    } else {
      List<String> headerList = Application.toLabelsList();
      List<List<String>> bodyRowsList = new ArrayList<>();

      for (Application application : applications) {
        bodyRowsList.add(application.toValuesList());
      }
      String textTable = this.textTable.render(headerList, bodyRowsList);
      this.consoleStream.print(textTable);
    }
  }

  void showGoodbye() {
    this.consoleStream.println("Goodbye.");
  }

  public void showCompletion(String completionNote) {
    this.consoleStream.println(completionNote);
  }

  public void showFoundNoStatusMatches(Status status) {
    this.consoleStream.println("No Applications found for status: " + status.getLabel());
  }
}
