package com.connorjensen.jobtracker.repository;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

  Application save(Application application);

  List<Application> findAll();

  Optional<Application> findById(Long id);

  List<Application> findByStatus(Status status);

  boolean deleteById(Long id);

}
