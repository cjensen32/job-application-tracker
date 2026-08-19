package com.connorjensen.jobtracker;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.connorjensen.jobtracker.cli.TextTable;
import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class Main {
  private Main() {}

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);

    // 1. Show Menu (header)
    System.out.println("Job Application Tracker");

    // 4. repeat
    run(scanner, service);
  }

  private static void run(Scanner scanner, ApplicationService service) {
    boolean running = true;
    int selection;

    while (running) {
      selection = input(scanner);
      running = dispatch(scanner, service, selection);
    }
    exit();
  }

  private static void menu() {
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
      System.out.println(option);
    }
    System.out.print("> ");
  }

  public static Integer input(Scanner scanner) {
    int selection = 6;
    String selectionString;

    while (selection > 5 || selection < 0) {
      menu();
      if (scanner.hasNextLine()) {
        selectionString = scanner.nextLine();
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

  public static boolean dispatch(Scanner scanner, ApplicationService service, int selection) {
    boolean running =
        switch (selection) {
          case 0 -> createApplication(scanner, service);
          case 1 -> listApplications(service);
          case 2 -> filterApplications(scanner, service);
          case 3 -> editApplication(scanner, service); // TODO: Need to complete
          case 4 -> deleteApplication(scanner, service); // TODO: Need to complete
          case 5 -> false;
          default -> throw new IllegalStateException("validated selection escaped its boundary");
        };
    return running;
  }

  // Case 0 - DONE
  public static boolean createApplication(Scanner scanner, ApplicationService service) {
    String company = null;
    String role = null;
    LocalDate appliedDate = null;
    String notes = "";
    String jobUrl = "";

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
      TextTable textTable = new TextTable(headerList, bodyRowsList);
      textTable.toTable();
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
    TextTable textTable = new TextTable(headerList, bodyRowsList);
    textTable.toTable();
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
