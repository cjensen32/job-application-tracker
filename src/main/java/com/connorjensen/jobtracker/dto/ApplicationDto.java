package com.connorjensen.jobtracker.dto;

import com.connorjensen.jobtracker.model.Status;

import java.time.LocalDate;

public record ApplicationDto(String company, String role, LocalDate appliedDate) {

}
