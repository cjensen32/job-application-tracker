package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

  public Optional<Integer> promptMenuSelection() {
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

  public Optional<String> promptFormEntry(String formLabel) {
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

  public Optional<LocalDate> promptDate(String dateLabel) {
    LocalDate appliedDate = null;

    while (appliedDate == null) {
      this.promptStream.print(dateLabel);
      String stringDate;
      if (this.promptScanner.hasNextLine()) {
        stringDate = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }

      // Validate is actual date
      try {
        appliedDate = LocalDate.parse(stringDate);
      } catch (DateTimeParseException ignored) {
        continue;
      }
    }
    return Optional.of(appliedDate);
  }

  public Optional<Status> promptStatus(String statusLabel) {
    Status status = null;
    while (status == null) {
      this.promptStream.print(statusLabel);
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

  public Optional<Long> promptPositiveId(String idLabel) {
    long id = 0L;
    String idString;

    while (id < 1) {
      if (this.promptScanner.hasNextLine()) {
        this.promptStream.print(idLabel);
        idString = this.promptScanner.nextLine();
      } else {
        return Optional.empty();
      }
      try {
        id = Long.parseLong(idString);
      } catch (NumberFormatException ignored) {
        continue;
      }
    }
    return Optional.of(id);
  }

  public Optional<String> promptNotesEntry(String notesLabel) {
    this.promptStream.print(notesLabel);
    if (this.promptScanner.hasNextLine()) {
      return Optional.of(this.promptScanner.nextLine());
    } else {
      return Optional.empty();
    }
  }

  public Optional<String> promptUrl(String urlLabel) {
    while (true) {
      this.promptStream.print(urlLabel);
      if (this.promptScanner.hasNextLine()) {
        String urlString = this.promptScanner.nextLine();
        if (urlString.isEmpty()) {
          return Optional.of(urlString);
        } else {
          try {
            URI uri = new URI(urlString);
            if (uri.isAbsolute()
                && (uri.getScheme().equalsIgnoreCase("http")
                    || uri.getScheme().equalsIgnoreCase("https"))
                && uri.getHost() != null) {
              return Optional.of(uri.toString());
            } else {
              throw new URISyntaxException(uri.toString(), " Failed validation test");
            }
          } catch (URISyntaxException e) {
            this.promptStream.println("Incorrect URL/URI syntax, enter again.");
            continue;
          }
        }
      } else {
        return Optional.empty();
      }
    }
  }
}
