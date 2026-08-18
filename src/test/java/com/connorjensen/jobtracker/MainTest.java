package com.connorjensen.jobtracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpOutputBuffer() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void testApplicationCreationSample() {
        // Set example sequence to test against
        String applicationOptionString = String.join("\n",
                "0",  // Select 0 for Application creation
                "Shyft Solutions",  // Employer
                "Software Engineer Intern", // Job Title
                "2025-01-01", // Application Date
                "1",  // Select 1 to view output
                "5"  // Select 5 to exit
        ) + "\n";

        // Set byte array to be
        ByteArrayInputStream testInputBuffer = new ByteArrayInputStream(
                applicationOptionString.getBytes(StandardCharsets.UTF_8)
        );

        System.setIn(testInputBuffer);
        System.setOut(new PrintStream(testOut));
        Main.main(new String[0]);

        String stringOut = testOut.toString(StandardCharsets.UTF_8);
        originalOut.println("STRING OUTPUT VARIABLE: " + stringOut);

        assertTrue(stringOut.contains("1"));
        assertTrue(stringOut.contains("Shyft Solutions"));
        assertTrue(stringOut.contains("Software Engineer Intern"));
        assertTrue(stringOut.contains("2025-01-01"));

    }

    /*
        @Test
        public void testApplicationCreationFailure() {
            // Set example sequence to test against
            String applicationOptionString = String.join("\n",
                    "0",
                    "Shyft Solutions",
                    "Software Engineer Intern",
                    "August 26, 2026",
                    "08-9-2021",
                    "2026-08-16",
                    "1",
                    "5"
            ) + "\n";

            // Set byte array to be
            ByteArrayInputStream testInputBuffer = new ByteArrayInputStream(
                    applicationOptionString.getBytes(StandardCharsets.UTF_8)
            );

            System.setIn(testInputBuffer);
            System.setOut(new PrintStream(testOut));
            Main.main(new String[0]);

            String stringOut = testOut.toString(StandardCharsets.UTF_8);
            originalOut.println("STRING OUTPUT VARIABLE: " + stringOut);

            assertTrue(stringOut.contains("1"));
            assertTrue(stringOut.contains("Shyft Solutions"));
            assertTrue(stringOut.contains("Software Engineer Intern"));
            assertTrue(stringOut.contains("2026-08-16"));
        }
     */
}
