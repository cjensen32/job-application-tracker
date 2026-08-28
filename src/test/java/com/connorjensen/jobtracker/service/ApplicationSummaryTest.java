package com.connorjensen.jobtracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.connorjensen.jobtracker.model.Status;

@DisplayName("application summary")
class ApplicationSummaryTest {

  @Test
  void copiesCountsAtConstruction() {
    Map<Status, Long> expectedCountsByStatus =
        Map.of(
            Status.APPLIED, 1L,
            Status.PHONE_SCREEN, 0L,
            Status.INTERVIEWING, 0L,
            Status.OFFER, 2L,
            Status.REJECTED, 0L);

    ApplicationSummary testCountsByStatus =
        new ApplicationSummary(
            3L,
            Map.of(
                Status.APPLIED,
                1L,
                Status.OFFER,
                2L,
                Status.INTERVIEWING,
                0L,
                Status.PHONE_SCREEN,
                0L,
                Status.REJECTED,
                0L));

    Map<Status, Long> actualCountsByStatus = testCountsByStatus.countsByStatus();
    assertEquals(expectedCountsByStatus, actualCountsByStatus);
  }
}
