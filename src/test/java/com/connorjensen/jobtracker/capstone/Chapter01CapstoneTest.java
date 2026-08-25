/*
 * CAPSTONE GRADER — SPOILERS.
 * Solve from CAPSTONE.md, not from this file.
 * Install with:
 * cp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java \
 *   src/test/java/com/connorjensen/jobtracker/capstone/
 */
package com.connorjensen.jobtracker.capstone;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.connorjensen.jobtracker.Main;
import com.connorjensen.jobtracker.cli.ConsoleApplication;
import com.connorjensen.jobtracker.cli.ConsolePrompter;
import com.connorjensen.jobtracker.cli.ConsoleView;
import com.connorjensen.jobtracker.cli.TextTable;
import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import com.connorjensen.jobtracker.service.CreateApplicationRequest;
import com.connorjensen.jobtracker.service.UpdateApplicationRequest;

@DisplayName("Chapter 1 Capstone — The Tracker Core")
class Chapter01CapstoneTest {

  private static final LocalDate DATE = LocalDate.of(2026, 8, 1);
  private static final String HEADER = "Job Application Tracker\n";
  private static final String MENU =
      """
      0 Create application
      1 List applications
      2 Filter applications by status
      3 Edit application
      4 Delete application
      5 Quit
      """
          + "> ";
  private static final String STATUS_ERROR =
      "Choose one of: APPLIED, PHONE_SCREEN, INTERVIEWING, OFFER, REJECTED.\n";
  private static final String URL_ERROR = "Enter a blank value or an absolute HTTP(S) URL.\n";

  @Nested
  @DisplayName("existing model and repository contract")
  class ExistingContractTests {

    @Test
    void statusHasExactlyFiveRequiredValues() {
      assertArrayEquals(
          new String[] {"APPLIED", "PHONE_SCREEN", "INTERVIEWING", "OFFER", "REJECTED"},
          Arrays.stream(Status.values()).map(Enum::name).toArray(String[]::new));
    }

    @Test
    void newApplicationDefaultsToAppliedWithNoId() {
      Application application = sample("Acme");

      assertAll(
          () -> assertEquals(null, application.getId()),
          () -> assertEquals(Status.APPLIED, application.getStatus()),
          () -> assertEquals("Acme", application.getCompany()),
          () -> assertEquals("Engineer", application.getRole()),
          () -> assertEquals(DATE, application.getAppliedDate()));
    }

    @Test
    void repositoryAssignsIdsUpdatesAndFindsValues() {
      ApplicationRepository repository = new InMemoryApplicationRepository();
      Application first = repository.save(sample("Acme"));
      Application second = repository.save(sample("Globex"));

      first.setStatus(Status.OFFER);
      repository.save(first);

      assertAll(
          () -> assertEquals(1L, first.getId()),
          () -> assertEquals(2L, second.getId()),
          () -> assertEquals(2, repository.findAll().size()),
          () -> assertEquals(first, repository.findById(1L).orElseThrow()),
          () -> assertEquals(List.of(first), repository.findByStatus(Status.OFFER)),
          () -> assertTrue(repository.findByStatus(Status.REJECTED).isEmpty()));
    }

    @Test
    void repositoryEmptyResultsAreNeverNull() {
      ApplicationRepository repository = new InMemoryApplicationRepository();

      assertAll(
          () -> assertNotNull(repository.findAll()),
          () -> assertTrue(repository.findAll().isEmpty()),
          () -> assertNotNull(repository.findById(99L)),
          () -> assertTrue(repository.findById(99L).isEmpty()),
          () -> assertNotNull(repository.findByStatus(Status.OFFER)),
          () -> assertTrue(repository.findByStatus(Status.OFFER).isEmpty()));
    }

    @Test
    void repositoryDeleteReportsSuccessThenAbsence() {
      ApplicationRepository repository = new InMemoryApplicationRepository();
      Long id = repository.save(sample("Acme")).getId();

      assertAll(
          () -> assertTrue(repository.deleteById(id)),
          () -> assertFalse(repository.deleteById(id)),
          () -> assertFalse(repository.deleteById(999L)));
    }
  }

  @Nested
  @DisplayName("request records and service")
  class ServiceTests {

