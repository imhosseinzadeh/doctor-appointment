package com.blubank.doctorappointment.infrastructure.output.patient;

import com.blubank.doctorappointment.domain.entity.Patient;
import com.blubank.doctorappointment.domain.vo.Appointment;
import com.blubank.doctorappointment.infrastructure.output.BaseEntity;
import com.blubank.doctorappointment.infrastructure.output.appointment.AppointmentEntity;
import com.blubank.doctorappointment.infrastructure.output.doctor.DoctorEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author a.ariani
 */
@Table(name = "patient_tbl")
@Entity
public class PatientEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<AppointmentEntity> appointments = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAppointments(Set<AppointmentEntity> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(AppointmentEntity appointmentEntity) {
        appointmentEntity.setPatient(this);
        this.appointments.add(appointmentEntity);
    }

    public Set<AppointmentEntity> getAppointments() {
        return appointments;
    }

    public static PatientEntity from(Patient patient) {
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setName(patient.getFullName().getFullName());
        patientEntity.setPhoneNumber(patient.getPhoneNumber().getPhoneNumber());
        return patientEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientEntity that = (PatientEntity) o;
        return phoneNumber.equals(that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    public Patient toDomain() {
        return Patient.of(this.getId(), this.getName(), this.getPhoneNumber(),
                appointments.stream().map(AppointmentEntity::toDomain).collect(Collectors.toList())
        );
    }
}