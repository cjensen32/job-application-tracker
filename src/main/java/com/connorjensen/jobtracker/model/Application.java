package com.connorjensen.jobtracker.model;

import java.time.LocalDate;

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

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(Status status) {
      this.status = status;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }
    
    public String summary() {
        return company + " - " + role;
    }
}
