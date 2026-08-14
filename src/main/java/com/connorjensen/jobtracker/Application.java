package com.connorjensen.jobtracker;

import java.time.LocalDate;

public class Application {
    private String company;
    private String role;
    private LocalDate appliedDate;

    public Application(String company, String role, LocalDate appliedDate) {
        this.company = company;
        this.role = role;
        this.appliedDate = appliedDate;
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

    public void setRole(String role) {
        this.role = role;
    }

    public String summary() {
        return company + " - " + role;
    }
}