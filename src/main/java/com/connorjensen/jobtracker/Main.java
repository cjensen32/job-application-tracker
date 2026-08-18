package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.util.ConsoleExperience;

public class Main {

  public static void main(String[] args) {
    // Activates the repository and service to store/manage apps
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    ConsoleExperience consoleTool = new ConsoleExperience(service);
    consoleTool.startCLI(service);
    // Print first time statements
  }
}