    @Test
    void requestRecordsHaveThePublishedComponentsAndValueEquality() {
      CreateApplicationRequest create =
          createRequest("Acme", "Engineer", DATE, "Referral", "https://example.com/jobs/1");
      CreateApplicationRequest equal =
          createRequest("Acme", "Engineer", DATE, "Referral", "https://example.com/jobs/1");
      UpdateApplicationRequest update =
          new UpdateApplicationRequest(
              "Globex", "Senior Engineer", DATE.plusDays(1), Status.OFFER, "Note", "");

      assertAll(
          () -> assertTrue(CreateApplicationRequest.class.isRecord()),
          () -> assertTrue(UpdateApplicationRequest.class.isRecord()),
          () -> assertEquals(equal, create),
          () -> assertEquals("Acme", create.company()),
          () -> assertEquals("Engineer", create.role()),
          () -> assertEquals(DATE, create.appliedDate()),
          () -> assertEquals("Referral", create.notes()),
          () -> assertEquals("https://example.com/jobs/1", create.jobUrl()),
          () -> assertEquals(Status.OFFER, update.status()));
    }

    @Test
    void requestBasedCreateCopiesEveryFieldAndDefaultsStatus() {
      ApplicationService service = service();

      Application created =
          service.create(
              createRequest("Acme", "Engineer", DATE, "Referral", "https://example.com/jobs/1"));

      assertAll(
          () -> assertEquals(1L, created.getId()),
          () -> assertEquals("Acme", created.getCompany()),
          () -> assertEquals("Engineer", created.getRole()),
          () -> assertEquals(DATE, created.getAppliedDate()),
          () -> assertEquals(Status.APPLIED, created.getStatus()),
          () -> assertEquals("Referral", created.getNotes()),
          () -> assertEquals("https://example.com/jobs/1", created.getJobUrl()));
    }

    @Test
    void fullUpdateCopiesEveryFieldAndPersistsIt() {
      ApplicationService service = service();
      Long id = service.create("Acme", "Engineer", DATE).getId();
      UpdateApplicationRequest request =
          new UpdateApplicationRequest(
              "Globex",
              "Senior Engineer",
              DATE.plusDays(4),
              Status.INTERVIEWING,
              "Second round",
              "https://example.com/jobs/2");

      Application updated = service.update(id, request);
      Application reloaded = service.findById(id).orElseThrow();

      assertAll(
          () -> assertEquals(updated, reloaded),
          () -> assertEquals("Globex", reloaded.getCompany()),
          () -> assertEquals("Senior Engineer", reloaded.getRole()),
          () -> assertEquals(DATE.plusDays(4), reloaded.getAppliedDate()),
          () -> assertEquals(Status.INTERVIEWING, reloaded.getStatus()),
          () -> assertEquals("Second round", reloaded.getNotes()),
          () -> assertEquals("https://example.com/jobs/2", reloaded.getJobUrl()));
    }

    @Test
    void missingUpdatesThrowIllegalArgumentException() {
      ApplicationService service = service();

      assertAll(
          () ->
              assertThrows(
                  IllegalArgumentException.class, () -> service.updateStatus(99L, Status.OFFER)),
          () ->
              assertThrows(
                  IllegalArgumentException.class,
                  () ->
                      service.update(
                          99L,
                          new UpdateApplicationRequest(
                              "Acme", "Engineer", DATE, Status.OFFER, "", ""))));
    }

    @Test
    void compatibilityOperationsRemainAvailable() {
      ApplicationService service = service();
      Application created = service.create("Acme", "Engineer", DATE);
      service.updateStatus(created.getId(), Status.OFFER);

      assertAll(
          () -> assertEquals(1, service.listAll().size()),
          () -> assertEquals(1, service.listByStatus(Status.OFFER).size()),
          () -> assertEquals(created, service.findById(created.getId()).orElseThrow()),
          () -> assertTrue(service.delete(created.getId())),
          () -> assertFalse(service.delete(created.getId())));
    }

    @Test
    void serviceUsesTheInjectedRepositoryForNewOperations() {
      RecordingRepository repository = new RecordingRepository();
      ApplicationService service = new ApplicationService(repository);

      Application created = service.create(createRequest("Acme", "Engineer", DATE, "", ""));
      service.update(
          created.getId(),
          new UpdateApplicationRequest("Globex", "SRE", DATE, Status.PHONE_SCREEN, "", ""));

      assertAll(
          () -> assertEquals(42L, created.getId()),
          () -> assertTrue(repository.calls.contains("save")),
          () -> assertTrue(repository.calls.contains("findById")),
          () -> assertEquals("Globex", repository.rows.get(42L).getCompany()));
    }
  }

