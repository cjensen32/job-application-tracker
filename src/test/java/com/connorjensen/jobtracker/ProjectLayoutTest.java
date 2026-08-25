package com.connorjensen.jobtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Maven standard directory layout")
class ProjectLayoutTest {

  private static final Path SRC = Path.of("src");
  private static final Path MAIN = Path.of("src", "main", "java");
  private static final Path TEST = Path.of("src", "test", "java");

  @Test
  void everyJavaFileLivesUnderMainOrTest() throws IOException {
    List<Path> strays = new ArrayList<>();
    for (Path file : filesUnder(SRC, ".java")) {
      if (!file.startsWith(MAIN) && !file.startsWith(TEST)) {
        strays.add(file);
      }
    }

    assertEquals(List.of(), strays, "java files outside src/main/java and src/test/java");
  }

  @Test
  void productionSourcesDoNotDependOnJunit() throws IOException {
    List<Path> leaks = new ArrayList<>();
    for (Path file : filesUnder(MAIN, ".java")) {
      if (Files.readString(file).contains("org.junit")) {
        leaks.add(file);
      }
    }

    assertEquals(List.of(), leaks, "production sources referencing JUnit");
  }

  @Test
  void compiledOutputStaysUnderTarget() throws IOException {
    assertEquals(List.of(), filesUnder(SRC, ".class"), "class files left inside the source tree");
    assertTrue(Files.isDirectory(Path.of("target", "classes")), "expected target/classes to exist");
  }

  @Test
  void packageDeclarationsMatchTheirDirectories() throws IOException {
    List<String> mismatches = new ArrayList<>();
    for (Path root : List.of(MAIN, TEST)) {
      for (Path file : filesUnder(root, ".java")) {
        String declared = declaredPackage(file);
        String expected = packageFromPath(root, file);
        if (!declared.equals(expected)) {
          mismatches.add(file + " declares " + declared + " but sits in " + expected);
        }
      }
    }

    assertEquals(List.of(), mismatches, "javac allows this; the convention is what forbids it");
  }

  private static List<Path> filesUnder(Path root, String suffix) throws IOException {
    try (Stream<Path> walk = Files.walk(root)) {
      return walk.filter(Files::isRegularFile)
          .filter(file -> file.toString().endsWith(suffix))
          .sorted()
          .toList();
    }
  }

  private static String declaredPackage(Path file) throws IOException {
    for (String line : Files.readAllLines(file)) {
      if (line.startsWith("package ")) {
        return line.substring("package ".length(), line.indexOf(';')).trim();
      }
    }
    return "";
  }

  private static String packageFromPath(Path root, Path file) {
    List<String> parts = new ArrayList<>();
    root.relativize(file.getParent()).forEach(part -> parts.add(part.toString()));
    return String.join(".", parts);
  }
}
