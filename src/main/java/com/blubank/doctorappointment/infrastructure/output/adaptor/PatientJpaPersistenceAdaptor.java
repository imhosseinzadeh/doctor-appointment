package com.blubank.doctorappointment.infrastructure.output.adaptor;

import com.blubank.doctorappointment.application.ports.output.PatientPersistencePort;
import com.blubank.doctorappointment.domain.entity.Doctor;
import com.blubank.doctorappointment.domain.vo.OpenTime;
import com.blubank.doctorappointment.domain.entity.Patient;
import com.blubank.doctorappointment.domain.exception.DomainNotFoundException;
import com.blubank.doctorappointment.domain.vo.Appointment;
import com.blubank.doctorappointment.domain.vo.ID;
import com.blubank.doctorappointment.domain.vo.VisitDate;
import com.blubank.doctorappointment.infrastructure.output.AppointmentRepository;
import com.blubank.doctorappointment.infrastructure.output.PatientRepository;
import com.blubank.doctorappointment.infrastructure.output.appointment.AppointmentEntity;
import com.blubank.doctorappointment.infrastructure.output.appointment.AppointmentPK;
import com.blubank.doctorappointment.infrastructure.output.patient.PatientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author a.ariani
 */
@Transactional(readOnly = true)
@Component
public class PatientJpaPersistenceAdaptor implements PatientPersistencePort {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;


    @Autowired
    public PatientJpaPersistenceAdaptor(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }


    @Override
    public List<OpenTime> findAllByVisitDate(VisitDate visitDate) {
        return this.appointmentRepository.findOpenTimeByVisitDate(visitDate.getVisitDate())
                .stream().map(AppointmentRepository.OpenAppointment::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Patient createAppointment(Doctor doctor, Patient patient, OpenTime openTime) {
        AppointmentEntity appointmentEntity = this.appointmentRepository.findById(new AppointmentPK(doctor.getId().getId(), openTime.getVisitDate().getVisitDate(),
                openTime.getTimeDuration().getStart(),
                openTime.getTimeDuration().getEnd()))
                .orElseThrow(() -> {
                    throw new DomainNotFoundException("time not found");
                });
        PatientEntity entity = PatientEntity.from(patient);
        entity.addAppointment(appointmentEntity);
        PatientEntity save = this.patientRepository.save(entity);
        return save.toDomain();
    }

    @Override
    public Patient findDetailedById(ID id) {
        return patientRepository.findDetailById(id.getId()).
                map(PatientEntity::toDomain)
                .orElseThrow(() -> {
                    throw new DomainNotFoundException(MessageFormat.format("patient with id '{'{0}'}' notfound", id.getId()));
                });
    }

    @Override
    public List<Appointment> findAppointments(ID id) {
        return null;
    }

}