  @Nested
  @DisplayName("CLI architecture")
  class ArchitectureTests {

    @Test
    void cliPublicApiIsMinimal() {
      assertAll(
          () -> assertOnlyPublicMethods(ConsolePrompter.class, Set.of()),
          () -> assertOnlyPublicMethods(ConsoleView.class, Set.of()),
          () -> assertOnlyPublicMethods(ConsoleApplication.class, Set.of("run")),
          () -> assertOnlyPublicMethods(TextTable.class, Set.of("render")));
    }

    @Test
    void cliConstructorsMatchThePublishedDependencyGraph() {
      assertAll(
          () -> assertPublicConstructor(ConsolePrompter.class, Scanner.class, PrintStream.class),
          () -> assertPublicConstructor(ConsoleView.class, PrintStream.class, TextTable.class),
          () ->
              assertPublicConstructor(
                  ConsoleApplication.class,
                  ApplicationService.class,
                  ConsolePrompter.class,
                  ConsoleView.class),
          () -> assertPublicConstructor(TextTable.class));
    }

    @Test
    void oldUtilityCliTypesAreGone() {
      assertAll(
          () -> assertClassMissing("com.connorjensen.jobtracker.cli.ConsoleExperience"),
          () -> assertClassMissing("com.connorjensen.jobtracker.util.ApplicationTextTable"),
          () -> assertClassMissing("com.connorjensen.jobtracker.util.Centering"));
    }
  }

  @Nested
  @DisplayName("pure text table")
  class TextTableTests {

    @Test
    void headerAndBodyControlColumnWidths() {
      assertEquals(
          expected(
              "+-----------------+--------------------------+----------+",
              "|       Co        |           Role           |    St    |",
              "+-----------------+--------------------------+----------+",
              "| Shyft Solutions | Software Engineer Intern | APPLIED  |",
              "|      Acme       |           Dev            | REJECTED |",
              "+-----------------+--------------------------+----------+"),
          render(
              row("Co", "Role", "St"),
              body(
                  row("Shyft Solutions", "Software Engineer Intern", "APPLIED"),
                  row("Acme", "Dev", "REJECTED"))));
    }

    @Test
    void nullEmptyAndWhitespaceCellsAreNormalized() {
      assertEquals(
          expected(
              "+---------+----------+--------+",
              "| Company |          | Status |",
              "+---------+----------+--------+",
              "|  Acme   |          |        |",
              "|         | Engineer |  OPEN  |",
              "+---------+----------+--------+"),
          render(
              row(" Company ", null, "Status"),
              body(row(" Acme ", "", null), row(null, " Engineer ", " OPEN "))));
    }

