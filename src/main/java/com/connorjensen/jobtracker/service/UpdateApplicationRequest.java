package com.connorjensen.jobtracker.service;

import java.time.LocalDate;

import com.connorjensen.jobtracker.model.Status;

public record UpdateApplicationRequest(
    String company,
    String role,
    LocalDate appliedDate,
    Status status,
    String notes,
    String jobUrl) {}
