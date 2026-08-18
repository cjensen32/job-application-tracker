package com.connorjensen.jobtracker.service;

import java.time.LocalDate;

public class CreateApplicationRequest {
  private final String company;
  private final String role;
  private final LocalDate appliedDate;
  private String notes;
  private String jobUrl;

  public CreateApplicationRequest(
      String company, String role, LocalDate appliedDate, String notes, String jobUrl) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.notes = notes;
    this.jobUrl = jobUrl;
  }

  public CreateApplicationRequest(
      String company, String role, LocalDate appliedDate, String notes) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.notes = notes;
  }

  public CreateApplicationRequest(String company, String role, LocalDate appliedDate) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
  }

  public String company() {
    return this.company;
  }

  public String role() {
    return this.role;
  }

  public LocalDate appliedDate() {
    return this.appliedDate;
  }

  public String notes() {
    return this.notes;
  }

  public String jobUrl() {
    return this.jobUrl;
  }
}
