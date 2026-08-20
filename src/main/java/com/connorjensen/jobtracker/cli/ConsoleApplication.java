package com.connorjensen.jobtracker.cli;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.service.ApplicationService;

public class ConsoleApplication {
  private final ApplicationService service;
  private final ConsolePrompter prompter;
  private final ConsoleView view;

  public ConsoleApplication(
      ApplicationService service, ConsolePrompter prompter, ConsoleView view) {
    this.service = service;
    this.prompter = prompter;
    this.view = view;
  }

  public void run() {
    boolean running = true;
    Optional<Integer> selection;

    this.view.showHeader();
    while (running) {
      this.view.showMenu();
      selection = this.prompter.promptMenuSelection();
      if (selection.isPresent()) {
        running = dispatch(selection.get());
      }
    }
    this.view.showGoodbye();
  }

  private boolean dispatch(int selection) {
    return switch (selection) {
      case 0 -> createApplication();
      case 1 -> listApplications();
      case 2 -> filterApplications();
      case 3 -> editApplication(); // TODO: Need to complete
      case 4 -> deleteApplication(); // TODO: Need to complete
      case 5 -> false;
      default -> throw new IllegalStateException("validated selection escaped its boundary");
    };
  }

  private boolean createApplication() {
    Optional<String> company = this.prompter.promptFormEntry("Company: ");
    Optional<String> role = this.prompter.promptFormEntry("Role: ");
    Optional<LocalDate> appliedDate = this.prompter.promptDate("Applied date (YYYY-MM-DD)");
    Optional<String> notes = this.prompter.promptNotesEntry("Notes (optional): ");
    Optional<URI> jobUrl = this.prompter.promptUrl("Job URL (optional): ");

    if (company.isPresent()
        && role.isPresent()
        && appliedDate.isPresent()
        && notes.isPresent()
        && jobUrl.isPresent()) {
      service.create(
          company.get(), role.get(), appliedDate.get(), notes.get(), String.valueOf(jobUrl.get()));
      return true;
    }
    return false;
  }

  private boolean listApplications() {
    this.view.showApplications(service.listAll());
    return true;
  }

  private boolean filterApplications() {
    Optional<Status> statusOptional = this.prompter.promptStatus("Status: ");
    statusOptional.ifPresent(
        status -> this.view.showApplications(this.service.listByStatus(status)));
    return true;
  }

  private boolean editApplication() {
    throw new UnsupportedOperationException("TODO");
  }

  private boolean deleteApplication() {
    throw new UnsupportedOperationException("TODO");
  }
}
