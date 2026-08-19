package com.connorjensen.jobtracker.cli;

import java.util.ArrayList;
import java.util.List;

import com.connorjensen.jobtracker.util.Centering;

public class TextTable {

  public TextTable() {}

  public String render(List<String> headerList, List<List<String>> bodyRowsList) {
    standardizeValues(headerList, bodyRowsList); // Fixes malformed data
    List<Integer> columnWidthsList = getColumnWidths(headerList, bodyRowsList);
    int columnCount = headerList.size();
    StringBuilder tableBuilder = new StringBuilder();

    // Print top border
    tableBuilder.append(buildBorder(columnWidthsList));

    // Print header row
    tableBuilder.append("|");
    for (int i = 0; i < columnCount; i++) {
      String header = headerList.get(i);
      tableBuilder.append(Centering.center(header, columnWidthsList.get(i) + 2));
      tableBuilder.append("|");
    }
    tableBuilder.append("\n");

    // print bottom border and return if bodyRowsList is empty
    tableBuilder.append(buildBorder(columnWidthsList));
    if (bodyRowsList.isEmpty()) {
      return tableBuilder.toString();
    }

    // print body if bodyRowsList has entries/content
    for (List<String> strings : bodyRowsList) {
      tableBuilder.append("|");

      for (int k = 0; k < strings.size(); k++) {
        String cellValue = strings.get(k);
        String centeredValue = Centering.center(cellValue, columnWidthsList.get(k) + 2);
        tableBuilder.append(centeredValue).append("|");
      }
      tableBuilder.append("\n");
    }
    tableBuilder.append(buildBorder(columnWidthsList));

    return tableBuilder.toString();
  }

  public List<Integer> getColumnWidths(List<String> headerList, List<List<String>> bodyRowsList) {
    // Put all initial column widths in List
    List<Integer> columnWidthsList = new ArrayList<>();
    for (int i = 0; i < headerList.size(); i++) {
      columnWidthsList.add(0);
      String value = headerList.get(i);
      int valueLength;
      if (value == null) {
        valueLength = 0;
      } else {
        valueLength = value.length();
      }
      columnWidthsList.set(i, valueLength);
    }

    // Try to find bigger widths
    for (List<String> rowList : bodyRowsList) {
      for (int i = 0; i < rowList.size(); i++) {
        String value = rowList.get(i);
        int currentLength;
        if (value == null) {
          currentLength = 0;
        } else {
          currentLength = value.length();
        }
        if (currentLength > columnWidthsList.get(i)) {
          columnWidthsList.set(i, currentLength);
        }
      }
    }
    return columnWidthsList;
  }

  public String buildBorder(List<Integer> columnWidthsList) {
    StringBuilder borderBuilder = new StringBuilder();
    int width;

    for (Integer currWidth : columnWidthsList) {
      width = currWidth;
      borderBuilder.append("+").repeat("-", width + 2);
    }
    borderBuilder.append("+\n");

    return borderBuilder.toString();
  }

  private void standardizeValues(List<String> headerList, List<List<String>> bodyRowsList) {
    // Get max list length for rows
    int maxRowLength = 0;
    for (List<String> bodyRow : bodyRowsList) {
      if (bodyRow.size() > maxRowLength) {
        maxRowLength = bodyRow.size();
      }
    }
    // return early if lists are same size
    if (headerList.size() == maxRowLength) {
      return;
    }

    int maxSize = 0;
    if (headerList.size() > maxRowLength) {
      // case: headerList is bigger than the bodyList
      maxSize = headerList.size();
      for (List<String> bodyRowList : bodyRowsList) {
        while (bodyRowList.size() < maxSize) {
          bodyRowList.add("");
        }
      }
    } else {
      // case: headerList is smaller than the BodyList
      maxSize = maxRowLength;
      while (headerList.size() < maxSize) {
        headerList.add("");
      }
    }
  }
}
