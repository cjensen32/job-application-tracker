package com.connorjensen.jobtracker.cli;

import com.connorjensen.jobtracker.service.ApplicationService;

public class ConsoleApplication {
  private ApplicationService service;
  private ConsolePrompter prompter;
  private ConsoleView view;

  public ConsoleApplication(
      ApplicationService service, ConsolePrompter prompter, ConsoleView view) {
    this.service = service;
    this.prompter = prompter;
    this.view = view;
  }

  public void run() {
    boolean running = true;
    int selection;

    while (running) {
      selection = this.prompter.input();
      running = this.prompter.dispatch(selection);
    }
    exit();
  }
}
