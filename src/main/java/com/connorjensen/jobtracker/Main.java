package com.connorjensen.jobtracker;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.connorjensen.jobtracker.cli.ConsoleApplication;
import com.connorjensen.jobtracker.cli.ConsolePrompter;
import com.connorjensen.jobtracker.cli.ConsoleView;
import com.connorjensen.jobtracker.cli.TextTable;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public class Main {
  private Main() {}

  public static void main(String[] args) {
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    TextTable table = new TextTable();
    ConsoleView view = new ConsoleView(System.out, table);
    ConsolePrompter prompter = new ConsolePrompter(scanner, System.out);
    ConsoleApplication application = new ConsoleApplication(service, prompter, view);

    application.run();
  }
}
