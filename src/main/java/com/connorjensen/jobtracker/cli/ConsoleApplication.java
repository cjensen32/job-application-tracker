package com.connorjensen.jobtracker.cli;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.connorjensen.jobtracker.model.Application;
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
      if (selection.isEmpty()) {
        this.view.showGoodbye();
        return;
      } else {
        running = dispatch(selection.get());
      }
    }
    this.view.showGoodbye();
    return;
  }

  private boolean dispatch(int selection) {
    return switch (selection) {
      case 0 -> createApplication();
      case 1 -> listApplications();
      case 2 -> filterApplications();
      case 3 -> editApplication();
      case 4 -> deleteApplication();
      case 5 -> false;
      default -> throw new IllegalStateException("validated selection escaped its boundary");
    };
  }

  private boolean createApplication() {
    Optional<String> company = this.prompter.promptRequiredFormEntry("Company: ");
    if (company.isEmpty()) {
      return false;
    }
    Optional<String> role = this.prompter.promptRequiredFormEntry("Role: ");
    if (role.isEmpty()) {
      return false;
    }
    Optional<LocalDate> appliedDate = this.prompter.promptDate();
    if (appliedDate.isEmpty()) {
      return false;
    }
    Optional<String> notes = this.prompter.promptNotesEntry();
    if (notes.isEmpty()) {
      return false;
    }
    Optional<String> jobUrl = this.prompter.promptUrl();
    if (jobUrl.isEmpty()) {
      return false;
    }

    Application newApp =
        service.create(company.get(), role.get(), appliedDate.get(), notes.get(), jobUrl.get());
    this.view.showCompletion("Created", newApp.getId());
    return true;
  }

  private boolean listApplications() {
    if (!service.listAll().isEmpty()) {
      this.view.showApplications(service.listAll());
    } else {
      this.view.showNoApplicationEntries("list");
    }
    return true;
  }

  private boolean filterApplications() {
    if (this.service.listAll().isEmpty()) {
      this.view.showNoApplicationEntries("filter & list");
      return true;
    } else {
      Optional<Status> statusOptional = this.prompter.promptStatus();
      if (statusOptional.isEmpty()) {
        return false;
      } else {
        List<Application> statusApplications = this.service.listByStatus(statusOptional.get());
        if (statusApplications.isEmpty()) {
          this.view.showFoundNoStatusMatches(statusOptional.get());
          return true;
        }
        this.view.showApplications(statusApplications);
        return true;
      }
    }
  }

  private boolean editApplication() {
    if (!this.service.listAll().isEmpty()) {
      Optional<Long> idOptional = this.prompter.promptPositiveId();
      if (idOptional.isPresent()) {
        Long id = idOptional.get();
        Optional<Application> applicationOptional = service.findById(id);
        if (applicationOptional.isPresent()) {
          Application applicationEdit = applicationOptional.get();
          this.view.showApplicationDetails(applicationEdit);
          Optional<String> newCompany =
              this.prompter.promptFormUpdate("Company", applicationEdit.getCompany());
          Optional<String> newRole =
              this.prompter.promptFormUpdate("Role", applicationEdit.getRole());
          Optional<LocalDate> newDate =
              this.prompter.promptDateUpdate(applicationEdit.getAppliedDate());
          Optional<Status> newStatus =
              this.prompter.promptStatusUpdate(applicationEdit.getStatus());
          Optional<String> newNotes = this.prompter.promptNotesUpdate(applicationEdit.getNotes());
          Optional<String> newURL = this.prompter.promptUrlUpdate(applicationEdit.getJobUrl());
          // Apply edits to update application if all come back as non EOF signals
          if (newCompany.isPresent()
              && newRole.isPresent()
              && newDate.isPresent()
              && newStatus.isPresent()
              && newNotes.isPresent()
              && newURL.isPresent()) {
            applicationEdit.setCompany(newCompany.get());
            applicationEdit.setRole(newRole.get());
            applicationEdit.setAppliedDate(newDate.get());
            applicationEdit.setStatus(newStatus.get());
            applicationEdit.setNotes(newNotes.get());
            applicationEdit.setJobUrl(newURL.get());
            this.view.showCompletion("Updated", applicationEdit.getId());
            return true;
          }
          return false;
        } else {
          this.view.showNoIdMatches(id);
        }
      }
    }
    this.view.showNoApplicationEntries("edit");
    return true;
  }

  private boolean deleteApplication() {
    if (!this.service.listAll().isEmpty()) {
      Optional<Long> idOptional = this.prompter.promptPositiveId();
      if (idOptional.isPresent()) {
        Long id = idOptional.get();
        boolean result = this.service.delete(id);
        if (result) {
          this.view.showCompletion("Deleted", id);
        } else {
          this.view.showNoIdMatches(id);
        }
        return true;
      }
      return false;
    }
    this.view.showNoApplicationEntries("delete");
    return true;
  }
}
