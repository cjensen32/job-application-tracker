package com.connorjensen.jobtracker.repository;

import java.util.List;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.service.UpdateApplicationRequest;

public interface ApplicationRepository {

  Application save(Application application);

  Application update(Long id, UpdateApplicationRequest request);

  List<Application> findAll();

  Optional<Application> findById(Long id);

  List<Application> findByStatus(Status status);

  boolean deleteById(Long id);
}
