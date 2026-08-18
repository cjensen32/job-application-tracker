package com.connorjensen.jobtracker.dto;

import java.time.LocalDate;

public record ApplicationDto(String company, String role, LocalDate appliedDate) {}
