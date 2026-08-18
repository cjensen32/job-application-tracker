package com.connorjensen.jobtracker.model;

public enum Status {
  APPLIED("Applied"),
  PHONE_SCREEN("Phone Screen"),
  INTERVIEWING("Interviewing"),
  OFFER("Offer"),
  REJECTED("Rejected");

  private final String label;

  Status(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
