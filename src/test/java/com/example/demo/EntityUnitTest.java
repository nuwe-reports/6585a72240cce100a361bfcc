package com.example.demo;

import com.example.demo.entities.Appointment;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
import com.example.demo.entities.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Doctor d1;
    private Patient p1;
    private Room r1;
    private Appointment a1;
    private Appointment a2;
    private Appointment a3;

    @BeforeEach
    void setUp() {
        d1 = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        p1 = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        r1 = new Room("Dermatology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt1 = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt1 = LocalDateTime.parse("20:30 24/04/2023", formatter);
        LocalDateTime startsAt2 = LocalDateTime.parse("19:30 25/04/2023", formatter);
        LocalDateTime finishesAt2 = LocalDateTime.parse("20:30 25/04/2023", formatter);

        a1 = new Appointment(p1, d1, r1, startsAt1, finishesAt1);
        a2 = new Appointment(p1, d1, r1, startsAt1, finishesAt1);
        a3 = new Appointment(p1, d1, r1, startsAt2, finishesAt2);
    }

    @Test
    void testAppointmentOverlapsTime1() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt1 = LocalDateTime.parse("12:30 24/04/2023", formatter);
        LocalDateTime finishesAt1 = LocalDateTime.parse("13:30 24/04/2023", formatter);
        LocalDateTime startsAt2 = LocalDateTime.parse("11:30 24/04/2023", formatter);
        LocalDateTime finishesAt2 = LocalDateTime.parse("12:35 24/04/2023", formatter);

        Appointment app1 = new Appointment(p1, d1, r1, startsAt1, finishesAt1);
        Appointment app2 = new Appointment(p1, d1, r1, startsAt2, finishesAt2);

        assertThat(app1.overlaps(app2)).isTrue();
    }

    @Test
    void testAppointmentOverlapsTime2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt1 = LocalDateTime.parse("11:20 24/04/2023", formatter);
        LocalDateTime finishesAt1 = LocalDateTime.parse("12:30 24/04/2023", formatter);
        LocalDateTime startsAt2 = LocalDateTime.parse("11:30 24/04/2023", formatter);
        LocalDateTime finishesAt2 = LocalDateTime.parse("12:35 24/04/2023", formatter);

        Appointment app1 = new Appointment(p1, d1, r1, startsAt1, finishesAt1);
        Appointment app2 = new Appointment(p1, d1, r1, startsAt2, finishesAt2);

        assertThat(app1.overlaps(app2)).isTrue();

    }

    @Test
    void testDoctorEntityPersistence() {
        entityManager.persistAndFlush(d1);
        Doctor found = entityManager.find(Doctor.class, d1.getId());
        assertThat(found).isEqualTo(d1);
    }

    @Test
    void testPatientEntityPersistence() {
        entityManager.persistAndFlush(p1);
        Patient found = entityManager.find(Patient.class, p1.getId());
        assertThat(found).isEqualTo(p1);
    }

    @Test
    void testRoomEntityPersistence() {
        entityManager.persistAndFlush(r1);
        Room found = entityManager.find(Room.class, r1.getRoomName());
        assertThat(found).isEqualTo(r1);
    }

    @Test
    void testAppointmentEntityPersistence() {
        entityManager.persistAndFlush(a1);
        Appointment found = entityManager.find(Appointment.class, a1.getId());
        assertThat(found).isEqualTo(a1);
    }

    @Test
    void testAppointmentOverlaps() {
        assertThat(a1.overlaps(a2)).isTrue();
    }

    @Test
    void testAppointmentDoesNotOverlap() {
        assertThat(a1.overlaps(a3)).isFalse();
        assertThat(a2.overlaps(a3)).isFalse();
    }
}
