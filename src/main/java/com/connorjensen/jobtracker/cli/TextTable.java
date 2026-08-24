package com.connorjensen.jobtracker.cli;

import java.util.ArrayList;
import java.util.List;

public class TextTable {

  public TextTable() {}

  public String render(List<String> headerList, List<List<String>> bodyRowsList) {
    List<Integer> columnWidthsList = getColumnWidths(headerList, bodyRowsList);
    int columnCount = columnWidthsList.size();
    if (columnCount == 0) {
      return "";
    }

    StringBuilder tableBuilder = new StringBuilder();

    // Print top border
    tableBuilder.append(buildBorder(columnWidthsList));

    // Print header row
    tableBuilder.append("|");
    for (int i = 0; i < columnCount; i++) {
      String header;
      if (headerList.size() > i) {
        header = headerList.get(i);
      } else {
        header = " ".repeat(columnWidthsList.get(i));
      }
      tableBuilder.append(centerText(header, columnWidthsList.get(i)));
      tableBuilder.append("|");
    }
    tableBuilder.append("\n");

    // print bottom border and return if bodyRowsList is empty
    tableBuilder.append(buildBorder(columnWidthsList));
    if (bodyRowsList.isEmpty()) {
      return tableBuilder.toString();
    }

    // print body if bodyRowsList has entries/content
    for (List<String> rowValues : bodyRowsList) {
      tableBuilder.append("|");

      for (int k = 0; k < columnCount; k++) {
        String cellValue;
        if (k < rowValues.size()) {
          cellValue = rowValues.get(k);
        } else {
          cellValue = " ".repeat(columnWidthsList.get(k));
        }
        String centeredValue = centerText(cellValue, columnWidthsList.get(k));
        tableBuilder.append(centeredValue).append("|");
      }
      tableBuilder.append("\n");
    }
    tableBuilder.append(buildBorder(columnWidthsList));

    return tableBuilder.toString();
  }

  private List<Integer> getColumnWidths(List<String> headerList, List<List<String>> bodyRowsList) {
    // Put all initial column widths in List
    List<Integer> columnWidthsList = new ArrayList<>();
    for (String s : headerList) {
      if (s != null) {
        columnWidthsList.add(s.strip().length());
      } else {
        columnWidthsList.add(0);
      }
    }

    for (List<String> bodyRow : bodyRowsList) {
      for (int i = 0; i < bodyRow.size(); i++) {
        int cellLength;
        if (bodyRow.get(i) != null) {
          cellLength = bodyRow.get(i).strip().length();
          if (i >= columnWidthsList.size()) {
            columnWidthsList.add(cellLength);
          } else if (cellLength > columnWidthsList.get(i)) {
            columnWidthsList.set(i, cellLength);
          }
        }
      }
    }
    return columnWidthsList;
  }

  private String buildBorder(List<Integer> columnWidthsList) {
    StringBuilder borderBuilder = new StringBuilder();
    int width;

    for (Integer currWidth : columnWidthsList) {
      width = currWidth;
      borderBuilder.append("+").repeat("-", width + 2);
    }
    borderBuilder.append("+\n");

    return borderBuilder.toString();
  }

  private static String centerText(String text, int width) {
    if (text == null) {
      text = "";
    } else {
      text = text.strip();
    }

    int padding = width + 2 - text.length();
    if (padding <= 0) {
      return text;
    }

    int left = padding / 2;
    return " ".repeat(left) + text + " ".repeat(padding - left);
  }
}
