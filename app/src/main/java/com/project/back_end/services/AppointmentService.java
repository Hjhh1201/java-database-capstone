package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private TokenService tokenService;
    private com.project.back_end.services.Service service;


    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository,
                              TokenService tokenService, com.project.back_end.services.Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment){
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment){
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> a = appointmentRepository.findById(appointment.getId());

        if(a.isEmpty()){
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment a1 = a.get();

        int valid = service.validateAppointment(appointment);
        if (valid == -1) {
            response.put("message", "Invalid doctor.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (valid == 0) {
            response.put("message", "No Matching time is found.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }


    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token){
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (!optionalAppointment.isPresent()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appointment = optionalAppointment.get();

        String identifier = tokenService.extractIdentifier(token);

        if(appointment.getPatient().getEmail().equals(identifier) || appointment.getDoctor().getEmail().equals(identifier)){
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment canceled successfully.");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Unauthorized to cancel this appointment.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }



    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token){
        Map<String, Object> result = new HashMap<>();
        String identifier = tokenService.extractIdentifier(token);

        Doctor doctor = doctorRepository.findByEmail(identifier);

        if(doctor==null){
            result.put("error", "Doctor not found");
            return result;
        }


        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59);
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctor.getId());

        if (pname != null && !pname.trim().isEmpty()) {
            appointments = appointments.stream()
                    .filter(app -> {
                        Optional<Patient> patient = patientRepository.findById(app.getPatient().getId());
                        return patient.isPresent() &&
                                patient.get().getName().toLowerCase().contains(pname.toLowerCase());
                    })
                    .collect(Collectors.toList());
        }

        result.put("appointments", appointments);
        return result;
    }

    // 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
