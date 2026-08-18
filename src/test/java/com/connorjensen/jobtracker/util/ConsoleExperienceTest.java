package com.connorjensen.jobtracker.util;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConsoleExperienceTest {

  private static final String HEADER = """
    ==============================================
    Welcome to Job Application Tracker!!
    What would you like to do?
    ==============================================
    """;

  private static final String MENU = """
     (0): Create new application
     (1): List all applications
     (2): List applications with status
     (3): Update status on application
     (4): Delete application by id
     (5): Quit Application
    > """ + " ";

  private static final String MENU_ERROR = """
    ==============================================
    ERROR IN ENTRY: Please try to enter again!!
    ==============================================
    """;

  private static final String DATE_ERROR =
    "!!Invalid format, use - YEAR-MONTH-DAY (YYYY-MM-DD)!!\n";

  private static final String APPLICATION_TABLE = """
    +----+-----------------+--------------------------+--------------+---------+-----+
    | ID |     Company     |           Role           | Applied Date | Status  | URL |
    +----+-----------------+--------------------------+--------------+---------+-----+
    | 1  | Shyft Solutions | Software Engineer Intern |  2026-08-16  | Applied |     |
    +----+-----------------+--------------------------+--------------+---------+-----+
    """;

  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;

  @AfterEach
  void restoreSystemStreams() {
    System.setIn(originalIn);
    System.setOut(originalOut);
  }

  @Test
  void completeConsoleWalkthrough() {
    String input = String.join("\n",
      "1",
      "6",
      "0",
      "Shyft Solutions",
      "Software Engineer Intern",
      "not-a-date",
      "2026-08-16",
      "1",
      "2",
      "3",
      "4",
      "5"
    ) + "\n";

    ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    System.setOut(new PrintStream(outputBuffer, true, StandardCharsets.UTF_8));

    ApplicationService service =
      new ApplicationService(new InMemoryApplicationRepository());
    ConsoleExperience consoleExperience = new ConsoleExperience(service);

    consoleExperience.startCLI(service);

    String output = outputBuffer.toString(StandardCharsets.UTF_8);
    List<Application> savedApplications = service.listAll();

    assertAll(
      () -> assertTrue(output.startsWith(HEADER + MENU + MENU + MENU_ERROR + MENU),
        "The first empty listing should return directly to the menu before validation retries."),
      () -> assertEquals(8, countOccurrences(output, MENU),
        "Every initial, retry, and post-selection menu should be rendered."),
      () -> assertEquals(1, countOccurrences(output, MENU_ERROR)),
      () -> assertEquals(1, countOccurrences(output, DATE_ERROR)),
      () -> assertEquals(1, countOccurrences(output, APPLICATION_TABLE)),
      () -> assertTrue(output.endsWith(MENU),
        "Option 5 should exit cleanly after the final menu prompt."),
      () -> assertEquals(1, savedApplications.size()),
      () -> assertEquals(1L, savedApplications.getFirst().getId()),
      () -> assertEquals("Shyft Solutions", savedApplications.getFirst().getCompany()),
      () -> assertEquals("Software Engineer Intern", savedApplications.getFirst().getRole()),
      () -> assertEquals(LocalDate.of(2026, 8, 16),
        savedApplications.getFirst().getAppliedDate())
    );

    assertAppearsInOrder(output,
      HEADER,
      MENU,
      MENU,
      MENU_ERROR,
      MENU,
      "Enter company name:\n> ",
      "Enter job name:\n> ",
      "Enter applied date (YYYY-MM-DD):\n> ",
      DATE_ERROR,
      "Enter applied date (YYYY-MM-DD):\n> ",
      MENU,
      APPLICATION_TABLE,
      MENU,
      MENU,
      MENU,
      MENU
    );
  }

  @Test
  void nonNumericMenuInputShowsErrorRetriesAndExitsCleanly() throws Exception {
    String javaExecutable = Path.of(
      System.getProperty("java.home"), "bin", "java").toString();

    Process process = new ProcessBuilder(
      javaExecutable,
      "-cp",
      System.getProperty("java.class.path"),
      "com.connorjensen.jobtracker.Main"
    ).redirectErrorStream(true).start();

    try (OutputStream processInput = process.getOutputStream()) {
      processInput.write("not-a-number\n5\n".getBytes(StandardCharsets.UTF_8));
    }

    if (!process.waitFor(5, TimeUnit.SECONDS)) {
      process.destroyForcibly();
      fail("The CLI subprocess did not finish within five seconds.");
    }

    String output = new String(
      process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    assertAll(
      () -> assertEquals(0, process.exitValue(),
        "Non-numeric input should be handled instead of terminating the CLI."),
      () -> assertEquals(1, countOccurrences(output, MENU_ERROR)),
      () -> assertEquals(2, countOccurrences(output, MENU),
        "The CLI should display the menu initially and again for the retry."),
      () -> assertTrue(output.startsWith(HEADER + MENU)),
      () -> assertTrue(output.endsWith(MENU),
        "The retry should accept option 5 and exit after the prompt.")
    );
  }

  private static int countOccurrences(String text, String expectedSection) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(expectedSection, index)) >= 0) {
      count++;
      index += expectedSection.length();
    }
    return count;
  }

  private static void assertAppearsInOrder(String output, String... sections) {
    int searchFrom = 0;
    for (String section : sections) {
      int sectionIndex = output.indexOf(section, searchFrom);
      assertTrue(sectionIndex >= 0,
        "Expected output section after index " + searchFrom + ":\n" + section);
      searchFrom = sectionIndex + section.length();
    }
  }
}
