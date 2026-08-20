package com.connorjensen.jobtracker;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import com.connorjensen.jobtracker.cli.ConsoleApplication;
import com.connorjensen.jobtracker.cli.ConsolePrompter;
import com.connorjensen.jobtracker.cli.ConsoleView;
import com.connorjensen.jobtracker.cli.TextTable;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class Main {
  private Main() {}

  public static void main(String[] args) throws Exception {
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    TextTable table = new TextTable();
    ConsoleView view = new ConsoleView(System.out, table);
    ConsolePrompter prompter = new ConsolePrompter(scanner, System.out);
    ConsoleApplication application = new ConsoleApplication(service, prompter, view);
    try {
      application.run();
    } catch (Exception e) {
      System.err.print("Error found, exiting here: " + e.getMessage() + "\n");
      System.err.print(Arrays.toString(e.getStackTrace()));
    }
    /*
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

    */
  }
}