    @Test
    void completelyEmptyInputReturnsEmptyString() {
      assertEquals("", render(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void emptyBodyRendersHeaderAndOneClosingBorder() {
      assertEquals(
          expected("+---------+------+", "| Company | Role |", "+---------+------+"),
          render(row("Company", "Role"), new ArrayList<>()));
    }

    @Test
    void headerShorterThanBodyGainsBlankColumns() {
      assertEquals(
          expected(
              "+-----+-----+", "|  A  |     |", "+-----+-----+", "| one | two |", "+-----+-----+"),
          render(row("A"), body(row("one", "two"))));
    }

    @Test
    void headerLongerThanBodyPadsBodyRows() {
      assertEquals(
          expected(
              "+-----+---+---+",
              "|  A  | B | C |",
              "+-----+---+---+",
              "| one |   |   |",
              "+-----+---+---+"),
          render(row("A", "B", "C"), body(row("one"))));
    }

    @Test
    void raggedRowsRemainRectangular() {
      assertEquals(
          expected(
              "+-----+-----+-------+",
              "|  A  |  B  |       |",
              "+-----+-----+-------+",
              "| one | two | three |",
              "|  x  |     |       |",
              "+-----+-----+-------+"),
          render(row("A", "B"), body(row("one", "two", "three"), row("x"))));
    }

    @Test
    void sixColumnApplicationTableEndsWithOneNewline() {
      String result =
          render(
              row("ID", "Company", "Role", "Applied Date", "Status", "URL"),
              body(row("1", "Acme", "Engineer", "2026-08-01", "APPLIED", "")));

      assertAll(
          () ->
              assertTrue(
                  result.contains("| ID | Company |   Role   | Applied Date | Status  | URL |")),
          () -> assertTrue(result.endsWith("\n")),
          () -> assertFalse(result.endsWith("\n\n")));
    }

    @Test
    void renderDoesNotMutateCallerCollections() {
      List<String> header = row(" A ", null);
      List<List<String>> rows = body(row("one"), row("two", " three ", "four"));
      List<String> headerSnapshot = new ArrayList<>(header);
      List<List<String>> rowsSnapshot = deepCopy(rows);

      render(header, rows);

      assertAll(() -> assertEquals(headerSnapshot, header), () -> assertEquals(rowsSnapshot, rows));
    }
  }

  @Nested
  @DisplayName("console create, list, and filter")
  class ConsoleReadTests {

    @Test
    void createAcceptsEveryField() {
      SessionResult result =
          session(
              script(
                  "0",
                  "  Acme  ",
                  " Engineer ",
                  "2026-08-01",
                  " Referral ",
                  " https://example.com/jobs/1 ",
                  "5"));
      Application created = result.service().listAll().getFirst();

      assertAll(
          () -> assertEquals("Acme", created.getCompany()),
          () -> assertEquals("Engineer", created.getRole()),
          () -> assertEquals(DATE, created.getAppliedDate()),
          () -> assertEquals(Status.APPLIED, created.getStatus()),
          () -> assertEquals("Referral", created.getNotes()),
          () -> assertEquals("https://example.com/jobs/1", created.getJobUrl()),
          () ->
              assertAppearsInOrder(
                  result.output(),
                  HEADER,
                  MENU,
                  "Company: ",
                  "Role: ",
                  "Applied date (YYYY-MM-DD): ",
                  "Notes (optional): ",
                  "Job URL (optional): ",
                  "Created application 1.\n",
                  MENU,
                  "Goodbye.\n"));
    }

    @Test
    void blankCreateDateDefaultsToToday() {
      LocalDate before = LocalDate.now();
      SessionResult result = session(script("0", "Acme", "Engineer", "", "", "", "5"));
      LocalDate after = LocalDate.now();
      Application created = result.service().listAll().getFirst();

      assertAll(
          () -> assertFalse(created.getAppliedDate().isBefore(before)),
          () -> assertFalse(created.getAppliedDate().isAfter(after)),
          () -> assertEquals(Status.APPLIED, created.getStatus()),
          () -> assertEquals(1, countOccurrences(result.output(), "Applied date (YYYY-MM-DD): ")),
          () -> assertFalse(result.output().contains("Enter a date as YYYY-MM-DD.")),
          () -> assertTrue(result.output().contains("Created application 1.\n")));
    }

    @Test
    void emptyAndPopulatedListsUseExplicitOutput() {
      SessionResult empty = session(script("1", "5"));
      SessionResult populated =
          session(
              script("1", "5"),
              service ->
                  service.create(
                      createRequest(
                          "Acme", "Engineer", DATE, "Hidden note", "https://example.com/jobs/1")));

      assertAll(
          () -> assertTrue(empty.output().contains("No applications exist to list.\n")),
          () ->
              assertTrue(
                  populated
                      .output()
                      .contains("| ID | Company |   Role   | Applied Date | Status  |")),
          () -> assertTrue(populated.output().contains("| 1  |  Acme   | Engineer |")),
          () -> assertTrue(populated.output().contains("https://example.com/jobs/1")),
          () -> assertFalse(populated.output().contains("Hidden note")));
    }

    @Test
    void statusFilteringNormalizesSpacesAndReportsNoMatches() {
      SessionResult result =
          session(
              script("2", "phone screen", "2", "offer", "5"),
              service -> {
                Application created = service.create("Acme", "Engineer", DATE);
                service.updateStatus(created.getId(), Status.PHONE_SCREEN);
              });

      assertAll(
          () -> assertTrue(result.output().contains("PHONE_SCREEN")),
          () -> assertTrue(result.output().contains("No applications found for status OFFER.\n")),
          () -> assertEquals(2, countOccurrences(result.output(), "Status: ")));
    }

    @Test
    void invalidStatusRetriesAndHyphenFormIsAccepted() {
      SessionResult result =
          session(
              script("2", "unknown", "phone-screen", "5"),
              service -> {
                Application created = service.create("Acme", "Engineer", DATE);
                service.updateStatus(created.getId(), Status.PHONE_SCREEN);
              });

      assertAll(
          () -> assertEquals(1, countOccurrences(result.output(), STATUS_ERROR)),
          () -> assertEquals(2, countOccurrences(result.output(), "Status: ")),
          () -> assertTrue(result.output().contains("PHONE_SCREEN")));
    }
  }

  @Nested
  @DisplayName("console edit and delete")
  class ConsoleWriteTests {

    @Test
    void fullEditShowsCurrentDataAndUpdatesEveryField() {
      SessionResult result =
          session(
              script(
                  "3",
                  "1",
                  "Globex",
                  "Senior Engineer",
                  "2026-08-05",
                  "offer",
                  "Final round",
                  "https://example.com/jobs/2",
                  "5"),
              Chapter01CapstoneTest::seedCompleteApplication);
      Application updated = result.service().findById(1L).orElseThrow();

      assertAll(
          () -> assertEquals("Globex", updated.getCompany()),
          () -> assertEquals("Senior Engineer", updated.getRole()),
          () -> assertEquals(LocalDate.of(2026, 8, 5), updated.getAppliedDate()),
          () -> assertEquals(Status.OFFER, updated.getStatus()),
          () -> assertEquals("Final round", updated.getNotes()),
          () -> assertEquals("https://example.com/jobs/2", updated.getJobUrl()),
          () ->
              assertAppearsInOrder(
                  result.output(),
                  "Current application\n",
                  "ID: 1\n",
                  "Company: Acme\n",
                  "Role: Engineer\n",
                  "Applied date: 2026-08-01\n",
                  "Status: APPLIED\n",
                  "Notes: Referral\n",
                  "URL: https://example.com/jobs/1\n",
                  "Company [Acme]: ",
                  "Role [Engineer]: ",
                  "Applied date [2026-08-01]: ",
                  "Status [APPLIED]: ",
                  "Notes [Referral] ('-' clears): ",
                  "Job URL [https://example.com/jobs/1] ('-' clears): ",
                  "Updated application 1.\n"));
    }

    @Test
    void blankEditKeepsRequiredValuesAndDashClearsOptionalValues() {
      SessionResult result =
          session(
              script("3", "1", "", "", "", "", "-", "-", "5"),
              Chapter01CapstoneTest::seedCompleteApplication);
      Application updated = result.service().findById(1L).orElseThrow();

      assertAll(
          () -> assertEquals("Acme", updated.getCompany()),
          () -> assertEquals("Engineer", updated.getRole()),
          () -> assertEquals(DATE, updated.getAppliedDate()),
          () -> assertEquals(Status.APPLIED, updated.getStatus()),
          () -> assertEquals("", updated.getNotes()),
          () -> assertEquals("", updated.getJobUrl()),
          () -> assertTrue(result.output().contains("Updated application 1.\n")));
    }

    @Test
    void invalidEditValuesRetryOnlyTheirPrompt() {
      SessionResult result =
          session(
              script(
                  "3",
                  "1",
                  "",
                  "",
                  "not-a-date",
                  "2026-08-02",
                  "not-a-status",
                  "interviewing",
                  "",
                  "ftp://example.com/job",
                  "https://example.com/job",
                  "5"),
              Chapter01CapstoneTest::seedCompleteApplication);
      Application updated = result.service().findById(1L).orElseThrow();

      assertAll(
          () -> assertEquals(1, countOccurrences(result.output(), "Enter a date as YYYY-MM-DD.\n")),
          () -> assertEquals(1, countOccurrences(result.output(), STATUS_ERROR)),
          () -> assertEquals(1, countOccurrences(result.output(), URL_ERROR)),
          () -> assertEquals(LocalDate.of(2026, 8, 2), updated.getAppliedDate()),
          () -> assertEquals(Status.INTERVIEWING, updated.getStatus()),
          () -> assertEquals("https://example.com/job", updated.getJobUrl()));
    }

    @Test
    void invalidAndMissingIdsDoNotCrashOrMutateState() {
      SessionResult result =
          session(
              script("3", "abc", "0", "-2", "99", "5"),
              Chapter01CapstoneTest::seedCompleteApplication);

      assertAll(
          () ->
              assertEquals(
                  3, countOccurrences(result.output(), "Enter a positive application ID.\n")),
          () -> assertTrue(result.output().contains("No application found with ID 99.\n")),
          () -> assertEquals("Acme", result.service().findById(1L).orElseThrow().getCompany()));
    }

    @Test
    void deleteSucceedsOnceAndThenReportsMissing() {
      SessionResult result =
          session(script("4", "1", "4", "1", "5"), Chapter01CapstoneTest::seedCompleteApplications);

      assertAll(
          () -> assertTrue(result.output().contains("Deleted application 1.\n")),
          () -> assertTrue(result.output().contains("No application found with ID 1.\n")),
          () -> assertEquals(1, result.service().listAll().size()));
    }
  }

  @Nested
  @DisplayName("console validation and lifecycle")
  class ConsoleValidationTests {

    @Test
    void invalidInputRetriesLocally() {
      SessionResult result =
          session(
              script(
                  "word",
                  "6",
                  "0",
                  "",
                  "Acme",
                  "",
                  "Engineer",
                  "bad-date",
                  "2026-08-01",
                  "",
                  "ftp://example.com/job",
                  "/relative",
                  "https://example.com/job",
                  "5"));

      assertAll(
          () -> assertEquals(2, countOccurrences(result.output(), "Enter a number from 0 to 5.\n")),
          () -> assertEquals(2, countOccurrences(result.output(), "Value is required.\n")),
          () -> assertEquals(1, countOccurrences(result.output(), "Enter a date as YYYY-MM-DD.\n")),
          () -> assertEquals(2, countOccurrences(result.output(), URL_ERROR)),
          () -> assertEquals(2, countOccurrences(result.output(), "Company: ")),
          () -> assertEquals(2, countOccurrences(result.output(), "Role: ")),
          () -> assertEquals(2, countOccurrences(result.output(), "Applied date (YYYY-MM-DD): ")),
          () -> assertEquals(3, countOccurrences(result.output(), "Job URL (optional): ")),
          () -> assertEquals(1, result.service().listAll().size()));
    }

    @Test
    void cleanQuitPrintsHeaderMenuAndGoodbyeExactly() {
      SessionResult result = session(script("5"));

      assertEquals(HEADER + MENU + "Goodbye.\n", result.output());
    }

    @Test
    void eofAtMenuExitsCleanly() {
      SessionResult result = session("");

      assertEquals(HEADER + MENU + "Goodbye.\n", result.output());
    }

    @Test
    void eofDuringCreateSavesNothing() {
      SessionResult result = session("0\nAcme\n");

      assertAll(
          () -> assertTrue(result.output().endsWith("Goodbye.\n")),
          () -> assertFalse(result.output().contains("Exception")),
          () -> assertTrue(result.service().listAll().isEmpty()));
    }

    @Test
    void eofDuringEditDoesNotSavePartialChanges() {
      SessionResult result =
          session("3\n1\nChanged Company\n", Chapter01CapstoneTest::seedCompleteApplication);
      Application application = result.service().findById(1L).orElseThrow();

      assertAll(
          () -> assertTrue(result.output().endsWith("Goodbye.\n")),
          () -> assertEquals("Acme", application.getCompany()),
          () -> assertEquals("Engineer", application.getRole()));
    }

    @Test
    void mainRunsToCleanEof() throws Exception {
      String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
      Process process =
          new ProcessBuilder(
                  java, "-cp", System.getProperty("java.class.path"), Main.class.getName())
              .redirectErrorStream(true)
              .start();

      try (OutputStream input = process.getOutputStream()) {
        input.write(new byte[0]);
      }

      if (!process.waitFor(5, TimeUnit.SECONDS)) {
        process.destroyForcibly();
        fail("Main did not exit within five seconds after EOF");
      }

      String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

      assertAll(
          () -> assertEquals(0, process.exitValue()),
          () -> assertEquals(HEADER + MENU + "Goodbye.\n", output),
          () -> assertFalse(output.contains("Exception")));
    }
  }

  private static Application sample(String company) {
    return new Application(company, "Engineer", DATE);
  }

  private static ApplicationService service() {
    return new ApplicationService(new InMemoryApplicationRepository());
  }

  private static CreateApplicationRequest createRequest(
      String company, String role, LocalDate appliedDate, String notes, String jobUrl) {
    return new CreateApplicationRequest(company, role, appliedDate, notes, jobUrl);
  }

  private static void seedCompleteApplication(ApplicationService service) {
    service.create(
        createRequest("Acme", "Engineer", DATE, "Referral", "https://example.com/jobs/1"));
  }

  private static void seedCompleteApplications(ApplicationService service) {
    service.create(
        createRequest("Acme", "Engineer", DATE, "Referral", "https://example.com/jobs/1"));
    service.create(
        createRequest(
            "Apple", "Developer", DATE, "Online Site", "https://panaple/com/fake/test/1"));
  }

  private static SessionResult session(String input) {
    return session(input, service -> {});
  }

  private static SessionResult session(String input, Consumer<ApplicationService> seed) {
    ByteArrayInputStream bytesIn = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    PrintStream output = new PrintStream(bytesOut, true, StandardCharsets.UTF_8);
    Scanner scanner = new Scanner(bytesIn, StandardCharsets.UTF_8);
    ApplicationService service = service();
    seed.accept(service);

    TextTable table = new TextTable();
    ConsoleView view = new ConsoleView(output, table);
    ConsolePrompter prompter = new ConsolePrompter(scanner, output);
    ConsoleApplication application = new ConsoleApplication(service, prompter, view);
    application.run();
    output.flush();

    return new SessionResult(bytesOut.toString(StandardCharsets.UTF_8), service);
  }

  private static String script(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private static List<String> row(String... values) {
    return new ArrayList<>(Arrays.asList(values));
  }

  @SafeVarargs
  private static List<List<String>> body(List<String>... rows) {
    List<List<String>> body = new ArrayList<>();
    for (List<String> row : rows) {
      body.add(row);
    }
    return body;
  }

  private static List<List<String>> deepCopy(List<List<String>> rows) {
    List<List<String>> copy = new ArrayList<>();
    for (List<String> row : rows) {
      copy.add(new ArrayList<>(row));
    }
    return copy;
  }

  private static String render(List<String> header, List<List<String>> rows) {
    return new TextTable().render(header, rows);
  }

  private static String expected(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private static int countOccurrences(String text, String section) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(section, index)) >= 0) {
      count++;
      index += section.length();
    }
    return count;
  }

  private static void assertAppearsInOrder(String output, String... sections) {
    int searchFrom = 0;
    for (String section : sections) {
      int index = output.indexOf(section, searchFrom);
      assertTrue(index >= 0, "Expected this output after index " + searchFrom + ":\n" + section);
      searchFrom = index + section.length();
    }
  }

  private static void assertOnlyPublicMethods(Class<?> type, Set<String> expectedNames) {
    Set<String> actualNames = new HashSet<>();
    for (Method method : type.getDeclaredMethods()) {
      if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
        actualNames.add(method.getName());
      }
    }
    assertEquals(expectedNames, actualNames, type.getSimpleName() + " public methods changed");
  }

