package com.connorjensen.jobtracker.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class ConsoleSessionTest {
  private record Session(String output, ApplicationService service) {}

  private static String script(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private static String expected(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private static Session run(String script) {
    // Test specific needs
    ByteArrayInputStream input = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);

    // Copy of Main.java instantiation, but add in the new source for input
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
    TextTable table = new TextTable();
    ConsoleView view = new ConsoleView(output, table);
    ConsolePrompter prompter = new ConsolePrompter(scanner, output);
    ConsoleApplication application = new ConsoleApplication(service, prompter, view);

    // Return after starting the application.run()
    application.run();
    return new Session(bytes.toString(StandardCharsets.UTF_8), service);
  }

  @Test
  void testConsoleStartupValidity() {
    String testScript = "5\n";
    String textExpected =
        """
        Job Application Tracker
        0 Create application
        1 List applications
        2 Filter applications by status
        3 Edit application
        4 Delete application
        5 Quit
        > Goodbye.
        """;

    Session returnedSession = run(testScript);
    assertEquals(textExpected, returnedSession.output());
  }

  @Test
  void testEmptySavesNothingService() {
    String testScript = "5\n";
    List<Application> textExpected = new ArrayList<>();

    Session returnedSession = run(testScript);
    assertEquals(textExpected, returnedSession.service().listAll());
  }

  @Test
  void testTableFormat() {
    String testScript = script("0", "Apple", "Banana", "2026-08-24", "", "", "1", "5");
    String textExpected =
        expected(
            "Job Application Tracker",
            "0 Create application",
            "1 List applications",
            "2 Filter applications by status",
            "3 Edit application",
            "4 Delete application",
            "5 Quit",
            """
        > \
        Company: \
        Role: \
        Applied date (YYYY-MM-DD): \
        Notes (optional): \
        Job URL (optional): \
        Created application 1.\
        """,
            "0 Create application",
            "1 List applications",
            "2 Filter applications by status",
            "3 Edit application",
            "4 Delete application",
            "5 Quit",
            """
        > \
        +----+---------+--------+--------------+---------+-----+
        | ID | Company |  Role  | Applied Date | Status  | URL |
        +----+---------+--------+--------------+---------+-----+
        | 1  |  Apple  | Banana |  2026-08-24  | APPLIED |     |
        +----+---------+--------+--------------+---------+-----+\
        """,
            "0 Create application",
            "1 List applications",
            "2 Filter applications by status",
            "3 Edit application",
            "4 Delete application",
            "5 Quit",
            "> Goodbye.");

    Session returnedSession = run(testScript);
    assertEquals(textExpected, returnedSession.output());
  }
}
