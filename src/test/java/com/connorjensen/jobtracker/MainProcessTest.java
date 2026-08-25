package com.connorjensen.jobtracker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class MainProcessTest {

  @Test
  void testConsoleStartupValidity() throws Exception {
    Path javaHome = Path.of(System.getProperty("java.home"), "bin", "java");
    String classPath = System.getProperty("java.class.path");
    Process process =
        new ProcessBuilder(String.valueOf(javaHome), "-cp", classPath, Main.class.getName())
            .redirectErrorStream(true)
            .start();

    try (OutputStream input = process.getOutputStream()) {
      input.close();
    }

    long timeWait = 5;
    if (!process.waitFor(timeWait, TimeUnit.SECONDS)) {
      process.destroyForcibly();
      fail("Main didn't exit in " + String.valueOf(timeWait) + " seconds after EOF");
    }

    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    String expectedOutput =
        """
        Job Application Tracker
        0 Create application
        1 List applications
        2 Filter applications by status
        3 Edit application
        4 Delete application
        5 Quit
        > Goodbye.
        """;

    assertAll(
        () -> assertEquals(0, process.exitValue()),
        () -> assertEquals(expectedOutput, output),
        () -> assertFalse(output.contains("Exception")));
  }
}
