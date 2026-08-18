package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.cli.ConsoleApplication;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class Main {
  private Main() {}

  public static void main(String[] args) {
    // Activates the repository and service to store/manage apps
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    ConsoleApplication consoleTool = new ConsoleApplication(service);
    consoleTool.startCLI(service);
  }
}
