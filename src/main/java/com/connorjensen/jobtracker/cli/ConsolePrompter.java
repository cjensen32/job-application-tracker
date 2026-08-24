package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import com.connorjensen.jobtracker.model.Status;

public class ConsolePrompter {
  private final Scanner promptScanner;
  private final PrintStream promptStream;

  public ConsolePrompter(Scanner promptScanner, PrintStream promptStream) {
    this.promptScanner = promptScanner;
    this.promptStream = promptStream;
  }

  Optional<Integer> promptMenuSelection() {
    boolean help = false;
    int selection = 6;
    String selectionString;

    while (selection > 5 || selection < 0) {
      if (help) {
        this.promptStream.println("Enter a number between 0 and 5.");
        this.promptStream.print("> ");
      }
      if (this.promptScanner.hasNextLine()) {
        selectionString = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }
      try {
        selection = Integer.parseInt(selectionString);
      } catch (NumberFormatException ignored) {
        help = true;
        continue;
      }
      help = true;
    }
    return Optional.of(selection);
  }

  Optional<Long> promptPositiveId() {
    String idString;

    while (true) {
      this.promptStream.print("Application ID: ");
      if (this.promptScanner.hasNextLine()) {
        idString = this.promptScanner.nextLine();
        try {
          long id = Long.parseLong(idString);
          if (id >= 1) {
            return Optional.of(id);
          }
          this.promptStream.println("Enter a positive application ID.");
        } catch (NumberFormatException ignored) {
          continue;
        }
      } else {
        return Optional.empty();
      }
    }
  }

  Optional<String> promptFormEntry(String formLabel) {
    while (true) {
      this.promptStream.print(formLabel);
      if (this.promptScanner.hasNextLine()) {
        String testLine = this.promptScanner.nextLine().strip();
        if (!testLine.isEmpty()) {
          return Optional.of(testLine);
        }
      } else {
        return Optional.empty();
      }
    }
  }

  Optional<String> promptFormUpdate(String formLabel, String previous) {
    this.promptStream.print(formLabel + " [" + previous + "]: ");
    if (this.promptScanner.hasNextLine()) {
      String testLine = this.promptScanner.nextLine().strip();
      if (testLine.isEmpty()) {
        return Optional.of(previous);
      } else {
        return Optional.of(testLine);
      }
    } else {
      return Optional.empty();
    }
  }

  Optional<LocalDate> promptDate() {
    LocalDate appliedDate = null;

    while (appliedDate == null) {
      this.promptStream.print("Applied date (YYYY-MM-DD): ");
      String stringDate;
      if (this.promptScanner.hasNextLine()) {
        stringDate = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }

      // Return current date on empty
      if (stringDate.isEmpty()) {
        return Optional.of(LocalDate.now());
      }

      // Validate entry as actual date
      try {
        appliedDate = LocalDate.parse(stringDate);
      } catch (DateTimeParseException ignored) {
        continue;
      }
    }
    return Optional.of(appliedDate);
  }

  Optional<LocalDate> promptDateUpdate(LocalDate previousDate) {
    LocalDate appliedDate = null;

    while (appliedDate == null) {
      this.promptStream.print("Applied date [" + previousDate.toString() + "]: ");
      String stringDate;
      if (this.promptScanner.hasNextLine()) {
        stringDate = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }

      // Return previous date on empty
      if (stringDate.isEmpty()) {
        return Optional.of(previousDate);
      }

      // Validate entry as actual date
      try {
        appliedDate = LocalDate.parse(stringDate);
      } catch (DateTimeParseException ignored) {
        continue;
      }
    }
    return Optional.of(appliedDate);
  }

  Optional<Status> promptStatus() {
    Status status = null;
    while (status == null) {
      this.promptStream.print("Status: ");
      String statusEntry;
      if (this.promptScanner.hasNextLine()) {
        statusEntry = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }
      String statusLookup = statusEntry.strip().replace(" ", "_").replace("-", "_").toUpperCase();
      try {
        status = Status.valueOf(statusLookup);
      } catch (IllegalArgumentException ignored) {
        continue;
      }
    }
    return Optional.of(status);
  }

  Optional<Status> promptStatusUpdate(Status previousStatus) {
    Status status = null;
    while (status == null) {
      this.promptStream.print("Status [" + previousStatus.toString() + "]: ");
      String statusEntry;
      if (this.promptScanner.hasNextLine()) {
        statusEntry = this.promptScanner.nextLine();
        if (statusEntry.isEmpty()) {
          return Optional.of(previousStatus);
        }
      } else {
        return Optional.empty();
      }
      String statusLookup = statusEntry.strip().replace(" ", "_").replace("-", "_").toUpperCase();
      try {
        status = Status.valueOf(statusLookup);
      } catch (IllegalArgumentException ignored) {
        continue;
      }
    }
    return Optional.of(status);
  }

  Optional<String> promptNotesEntry() {
    this.promptStream.print("Notes (optional): ");
    if (this.promptScanner.hasNextLine()) {
      return Optional.of(this.promptScanner.nextLine());
    } else {
      return Optional.empty();
    }
  }

  Optional<String> promptNotesUpdate(String notes) {
    this.promptStream.print("Notes [" + notes + "] ('-' clears): ");
    if (this.promptScanner.hasNextLine()) {
      String notesString = this.promptScanner.nextLine();
      if (Objects.equals(notesString, "-")) {
        return Optional.of("");
      } else if (notesString.isEmpty()) {
        return Optional.of(notes);
      } else {
        return Optional.of(notesString);
      }
    } else {
      return Optional.empty();
    }
  }

  Optional<String> promptUrl() {
    while (true) {
      this.promptStream.print("Job URL (optional): ");
      if (this.promptScanner.hasNextLine()) {
        String urlString = this.promptScanner.nextLine();
        if (urlString.isEmpty()) {
          return Optional.of("");
        } else {
          if (validateUriString(urlString)) {
            return Optional.of(urlString);
          }
        }
      } else {
        return Optional.empty();
      }
    }
  }

  Optional<String> promptUrlUpdate(String jobUrl) {
    while (true) {
      this.promptStream.print("Job URL [" + jobUrl + "] ('-' clears): ");
      if (this.promptScanner.hasNextLine()) {
        String urlString = this.promptScanner.nextLine();
        if (urlString.isEmpty()) {
          return Optional.of(jobUrl);
        } else if (urlString.equals("-")) {
          return Optional.of("");
        } else {
          if (validateUriString(urlString)) {
            return Optional.of(urlString);
          }
        }
      } else {
        return Optional.empty();
      }
    }
  }

  // Helper refactor content for URL prompter
  private boolean validateUriString(String urlString) {
    try {
      URI uri = new URI(urlString);
      if (uri.isAbsolute()
          && (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"))
          && uri.getHost() != null) {
        return true;
      } else {
        throw new URISyntaxException(uri.toString(), " Failed validation test");
      }
    } catch (URISyntaxException e) {
      this.promptStream.println("Incorrect URL/URI syntax, enter again.");
    }
    return false;
  }
}
