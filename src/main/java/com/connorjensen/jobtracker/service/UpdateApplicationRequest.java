package com.connorjensen.jobtracker.service;

import java.time.LocalDate;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

public class UpdateApplicationRequest {
  private final String company;
  private final String role;
  private final LocalDate appliedDate;
  public Status status;
  private String notes;
  private String jobUrl;

  public UpdateApplicationRequest(
      String company,
      String role,
      LocalDate appliedDate,
      Status status,
      String notes,
      String jobUrl) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.status = status;
    this.notes = notes;
    this.jobUrl = jobUrl;
  }

  public UpdateApplicationRequest(
      String company, String role, LocalDate appliedDate, Status status, String notes) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.status = status;
    this.notes = notes;
  }

  public UpdateApplicationRequest(
      String company, String role, LocalDate appliedDate, Status status) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
    this.status = status;
  }

  public UpdateApplicationRequest(String company, String role, LocalDate appliedDate) {
    this.company = company;
    this.role = role;
    this.appliedDate = appliedDate;
  }

  public Application update(Long id) {
    return null;
  }

  public Application update() {
    return null;
  }

  public Status status() {
    return this.status;
  }
}
