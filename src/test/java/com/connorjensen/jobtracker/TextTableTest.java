package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.util.ApplicationTextTable;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextTableTest {

  /** Set by the first case that throws. Once non-null, every later case is aborted. */
  private static Throwable firstFailure;
  private static String firstFailureCase;

  @BeforeEach
  void haltIfAlreadyFailed() {
    Assumptions.assumeTrue(
      firstFailure == null,
      () -> "HALTED - '" + firstFailureCase + "' threw "
        + firstFailure.getClass().getName() + "; remaining cases skipped.");
  }

  // ---------- builders ----------

  private static List<String> row(String... values) {
    return new ArrayList<>(Arrays.asList(values));
  }

  @SafeVarargs
  private static List<List<String>> body(List<String>... rows) {
    List<List<String>> list = new ArrayList<>();
    Collections.addAll(list, rows);
    return list;
  }

  /** Joins hand-written expected lines. Keeps exact spacing intact. */
  private static String expected(String... lines) {
    return String.join("\n", lines);
  }

  // ---------- input rendering ----------

  private static String literal(String value) {
    if (value == null) {
      return "null";
    }
    return "\"" + value
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\t", "\\t")
      .replace("\n", "\\n")
      .replace("\r", "\\r") + "\"";
  }

  private static String renderHeader(List<String> header) {
    if (header.isEmpty()) {
      return "List<String> headerList = new ArrayList<>();";
    }
    StringBuilder cells = new StringBuilder();
    for (int i = 0; i < header.size(); i++) {
      cells.append(literal(header.get(i)));
      if (i + 1 < header.size()) {
        cells.append(", ");
      }
    }
    return "List<String> headerList = new ArrayList<>(Arrays.asList(" + cells + "));";
  }

  private static String renderBody(List<List<String>> rows) {
    if (rows.isEmpty()) {
      return "List<List<String>> bodyRowsList = new ArrayList<>();";
    }
    StringBuilder sb =
      new StringBuilder("List<List<String>> bodyRowsList = new ArrayList<>(List.of(\n");
    for (int r = 0; r < rows.size(); r++) {
      List<String> rowValues = rows.get(r);
      StringBuilder cells = new StringBuilder();
      for (int i = 0; i < rowValues.size(); i++) {
        cells.append(literal(rowValues.get(i)));
        if (i + 1 < rowValues.size()) {
          cells.append(", ");
        }
      }
      sb.append("    new ArrayList<>(Arrays.asList(").append(cells).append("))");
      sb.append(r + 1 < rows.size() ? ",\n" : "\n");
    }
    return sb.append("));").toString();
  }

  // ---------- runner ----------

  private void report(String caseName,
                      List<String> header,
                      List<List<String>> rows,
                      String expectedOutput) {

    // Render inputs before the class has a chance to mutate the lists.
    String headerText = renderHeader(header);
    String bodyText = renderBody(rows);

    PrintStream realOut = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    String actual;
    Throwable failure = null;

    try {
      System.setOut(new PrintStream(buffer, true));
      new ApplicationTextTable(header, rows).toTable();
    } catch (Throwable t) {
      failure = t;
    } finally {
      System.setOut(realOut);
      actual = buffer.toString();
    }

    System.out.println();
    System.out.println("======================================= " + caseName + " ======");
    System.out.println();
    System.out.println("**Inputs:**");
    System.out.println("```java");
    System.out.println(headerText);
    System.out.println();
    System.out.println(bodyText);
    System.out.println("```");
    System.out.println();
    System.out.println("**Expected Output:**");
    System.out.println(expectedOutput);
    System.out.println();
    System.out.println("**Actual Output:**");
    if (actual.isEmpty()) {
      System.out.println("(nothing printed)");
    } else {
      System.out.print(actual);
      if (!actual.endsWith("\n")) {
        System.out.println("   <-- no trailing newline");
      }
    }

    if (failure != null) {
      System.out.println();
      System.out.println("**Stack Trace:**");
      System.out.println("```");
      // printStackTrace walks the full chain: causes ("Caused by: ...")
      // and any suppressed exceptions.
      failure.printStackTrace(System.out);
      System.out.println("```");
      System.out.println();
      System.out.println("!! " + failure.getClass().getName()
        + (failure.getMessage() == null ? "" : ": " + failure.getMessage()));
      System.out.println("!! Halting run - remaining cases will be skipped.");
      System.out.println();
      System.out.flush();

      firstFailure = failure;
      firstFailureCase = caseName;

      throw new AssertionError(
        "Case '" + caseName + "' threw " + failure.getClass().getName(), failure);
    }

    System.out.println();
    System.out.flush();
  }

  // ---------- cases ----------

  @Test
  @Order(1)
  @DisplayName("Header widest")
  void headerWidest() {
    report("Header widest",
      row("Company", "Role", "Status"),
      body(row("Acme", "Dev", "Open")),
      expected(
        "+---------+------+--------+",
        "| Company | Role | Status |",
        "+---------+------+--------+",
        "|  Acme   | Dev  |  Open  |",
        "+---------+------+--------+"
      ));
  }

  @Test
  @Order(2)
  @DisplayName("Body widest")
  void bodyWidest() {
    report("Body widest",
      row("Co", "Role", "St"),
      body(
        row("Shyft Solutions", "Software Engineer Intern", "Applied"),
        row("Acme", "Dev", "Rejected")
      ),
      expected(
        "+-----------------+--------------------------+----------+",
        "|       Co        |           Role           |    St    |",
        "+-----------------+--------------------------+----------+",
        "| Shyft Solutions | Software Engineer Intern | Applied  |",
        "|      Acme       |           Dev            | Rejected |",
        "+-----------------+--------------------------+----------+"
      ));
  }

  @Test
  @Order(3)
  @DisplayName("Widest value varies by column")
  void widestVariesByColumn() {
    report("Widest value varies by column",
      row("A", "B", "C", "D"),
      body(
        row("xxxxxxxxxx", "y", "zz", "w"),
        row("x", "yyyyyyyyyyyy", "z", "wwww"),
        row("xx", "yy", "zzzzzzzzzzzzzz", "ww")
      ),
      expected(
        "+------------+--------------+----------------+------+",
        "|     A      |      B       |       C        |  D   |",
        "+------------+--------------+----------------+------+",
        "| xxxxxxxxxx |      y       |       zz       |  w   |",
        "|     x      | yyyyyyyyyyyy |       z        | wwww |",
        "|     xx     |      yy      | zzzzzzzzzzzzzz |  ww  |",
        "+------------+--------------+----------------+------+"
      ));
  }

  @Test
  @Order(4)
  @DisplayName("Nulls in header and body")
  void nullsInHeaderAndBody() {
    report("Nulls in header and body",
      row("Company", null, "Status"),
      body(
        row("Acme", null, null),
        row(null, "Engineer", "Open")
      ),
      expected(
        "+---------+----------+--------+",
        "| Company |          | Status |",
        "+---------+----------+--------+",
        "|  Acme   |          |        |",
        "|         | Engineer |  Open  |",
        "+---------+----------+--------+"
      ));
  }

  @Test
  @Order(5)
  @DisplayName("Empty strings only")
  void emptyStringsOnly() {
    report("Empty strings only",
      row("", ""),
      body(row("", "")),
      expected(
        "+--+--+",
        "|  |  |",
        "+--+--+",
        "|  |  |",
        "+--+--+"
      ));
  }

  @Test
  @Order(6)
  @DisplayName("Values with leading/trailing spaces")
  void valuesWithSurroundingSpaces() {
    report("Values with leading/trailing spaces",
      row("Company", "Role"),
      body(
        row("  Acme  ", " Dev"),
        row("Globex", "SRE ")
      ),
      expected(
        "+----------+------+",
        "| Company  | Role |",
        "+----------+------+",
        "|   Acme   |  Dev |",
        "|  Globex  | SRE  |",
        "+----------+------+"
      ));
  }

  @Test
  @Order(7)
  @DisplayName("Ragged rows (body has a column the header doesn't)")
  void raggedRows() {
    report("Ragged rows",
      row("A", "B"),
      body(
        row("one", "two", "three"),
        row("x")
      ),
      expected(
        "+-----+-----+-------+",
        "|  A  |  B  |       |",
        "+-----+-----+-------+",
        "| one | two | three |",
        "|  x  |     |       |",
        "+-----+-----+-------+"
      ));
  }

  @Test
  @Order(8)
  @DisplayName("Empty body")
  void emptyBody() {
    report("Empty body",
      row("Company", "Role"),
      new ArrayList<>(),
      expected(
        "+---------+------+",
        "| Company | Role |",
        "+---------+------+"
      ));
  }

  @Test
  @Order(9)
  @DisplayName("Single column")
  void singleColumn() {
    report("Single column",
      row("Status"),
      body(
        row("Applied"),
        row("Interviewing"),
        row("No")
      ),
      expected(
        "+--------------+",
        "|    Status    |",
        "+--------------+",
        "|   Applied    |",
        "| Interviewing |",
        "|      No      |",
        "+--------------+"
      ));
  }

  @Test
  @Order(10)
  @DisplayName("Six columns, wildly mixed widths")
  void sixColumnsMixedWidths() {
    report("Six columns, wildly mixed widths",
      row("ID", "Company", "Role", "Location", "Status", "Notes"),
      body(
        row("1", "Shyft Solutions", "Software Developer Intern",
          "Offutt AFB, NE", "Completed", "TADS project"),
        row("2", "A", "B", "C", "D", "E"),
        row("3", "Globex Corporation International", "SRE", "Omaha",
          "Interviewing", "Third round scheduled for next Tuesday")
      ),
      expected(
        "+----+----------------------------------+---------------------------+----------------+--------------+----------------------------------------+",
        "| ID |             Company              |           Role            |    Location    |    Status    |                 Notes                  |",
        "+----+----------------------------------+---------------------------+----------------+--------------+----------------------------------------+",
        "| 1  |         Shyft Solutions          | Software Developer Intern | Offutt AFB, NE |  Completed   |              TADS project              |",
        "| 2  |                A                 |             B             |       C        |      D       |                   E                    |",
        "| 3  | Globex Corporation International |            SRE            |     Omaha      | Interviewing | Third round scheduled for next Tuesday |",
        "+----+----------------------------------+---------------------------+----------------+--------------+----------------------------------------+"
      ));
  }
}