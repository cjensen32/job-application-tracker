package com.connorjensen.jobtracker.cli;

import java.util.ArrayList;
import java.util.List;

import com.connorjensen.jobtracker.util.Centering;

public class TextTable {

  private List<String> headerList;
  private List<List<String>> bodyRowsList;
  private List<Integer> columnWidthsList;

  public TextTable(List<String> headerList, List<List<String>> bodyRowsList) {
    this.headerList = headerList;
    this.bodyRowsList = bodyRowsList;
    this.columnWidthsList = new ArrayList<>();
    standardizeInputValues();
    setColumnWidths();
  }

  public TextTable() {}

  public void toTable() {
    if (this.columnWidthsList.isEmpty()) {
      return;
    }
    int columnCount = this.headerList.size();

    // Print top border
    printBorderLine();

    // Print header row
    System.out.print("|");
    for (int i = 0; i < columnCount; i++) {
      String header = this.headerList.get(i);
      System.out.print(Centering.center(header, this.columnWidthsList.get(i) + 2) + "|");
    }
    System.out.println();

    // print bottom border and return if bodyRowsList is empty
    printBorderLine();
    if (this.bodyRowsList.isEmpty()) {
      return;
    }

    // print body if bodyRowsList has entries/content
    for (List<String> strings : this.bodyRowsList) {
      System.out.print("|");

      for (int k = 0; k < strings.size(); k++) {
        String cellValue = strings.get(k);
        String centeredValue = Centering.center(cellValue, this.columnWidthsList.get(k) + 2);
        System.out.print(centeredValue + "|");
      }
      System.out.println();
    }
    printBorderLine();
  }

  public void setColumnWidths() {
    // Put all initial column widths in List
    for (int i = 0; i < this.headerList.size(); i++) {
      this.columnWidthsList.add(0);
      String value = this.headerList.get(i);
      int valueLength;
      if (value == null) {
        valueLength = 0;
      } else {
        valueLength = value.length();
      }
      this.columnWidthsList.set(i, valueLength);
    }

    // Try to find bigger widths
    for (List<String> rowList : this.bodyRowsList) {
      for (int i = 0; i < rowList.size(); i++) {
        String value = rowList.get(i);
        int currentLength;
        if (value == null) {
          currentLength = 0;
        } else {
          currentLength = value.length();
        }
        if (currentLength > this.columnWidthsList.get(i)) {
          this.columnWidthsList.set(i, currentLength);
        }
      }
    }
  }

  private void printBorderLine() {
    int columnWidth;
    for (Integer integer : this.columnWidthsList) {
      columnWidth = integer;
      System.out.print("+" + "-".repeat(columnWidth + 2));
    }
    System.out.print("+\n");
  }

  private void standardizeInputValues() {
    // Get max list length for rows
    int maxRowLength = 0;
    for (List<String> bodyRow : this.bodyRowsList) {
      if (bodyRow.size() > maxRowLength) {
        maxRowLength = bodyRow.size();
      }
    }

    // Don't care if lists are same size
    if (this.headerList.size() == maxRowLength) {
      return;
    }

    int maxSize = 0;
    if (this.headerList.size() > maxRowLength) {
      // headerList is bigger than the bodyList
      maxSize = this.headerList.size();
      for (List<String> bodyRowList : this.bodyRowsList) {
        while (bodyRowList.size() < maxSize) {
          bodyRowList.add("");
        }
      }
    } else {
      // headerList is smaller than the BodyList
      maxSize = maxRowLength;
      while (this.headerList.size() < maxSize) {
        this.headerList.add("");
      }
    }
  }
}
