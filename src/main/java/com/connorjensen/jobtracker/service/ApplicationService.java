package com.connorjensen.jobtracker.service;

import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ApplicationService {

    private final ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public Application create(String company, String role, LocalDate appliedDate) {
        return repository.save(new Application(company, role, appliedDate));
    }

    public List<Application> listAll() {
        return repository.findAll();
    }

    public Optional<Application> findById(Long id) {
        return repository.findById(id);
    }

    public List<Application> listByStatus(Status status) {
        return repository.findByStatus(status);
    }

    public Application updateStatus(Long id, Status status) {
        Application app = repository.findById(id)
                .orElseThrow(() -> (
                        new IllegalArgumentException("No Application found with ID: " + id)
                ));

        app.setStatus(status);
        return repository.save(app);
    }
    public boolean delete(Long id) {
        return repository.deleteById(id);
    }

    public List<String> getLabels() {
      return Application.toLabelsList();
    };
}