  private static void assertPublicConstructor(Class<?> type, Class<?>... parameterTypes) {
    Constructor<?> constructor;
    try {
      constructor = type.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException exception) {
      fail(type.getSimpleName() + " is missing its documented constructor", exception);
      return;
    }
    assertTrue(
        Modifier.isPublic(constructor.getModifiers()),
        type.getSimpleName() + " constructor must be public");
  }

  private static void assertClassMissing(String className) {
    assertThrows(
        ClassNotFoundException.class,
        () -> Class.forName(className),
        className + " should be removed after the CLI refactor");
  }

  private record SessionResult(String output, ApplicationService service) {}

  private static final class RecordingRepository implements ApplicationRepository {
    private final List<String> calls = new ArrayList<>();
    private final Map<Long, Application> rows = new HashMap<>();

    @Override
    public Application save(Application application) {
      calls.add("save");
      if (application.getId() == null) {
        application.setId(42L);
      }
      rows.put(application.getId(), application);
      return application;
    }

    @Override
    public List<Application> findAll() {
      calls.add("findAll");
      return new ArrayList<>(rows.values());
    }

    @Override
    public Optional<Application> findById(Long id) {
      calls.add("findById");
      return Optional.ofNullable(rows.get(id));
    }

    @Override
    public List<Application> findByStatus(Status status) {
      calls.add("findByStatus");
      return rows.values().stream()
          .filter(application -> application.getStatus() == status)
          .toList();
    }

    @Override
    public boolean deleteById(Long id) {
      calls.add("deleteById");
      return rows.remove(id) != null;
    }
  }
}
