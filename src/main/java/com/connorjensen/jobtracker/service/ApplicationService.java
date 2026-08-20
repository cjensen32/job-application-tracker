package com.connorjensen.jobtracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;

public class ApplicationService {

  private final ApplicationRepository repository;

  public ApplicationService(ApplicationRepository repository) {
    this.repository = repository;
  }

  public Application create(String company, String role, LocalDate appliedDate) {
    return this.repository.save(new Application(company, role, appliedDate));
  }

  public Application create(
      String company, String role, LocalDate appliedDate, String notes, String jobUrl) {
    return this.repository.save(new Application(company, role, appliedDate, notes, jobUrl));
  }

  public List<Application> listAll() {
    return this.repository.findAll();
  }

  public Optional<Application> findById(Long id) {
    return this.repository.findById(id);
  }

  public List<Application> listByStatus(Status status) {
    return this.repository.findByStatus(status);
  }

  public Application updateStatus(Long id, Status status) {
    Application app =
        this.repository
            .findById(id)
            .orElseThrow(
                () -> (new IllegalArgumentException("No Application found with ID: " + id)));

    app.setStatus(status);
    return this.repository.save(app);
  }

  public boolean delete(Long id) {
    return this.repository.deleteById(id);
  }
}
