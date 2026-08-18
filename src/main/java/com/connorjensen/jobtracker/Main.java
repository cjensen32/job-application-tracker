package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.util.ApplicationTextTable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    // Activates the repository and service to store/manage apps
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);

    // Print first time statements
    printHeader();

    int selection = 0;

    // Switch to continue with process
    while (selection != 5) {
      printOptions();
      selection = selectionEntry();

      switch (selection) {
        case 0:
          applicationEntry(service);
          break;
        case 1:
          listApplications(service);
          break;
        default:
          break;
      }

    }
  }

  public static void printHeader() {
    System.out.print("""
                ==============================================
                Welcome to Job Application Tracker!!
                What would you like to do?
                ==============================================
                """);
  }

  public static void printOptions() {
    List<String> options = new ArrayList<>(List.of("Create new application", "List all applications", "List applications with status", "Update status on application", "Delete application by id", "Quit Application"));

    for (int i = 0; i < options.size(); i++) {
      System.out.println(" (" + i + "): " + options.get(i));
    }
    System.out.print("> ");
  }

  public static void printOptionsError() {
    System.out.print("""
                ==============================================
                ERROR IN ENTRY: Please try to enter again!!
                ==============================================
                """);
    printOptions();
  }

  public static int selectionEntry() {
    boolean success = false;
    int selection = 6; // Number out of range of options; keeps selection initialized

    while (!success) {
      try {
        String strSelection = scanner.nextLine();
        selection = Integer.parseInt(strSelection);
        if (selection <= 5 && selection >= 0) {
          success = true;
        } else {
          printOptionsError();
        }
      } catch (InputMismatchException e) {
        printOptionsError();
      }
    }
    return selection;
  }

  public static void applicationEntry(ApplicationService service) {
    System.out.print("Enter company name:\n> ");
    String company = scanner.nextLine();

    System.out.print("Enter job name:\n> ");
    String role = scanner.nextLine();

    LocalDate appliedDate = LocalDate.now();  // Set to current date as default
    boolean datePassCheck = false;
    while (!datePassCheck) {
      try {
        System.out.print("Enter applied date (YYYY-MM-DD):\n> ");
        String stringDate = scanner.nextLine();
        appliedDate = LocalDate.parse(stringDate);
        datePassCheck = true;
      } catch (DateTimeParseException e) {
        System.out.print("!!Invalid format, use - YEAR-MONTH-DAY (YYYY-MM-DD)!!\n");
        continue;
      }
    }
    service.create(company, role, appliedDate);
  }

  public static void listApplications(ApplicationService service) {
    if (service.listAll().isEmpty()) {
      return;
    }

    List<List<String>> applicationsListValues = new ArrayList<>();
    for (Application entry : service.listAll()) {
      applicationsListValues.add(entry.toValuesList());
    }
    ApplicationTextTable applicationTextTable = new ApplicationTextTable(service.getLabels(), applicationsListValues);
    applicationTextTable.toTable();
  }
}
