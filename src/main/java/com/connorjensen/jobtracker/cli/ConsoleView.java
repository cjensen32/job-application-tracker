package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.ApplicationDetails;
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

  void showApplications(List<Application> applications) {
    List<String> headerList = Application.toLabelsList();
    List<List<String>> bodyRowsList = new ArrayList<>();

    for (Application application : applications) {
      bodyRowsList.add(application.toValuesList());
    }
    String textTable = this.textTable.render(headerList, bodyRowsList);
    this.consoleStream.print(textTable);
  }

  void showApplicationDetails(Application application) {
    this.consoleStream.println("Current application");
    for (ApplicationDetails detail : ApplicationDetails.values()) {
      this.consoleStream.println(detail.getLabel() + ": " + detail.getValue(application));
    }
  }

  // result and error messages
  void showCompletion(String completionType, Long id) {
    this.consoleStream.println(completionType + " application " + id.toString() + ".");
  }

  void showFoundNoStatusMatches(Status status) {
    this.consoleStream.println("No Applications found for status: " + status.getLabel());
  }

  void showNoIdMatches(Long applicationID) {
    this.consoleStream.println("No application found with ID: " + applicationID);
  }

  void showGoodbye() {
    this.consoleStream.println("Goodbye.");
  }

  public void showNoApplicationEntries(String modificationType) {
    this.consoleStream.println("No applications exist to " + modificationType + "!");
  }
}
