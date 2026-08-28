package com.connorjensen.jobtracker.service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.connorjensen.jobtracker.model.Status;

public record ApplicationSummary(long total, Map<Status, Long> countsByStatus) {
  public ApplicationSummary {
    EnumMap<Status, Long> copy = new EnumMap<>(Status.class);
    copy.putAll(countsByStatus);
    countsByStatus = Collections.unmodifiableMap(copy);
  }
}
