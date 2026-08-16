package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        
        // Activates the repository and service to store/manage apps
        ApplicationRepository repository = new InMemoryApplicationRepository();
        ApplicationService service = new ApplicationService(repository);

        // Print first time statements
        printHeader();
        printOptions();

        int selection = 0;

        // Switch to continue with process
        while (selection != 5) {
            selection = takeInput();

            Application result = switch (selection) {
                case 0 -> ApplicationEntry(service);
                default -> ApplicationEntry(service);
            };
            
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
        List<String> options = new ArrayList<>(
                List.of(
                    "Create new application",  
                    "List all applications", 
                    "List applications with status",  
                    "Update status on application",
                    "Delete application by id",
                    "Quit Application"
                    )
                );

        for (int i = 0; i < options.size(); i++) { 
            System.out.println(" (" + i + "): " +  options.get(i));
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

    public static int takeInput() {
        boolean success = false;  
        int selection = 6; // Number out of range of options; keeps selection initialized 
        while (!success) {
            try {
                selection = scanner.nextInt();
                if (selection < 5 && selection >= 0) {
                    success = true;
                } else {
                    printOptionsError();
                }
            } catch(InputMismatchException e) {
                printOptionsError();
                scanner.nextLine();
            }
        }
        return selection;
    }

    public static Application ApplicationEntry(ApplicationService service) {
        scanner.nextLine(); // Clear the buffer

        System.out.print("Enter company name:\n> ");
        String company = scanner.nextLine();

        System.out.print("Enter job name:\n> ");
        String role = scanner.nextLine();

        System.out.print("Enter applied date (YYYY-MM-DD):\n> ");
        String stringDate = scanner.nextLine();
        LocalDate appliedDate = LocalDate.parse(stringDate);
        
        Application result = service.create(company, role, appliedDate);
        System.out.print(result);
        return result;
    }
}
