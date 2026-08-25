package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

public class ConsoleView {
  private final PrintStream consoleStream;
  private final TextTable textTable;

  public ConsoleView(PrintStream viewStream, TextTable textTable) {
    this.consoleStream = viewStream;
    this.textTable = textTable;
  }

  private static final List<String> COLUMNS =
      List.of("ID", "Company", "Role", "Applied Date", "Status", "URL");

  private static final List<String> DETAILS =
      List.of("ID", "Company", "Role", "Applied date", "Status", "Notes", "URL");

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
    List<List<String>> bodyRowsList = new ArrayList<>();

    for (Application application : applications) {
      bodyRowsList.add(applicationToRow(application));
    }
    String textTable = this.textTable.render(COLUMNS, bodyRowsList);
    this.consoleStream.print(textTable);
  }

  void showApplicationDetails(Application application) {
    this.consoleStream.println("Current application");
    List<String> applicationDetails = applicationToDetails(application);
    for (int i = 0; i < DETAILS.size(); i++) {
      this.consoleStream.println(DETAILS.get(i) + ": " + applicationDetails.get(i));
    }
  }

  // result and error messages
  void showCompletion(String completionType, Long id) {
    this.consoleStream.println(completionType + " application " + id.toString() + ".");
  }

  void showFoundNoStatusMatches(Status status) {
    this.consoleStream.println("No applications found for status " + status + ".");
  }

  void showNoIdMatches(Long applicationID) {
    this.consoleStream.println("No application found with ID " + applicationID + ".");
  }

  void showGoodbye() {
    this.consoleStream.println("Goodbye.");
  }

  void showNoApplicationEntries(String modificationType) {
    this.consoleStream.println("No applications exist to " + modificationType + ".");
  }

  // Helper for turning into a table row
  private List<String> applicationToRow(Application application) {

    return List.of(
        application.getId().toString(),
        application.getCompany(),
        application.getRole(),
        application.getAppliedDate().toString(),
        application.getStatus().name(),
        application.getJobUrl());
  }

  private List<String> applicationToDetails(Application application) {

    return List.of(
        application.getId().toString(),
        application.getCompany(),
        application.getRole(),
        application.getAppliedDate().toString(),
        application.getStatus().name(),
        application.getNotes(),
        application.getJobUrl());
  }
}
