package com.connorjensen.jobtracker.service;

import java.time.LocalDate;

public record CreateApplicationRequest(
    String company, String role, LocalDate appliedDate, String notes, String jobUrl) {}
