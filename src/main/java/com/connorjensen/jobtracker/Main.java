package com.connorjensen.jobtracker;

import java.time.LocalDate;

public class Main {
  public static void main(String[] args) {
    Application app = new Application("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1));
    System.out.println(app.summary());
    System.out.println(app);
    
    ApplicationDto dto = new ApplicationDto("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1));
    System.out.println(dto);
  }
}
