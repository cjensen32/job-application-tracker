package com.connorjensen.jobtracker.service;

import com.connorjensen.jobtracker.repository.ApplicationRepository;

public class ApplicationService {

  private final ApplicationRepository repository;

  public ApplicationService(ApplicationRepository repository) {
    this.repository = repository;
  }
}
