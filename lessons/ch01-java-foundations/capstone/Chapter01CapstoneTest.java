/*
 * ============================================================================
 *  SPOILERS — DO NOT READ THIS FILE.
 *
 *  Everything you need to pass it is written out in CAPSTONE.md, including
 *  every class name, method signature and behaviour this file checks.
 *  Reading the assertions instead of the spec turns the capstone into a
 *  transcription exercise.
 *
 *  Copy it into place and run it. Don't open it.
 *
 *      cp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java \
 *         src/test/java/com/connorjensen/jobtracker/capstone/
 *      mvn test
 *
 *  If a test fails, read the failure message — they are written to tell you
 *  what was expected without showing you the code.
 * ============================================================================
 */
package com.connorjensen.jobtracker.capstone;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Chapter 1 Capstone — The Tracker Core")
class Chapter01CapstoneTest {

    private static final LocalDate DATE = LocalDate.of(2026, 8, 1);

    private static Application sample(String company) {
        return new Application(company, "Backend Engineer", DATE);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("model")
    class ModelTests {

        @Test
        @DisplayName("Status has exactly the five values from PROJECT.md")
        void statusHasFiveValues() {
            assertEquals(5, Status.values().length,
                    "Status should declare exactly 5 constants");
            assertNotNull(Status.valueOf("APPLIED"));
            assertNotNull(Status.valueOf("PHONE_SCREEN"));
            assertNotNull(Status.valueOf("INTERVIEWING"));
            assertNotNull(Status.valueOf("OFFER"));
            assertNotNull(Status.valueOf("REJECTED"));
        }

        @Test
        @DisplayName("Status.valueOf rejects an unknown name")
        void statusRejectsUnknown() {
            assertThrows(IllegalArgumentException.class, () -> Status.valueOf("GHOSTED"));
        }

        @Test
        @DisplayName("a newly constructed Application defaults to APPLIED with no id")
        void newApplicationDefaults() {
            Application app = sample("Acme Corp");

            assertEquals(Status.APPLIED, app.getStatus(),
                    "constructor should default status to APPLIED");
            assertEquals(null, app.getId(),
                    "an unsaved Application should have a null id");
            assertEquals("Acme Corp", app.getCompany());
            assertEquals("Backend Engineer", app.getRole());
            assertEquals(DATE, app.getAppliedDate());
        }

        @Test
        @DisplayName("notes and jobUrl are settable after construction")
        void optionalFieldsAreSettable() {
            Application app = sample("Acme Corp");
            app.setNotes("referred by Dana");
            app.setJobUrl("https://example.com/jobs/1");

            assertEquals("referred by Dana", app.getNotes());
            assertEquals("https://example.com/jobs/1", app.getJobUrl());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("repository")
    class RepositoryTests {

        private final ApplicationRepository repository = new InMemoryApplicationRepository();

        @Test
        @DisplayName("save assigns sequential ids starting at 1")
        void saveAssignsSequentialIds() {
            Application first = repository.save(sample("Acme Corp"));
            Application second = repository.save(sample("Globex"));

            assertEquals(1L, first.getId(), "first saved id should be 1");
            assertEquals(2L, second.getId(), "second saved id should be 2");
        }

        @Test
        @DisplayName("save does not reassign an id that already exists")
        void saveKeepsExistingId() {
            Application app = repository.save(sample("Acme Corp"));
            Long originalId = app.getId();

            app.setStatus(Status.OFFER);
            repository.save(app);

            assertEquals(originalId, app.getId(),
                    "re-saving should update, not insert a second row");
            assertEquals(1, repository.findAll().size(),
                    "re-saving an existing Application should not grow the store");
        }

        @Test
        @DisplayName("findAll returns everything saved")
        void findAllReturnsAll() {
            repository.save(sample("Acme Corp"));
            repository.save(sample("Globex"));
            repository.save(sample("Initech"));

            assertEquals(3, repository.findAll().size());
        }

        @Test
        @DisplayName("findAll on an empty repository returns an empty list, not null")
        void findAllEmptyIsNotNull() {
            List<Application> all = repository.findAll();
            assertNotNull(all, "findAll should never return null");
            assertTrue(all.isEmpty());
        }

        @Test
        @DisplayName("findById returns the saved Application")
        void findByIdReturnsSaved() {
            Application saved = repository.save(sample("Acme Corp"));

            Optional<Application> found = repository.findById(saved.getId());

            assertTrue(found.isPresent(), "findById should find a saved Application");
            assertEquals("Acme Corp", found.get().getCompany());
        }

        @Test
        @DisplayName("findById returns an empty Optional for an unknown id")
        void findByIdMissingIsEmpty() {
            Optional<Application> found = repository.findById(999L);

            assertNotNull(found, "findById should return Optional.empty(), never null");
            assertTrue(found.isEmpty(), "unknown id should produce an empty Optional");
        }

        @Test
        @DisplayName("findByStatus returns only matching applications")
        void findByStatusFilters() {
            Application a = repository.save(sample("Acme Corp"));
            repository.save(sample("Globex"));
            Application c = repository.save(sample("Initech"));

            a.setStatus(Status.INTERVIEWING);
            repository.save(a);
            c.setStatus(Status.INTERVIEWING);
            repository.save(c);

            List<Application> interviewing = repository.findByStatus(Status.INTERVIEWING);
            List<Application> applied = repository.findByStatus(Status.APPLIED);

            assertEquals(2, interviewing.size());
            assertEquals(1, applied.size());
            assertTrue(repository.findByStatus(Status.REJECTED).isEmpty(),
                    "a status with no matches should give an empty list, not null");
        }

        @Test
        @DisplayName("deleteById reports whether something was removed")
        void deleteByIdReportsOutcome() {
            Application saved = repository.save(sample("Acme Corp"));

            assertTrue(repository.deleteById(saved.getId()),
                    "deleting an existing id should return true");
            assertFalse(repository.deleteById(saved.getId()),
                    "deleting the same id twice should return false the second time");
            assertFalse(repository.deleteById(12345L),
                    "deleting an unknown id should return false");
            assertTrue(repository.findAll().isEmpty());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("service")
    class ServiceTests {

        private final ApplicationService service =
                new ApplicationService(new InMemoryApplicationRepository());

        @Test
        @DisplayName("create returns a saved Application with an id")
        void createReturnsSaved() {
            Application created = service.create("Acme Corp", "Backend Engineer", DATE);

            assertNotNull(created.getId(), "create should return an Application with an id assigned");
            assertEquals(Status.APPLIED, created.getStatus());
            assertEquals(1, service.listAll().size());
        }

        @Test
        @DisplayName("findById delegates and returns an Optional")
        void findByIdDelegates() {
            Application created = service.create("Acme Corp", "Backend Engineer", DATE);

            assertTrue(service.findById(created.getId()).isPresent());
            assertTrue(service.findById(404L).isEmpty());
        }

        @Test
        @DisplayName("listByStatus filters")
        void listByStatusFilters() {
            Application a = service.create("Acme Corp", "Backend Engineer", DATE);
            service.create("Globex", "Platform Engineer", DATE);
            service.updateStatus(a.getId(), Status.OFFER);

            assertEquals(1, service.listByStatus(Status.OFFER).size());
            assertEquals(1, service.listByStatus(Status.APPLIED).size());
            assertEquals(0, service.listByStatus(Status.REJECTED).size());
        }

        @Test
        @DisplayName("updateStatus persists the new status")
        void updateStatusPersists() {
            Application created = service.create("Acme Corp", "Backend Engineer", DATE);

            Application updated = service.updateStatus(created.getId(), Status.INTERVIEWING);

            assertEquals(Status.INTERVIEWING, updated.getStatus(),
                    "updateStatus should return the updated Application");
            assertEquals(Status.INTERVIEWING,
                    service.findById(created.getId()).orElseThrow().getStatus(),
                    "the change should still be there when read back");
        }

        @Test
        @DisplayName("updateStatus on an unknown id throws IllegalArgumentException")
        void updateStatusUnknownThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.updateStatus(999L, Status.OFFER));
        }

        @Test
        @DisplayName("delete reports whether something was removed")
        void deleteReportsOutcome() {
            Application created = service.create("Acme Corp", "Backend Engineer", DATE);

            assertTrue(service.delete(created.getId()));
            assertFalse(service.delete(created.getId()));
            assertTrue(service.listAll().isEmpty());
        }
    }

    // ------------------------------------------------------------------
    // The point of the whole chapter: the service must accept ANY
    // ApplicationRepository, including one it has never heard of.
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("dependency injection")
    class InjectionTests {

        /** A repository that isn't the in-memory one, and records that it was used. */
        static class RecordingRepository implements ApplicationRepository {
            final List<String> callsMade = new ArrayList<>();
            final List<Application> rows = new ArrayList<>();

            @Override
            public Application save(Application application) {
                callsMade.add("save");
                application.setId(42L);
                rows.add(application);
                return application;
            }

            @Override
            public List<Application> findAll() {
                callsMade.add("findAll");
                return new ArrayList<>(rows);
            }

            @Override
            public Optional<Application> findById(Long id) {
                callsMade.add("findById");
                return Optional.empty();
            }

            @Override
            public List<Application> findByStatus(Status status) {
                callsMade.add("findByStatus");
                return List.of();
            }

            @Override
            public boolean deleteById(Long id) {
                callsMade.add("deleteById");
                return false;
            }
        }

        @Test
        @DisplayName("the service uses the repository it was given, not one it built itself")
        void serviceUsesInjectedRepository() {
            RecordingRepository fake = new RecordingRepository();
            ApplicationService service = new ApplicationService(fake);

            Application created = service.create("Acme Corp", "Backend Engineer", DATE);
            service.listAll();

            assertTrue(fake.callsMade.contains("save"),
                    "service.create should delegate to the injected repository's save");
            assertTrue(fake.callsMade.contains("findAll"),
                    "service.listAll should delegate to the injected repository's findAll");
            assertEquals(42L, created.getId(),
                    "the id should come from the injected repository — the service must not "
                            + "construct its own storage");
        }

        @Test
        @DisplayName("service behaviour follows the injected repository, even when it finds nothing")
        void serviceHonoursEmptyResults() {
            ApplicationService service = new ApplicationService(new RecordingRepository());

            assertTrue(service.findById(1L).isEmpty());
            assertThrows(IllegalArgumentException.class,
                    () -> service.updateStatus(1L, Status.OFFER));
        }
    }
}
