package com.connorjensen.jobtracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class Main {

  private Main() {}

  public static void main(String[] args) {
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    List<Application> applications = new ArrayList<>();
    applications.add(new Application("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1)));
    applications.add(new Application("Globex", "Platform Engineer", LocalDate.of(2026, 8, 5)));
    applications.add(new Application("Initech", "Java Developer", LocalDate.of(2026, 8, 9)));
    applications.get(1).setStatus(Status.INTERVIEWING);

    for (Application app : applications) {
      System.out.println(app.summary() + " [" + app.getStatus() + "]");
    }

    List<Application> interviewing =
        applications.stream().filter(app -> app.getStatus() == Status.INTERVIEWING).toList();

    Optional<Application> found =
        applications.stream().filter(app -> app.getCompany().equals("Globex")).findFirst();

    Optional<Application> missing =
        applications.stream().filter(app -> app.getCompany().equals("Nope Inc")).findFirst();
    System.out.println("missing: " + missing.map(Application::getCompany).orElse("not found"));

    System.out.println(
        "Found Globex entries: " + found.map(Application::getCompany).orElse("not found"));
    System.out.println("interviewing: " + interviewing.size());
  }
}
