package com.connorjensen.jobtracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
  private Long id;
  private String company;
  private String role;
  private LocalDate appliedDate;
  private Status status;
  private String notes;
  private String jobUrl;

  public Application(String company, String role, LocalDate appliedDate) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.status = Status.APPLIED;
    this.jobUrl = "";
  }

  public Long getId() {
    return id;
  }

  public String getCompany() {
    return company;
  }

  public String getRole() {
    return role;
  }

  public LocalDate getAppliedDate() {
    return appliedDate;
  }

  public Status getStatus() {
    return status;
  }

  public String getNotes() {
    return notes;
  }

  public String getJobUrl() {
    return jobUrl;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setCompany(String company) { this.company = company; }

  public void setRole(String role) {
    this.role = role;
  }

  public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setJobUrl(String jobUrl) {
    this.jobUrl = jobUrl;
  }

  @Override
  public String toString() {
    return company + " - " + role + " [" + status + "] ";
  }

  public List<String> toValuesList() {
    return new ArrayList<>(
      List.of(
        String.valueOf(this.id),
        this.company,
        this.role,
        String.valueOf(this.appliedDate),
        this.status.getLabel(),
        this.jobUrl
      )
    );
  }

  public static List<String> toLabelsList() {
    return new ArrayList<>(
      List.of(
        "ID",
        "Company",
        "Role",
        "Applied Date",
        "Status",
        "URL"
      ));
  }


}
