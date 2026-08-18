package com.connorjensen.jobtracker;

import java.time.LocalDate;

public record ApplicationDto(String company, String role, LocalDate appliedDate) {

}