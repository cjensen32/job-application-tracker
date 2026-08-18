package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.util.ApplicationTextTable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextTableTest {

  private static List<String> row(String... values) {
    return new ArrayList<>(Arrays.asList(values));
  }

  @SafeVarargs
  private static List<List<String>> body(List<String>... rows) {
    List<List<String>> values = new ArrayList<>();
    Collections.addAll(values, rows);
    return values;
  }

  private static String expected(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  private static String render(List<String> header, List<List<String>> rows) {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream capturedOut = new PrintStream(buffer, true, StandardCharsets.UTF_8);

    try {
      System.setOut(capturedOut);
      new ApplicationTextTable(header, rows).toTable();
    } finally {
      capturedOut.flush();
      System.setOut(originalOut);
      capturedOut.close();
    }

    return buffer.toString(StandardCharsets.UTF_8);
  }

  @Test
  void headerControlsColumnWidths() {
    assertEquals(expected(
        "+---------+------+--------+",
        "| Company | Role | Status |",
        "+---------+------+--------+",
        "|  Acme   | Dev  |  Open  |",
        "+---------+------+--------+"),
      render(
        row("Company", "Role", "Status"),
        body(row("Acme", "Dev", "Open"))));
  }

  @Test
  void bodyControlsColumnWidthsAcrossMultipleRows() {
    assertEquals(expected(
        "+-----------------+--------------------------+----------+",
        "|       Co        |           Role           |    St    |",
        "+-----------------+--------------------------+----------+",
        "| Shyft Solutions | Software Engineer Intern | Applied  |",
        "|      Acme       |           Dev            | Rejected |",
        "+-----------------+--------------------------+----------+"),
      render(
        row("Co", "Role", "St"),
        body(
          row("Shyft Solutions", "Software Engineer Intern", "Applied"),
          row("Acme", "Dev", "Rejected"))));
  }

  @Test
  void widestValueVariesByColumn() {
    assertEquals(expected(
        "+------------+--------------+----------------+------+",
        "|     A      |      B       |       C        |  D   |",
        "+------------+--------------+----------------+------+",
        "| xxxxxxxxxx |      y       |       zz       |  w   |",
        "|     x      | yyyyyyyyyyyy |       z        | wwww |",
        "|     xx     |      yy      | zzzzzzzzzzzzzz |  ww  |",
        "+------------+--------------+----------------+------+"),
      render(
        row("A", "B", "C", "D"),
        body(
          row("xxxxxxxxxx", "y", "zz", "w"),
          row("x", "yyyyyyyyyyyy", "z", "wwww"),
          row("xx", "yy", "zzzzzzzzzzzzzz", "ww"))));
  }

  @Test
  void nullCellsRenderAsBlankCells() {
    assertEquals(expected(
        "+---------+----------+--------+",
        "| Company |          | Status |",
        "+---------+----------+--------+",
        "|  Acme   |          |        |",
        "|         | Engineer |  Open  |",
        "+---------+----------+--------+"),
      render(
        row("Company", null, "Status"),
        body(
          row("Acme", null, null),
          row(null, "Engineer", "Open"))));
  }

  @Test
  void emptyStringsRenderAsMinimumWidthCells() {
    assertEquals(expected(
        "+--+--+",
        "|  |  |",
        "+--+--+",
        "|  |  |",
        "+--+--+"),
      render(row("", ""), body(row("", ""))));
  }

  @Test
  void surroundingWhitespaceIsTrimmedBeforeCentering() {
    assertEquals(expected(
        "+----------+------+",
        "| Company  | Role |",
        "+----------+------+",
        "|   Acme   | Dev  |",
        "|  Globex  | SRE  |",
        "+----------+------+"),
      render(
        row("Company", "Role"),
        body(
          row("  Acme  ", " Dev"),
          row("Globex", "SRE "))));
  }

  @Test
  void emptyBodyPrintsHeaderAndBottomBorder() {
    assertEquals(expected(
        "+---------+------+",
        "| Company | Role |",
        "+---------+------+"),
      render(row("Company", "Role"), new ArrayList<>()));
  }

  @Test
  void completelyEmptyInputPrintsNothing() {
    assertEquals("", render(new ArrayList<>(), new ArrayList<>()));
  }

  @Test
  void headerShorterThanBodyGainsBlankColumns() {
    assertEquals(expected(
        "+-----+-----+",
        "|  A  |     |",
        "+-----+-----+",
        "| one | two |",
        "+-----+-----+"),
      render(row("A"), body(row("one", "two"))));
  }

  @Test
  void headerLongerThanBodyPadsTheBodyRow() {
    assertEquals(expected(
        "+-----+---+---+",
        "|  A  | B | C |",
        "+-----+---+---+",
        "| one |   |   |",
        "+-----+---+---+"),
      render(row("A", "B", "C"), body(row("one"))));
  }

  @Test
  void raggedRowsRemainRectangular() {
    assertEquals(expected(
        "+-----+-----+-------+",
        "|  A  |  B  |       |",
        "+-----+-----+-------+",
        "| one | two | three |",
        "|  x  |     |       |",
        "+-----+-----+-------+"),
      render(
        row("A", "B"),
        body(
          row("one", "two", "three"),
          row("x"))));
  }

  @Test
  void singleColumnSupportsMultipleRowsAndOddPadding() {
    assertEquals(expected(
        "+--------------+",
        "|    Status    |",
        "+--------------+",
        "|   Applied    |",
        "| Interviewing |",
        "|      No      |",
        "+--------------+"),
      render(
        row("Status"),
        body(row("Applied"), row("Interviewing"), row("No"))));
  }

  @Test
  void applicationStyleTableUsesAllSixColumns() {
    assertEquals(expected(
        "+----+-----------------+--------------------------+--------------+---------+-----+",
        "| ID |     Company     |           Role           | Applied Date | Status  | URL |",
        "+----+-----------------+--------------------------+--------------+---------+-----+",
        "| 1  | Shyft Solutions | Software Engineer Intern |  2026-08-16  | Applied |     |",
        "+----+-----------------+--------------------------+--------------+---------+-----+"),
      render(
        row("ID", "Company", "Role", "Applied Date", "Status", "URL"),
        body(row(
          "1", "Shyft Solutions", "Software Engineer Intern",
          "2026-08-16", "Applied", ""))));
  }
}
