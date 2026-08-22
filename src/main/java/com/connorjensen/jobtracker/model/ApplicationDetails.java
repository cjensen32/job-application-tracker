package com.connorjensen.jobtracker.model;

import java.util.ArrayList;
import java.util.List;

public enum ApplicationDetails {
  ID("ID"),
  COMPANY("Company"),
  ROLE("Role"),
  APPLIED_DATE("Applied date"),
  STATUS("Status"),
  NOTES("Notes"),
  JOB_URL("URL");

  private final String label;

  ApplicationDetails(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String getValue(Application app) {
    return switch (this) {
      case ID -> String.valueOf(app.getId());
      case COMPANY -> app.getCompany();
      case ROLE -> app.getRole();
      case APPLIED_DATE -> String.valueOf(app.getAppliedDate());
      case STATUS -> app.getStatus().getLabel();
      case NOTES -> app.getNotes();
      case JOB_URL -> app.getJobUrl();
    };
  }

  public static List<String> getAllLabels() {
    List<String> labels = new ArrayList<>();

    for (ApplicationDetails detail : ApplicationDetails.values()) {
      if (detail != NOTES) {
        labels.add(detail.getLabel());
      }
    }
    return labels;
  }

  public static List<String> getAllValues(Application app) {
    List<String> values = new ArrayList<>();

    for (ApplicationDetails detail : ApplicationDetails.values()) {
      if (detail != NOTES) {
        values.add(detail.getValue(app));
      }
    }
    return values;
  }
}
