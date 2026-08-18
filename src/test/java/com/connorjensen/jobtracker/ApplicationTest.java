package com.connorjensen.jobtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.connorjensen.jobtracker.dto.ApplicationDto;
import com.connorjensen.jobtracker.model.Application;

class ApplicationTest {

  private static final LocalDate DATE = LocalDate.of(2026, 8, 1);

  @Test
  void twoClassesWithIdenticalValuesAreNotEqual() {
    Application a = new Application("Acme Corp", "Backend Engineer", DATE);
    Application b = new Application("Acme Corp", "Backend Engineer", DATE);

    assertNotEquals(a, b);
  }

  @Test
  void twoRecordsWithIdenticalValuesAreEqual() {
    ApplicationDto a = new ApplicationDto("Acme Corp", "Backend Engineer", DATE);
    ApplicationDto b = new ApplicationDto("Acme Corp", "Backend Engineer", DATE);

    assertEquals(a, b);
  }
}
