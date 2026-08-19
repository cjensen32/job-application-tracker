package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.service.ApplicationService;

public class ConsolePrompter {
  private final Scanner promptScanner;
  private final PrintStream promptStream;

  public ConsolePrompter(Scanner promptScanner, PrintStream promptStream) {
    this.promptScanner = promptScanner;
    this.promptStream = promptStream;
  }

  public Integer input() {
    int selection = 6;
    String selectionString;

    while (selection > 5 || selection < 0) {
      ConsoleView.menu();
      if (this.promptScanner.hasNextLine()) {
        selectionString = this.promptScanner.nextLine();
      } else {
        selectionString = "5";
      }
      try {
        selection = Integer.parseInt(selectionString);
      } catch (NumberFormatException ignored) {
        continue;
      }
    }
    return selection;
  }

  public boolean dispatch(Scanner scanner, ApplicationService service, int selection) {
    return switch (selection) {
      case 0 -> createApplication(scanner, service);
      case 1 -> listApplications(service);
      case 2 -> filterApplications(scanner, service);
      case 3 -> editApplication(scanner, service); // TODO: Need to complete
      case 4 -> deleteApplication(scanner, service); // TODO: Need to complete
      case 5 -> false;
      default -> throw new IllegalStateException("validated selection escaped its boundary");
    };
  }

  // Case 0 - DONE
  public static boolean createApplication(Scanner scanner, ApplicationService service) {
    String company;
    String role;
    LocalDate appliedDate = null;
    String notes;
    String jobUrl;

    // a. Get company, append a newline to end.
    System.out.print("Company: ");
    if (scanner.hasNextLine()) {
      company = scanner.nextLine();
    } else {
      return false;
    }

    // b. Get role
    System.out.print("Role: ");
    if (scanner.hasNextLine()) {
      role = scanner.nextLine();
    } else {
      return false;
    }

    // c. Get applied date
    while (appliedDate == null) {
      System.out.print("Applied date (YYYY-MM-DD): ");
      String stringDate = null;
      if (scanner.hasNextLine()) {
        stringDate = scanner.nextLine();
      } else {
        return false;
      }

      // Validate is actual date
      try {
        appliedDate = LocalDate.parse(stringDate);
      } catch (DateTimeParseException ignored) {
        continue;
      }
    }

    // d. <Optional> Notes
    System.out.print("Notes (optional): ");
    if (scanner.hasNextLine()) {
      notes = scanner.nextLine();
    } else {
      return false;
    }

    // e. <Optional> jobUrl
    System.out.print("Job URL (optional): ");
    if (scanner.hasNextLine()) {
      jobUrl = scanner.nextLine();
    } else {
      return false;
    }

    if (company != null && role != null) {
      service.create(company, role, appliedDate, notes, jobUrl);
      return true;
    }
    return false;
  }

  // Case 1 - DONE
  public static boolean listApplications(ApplicationService service) {
    List<Application> allApplications = service.listAll();
    List<String> headerList = service.getLabels();
    List<List<String>> bodyRowsList = new ArrayList<>();

    if (allApplications.isEmpty()) {
      System.out.println("No applications to list");
    } else {
      for (Application application : allApplications) {
        bodyRowsList.add(application.toValuesList());
      }
      String textTable = TextTable.render(headerList, bodyRowsList);
      System.out.print(textTable);
    }
    return true;
  }

  // Case 2 - DONE
  public static boolean filterApplications(Scanner scanner, ApplicationService service) {

    // a. get status to filter by
    Status status = null;
    while (status == null) {
      System.out.print("Status: ");
      String statusEntry = null;
      if (scanner.hasNextLine()) {
        statusEntry = scanner.nextLine();
      } else {
        return false;
      }
      String statusLookup = statusEntry.strip().replace(" ", "_").toUpperCase();
      try {
        status = Status.valueOf(statusLookup);
      } catch (IllegalArgumentException ignored) {
        continue;
      }
    }
    // Lookup in service
    List<Application> statusApplications = service.listByStatus(status);

    if (statusApplications.isEmpty()) {
      System.out.println("No applications found for status " + status);
      return true;
    }
    List<String> headerList = service.getLabels();
    List<List<String>> bodyRowsList = new ArrayList<>();
    for (Application application : statusApplications) {
      bodyRowsList.add(application.toValuesList());
    }
    String textTable = TextTable.render(headerList, bodyRowsList);
    System.out.print(textTable);
    return true;
  }

  // Case 3 - DONE
  public static boolean editApplication(Scanner scanner, ApplicationService service) {
    System.out.println("TODO: Complete implementation");
    return true;
  }

  // Case 4 - TODO
  public static boolean deleteApplication(Scanner scanner, ApplicationService service) {
    System.out.println("TODO: Complete implementation");
    return true;
  }

  // Exit / EOF / END - TODO
  public static void exit() {
    System.out.println("Goodbye.");
  }
}
}
