package com.connorjensen.jobtracker.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

public class InMemoryApplicationRepository implements ApplicationRepository {

  private final Map<Long, Application> storage = new HashMap<>();
  private long nextId = 1;

  @Override
  public Application save(Application application) {
    if (application.getId() == null) {
      application.setId(nextId);
      nextId++;
    }
    storage.put(application.getId(), application);
    return application;
  }

  @Override
  public List<Application> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Optional<Application> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Application> findByStatus(Status status) {
    return storage.values().stream().filter(app -> app.getStatus() == status).toList();
  }

  @Override
  public boolean deleteById(Long id) {
    if (storage.containsKey(id)) {
      storage.remove(id);
      return true;
    }
    return false;
  }
}
