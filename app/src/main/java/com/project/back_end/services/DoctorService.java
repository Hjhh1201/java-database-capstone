package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.hibernate.Hibernate;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private AppointmentRepository appointmentRepository;
    private TokenService tokenService;


    @Autowired
    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }


    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date){
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);

        if(optionalDoctor.isEmpty()){
            return Collections.emptyList();
        }

        Doctor doctor = optionalDoctor.get();

        List<String> availableTimes = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23,59,59);

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId,startOfDay,endOfDay);

        Set<String> bookedTimeSlots = appointments.stream()
                .map(appointment -> {
                    LocalTime startTime = appointment.getAppointmentTime().toLocalTime();
                    LocalTime endTime = startTime.plusHours(1); // Assuming 1-hour appointments
                    return String.format("%02d:%02d-%02d:%02d",
                            startTime.getHour(), startTime.getMinute(),
                            endTime.getHour(), endTime.getMinute());
                })
                .collect(Collectors.toSet());


        availableTimes.removeAll(bookedTimeSlots);

        return availableTimes;
    }

    @Transactional
    public int saveDoctor(Doctor doctor){
        System.out.println("doctor email: "+doctor.getEmail());
        
        try{
            if(doctorRepository.findByEmail(doctor.getEmail())==null){
                System.out.println("save doctor");
                doctorRepository.save(doctor);
                return 1;
            }else{
                return -1;
            }

            

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Transactional
    public int updateDoctor(Doctor doctor){
        try{
            if(doctorRepository.findById(doctor.getId()).isEmpty()){
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }


    @Transactional
    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }


    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1; // Doctor not found
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Internal error
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login login){
        System.out.println("login email "+login.getIdentifier());
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        Map<String, String> response = new HashMap<>();

        
        System.out.println("Login password: "+login.getPassword());

        if(doctor==null){
            response.put("error", "Invalid email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        System.out.println("doctor password "+doctor.getPassword());

        if(login.getPassword().equals(doctor.getPassword())){
            String token = tokenService.generateToken(login.getIdentifier());
            response.put("token", token);
            return ResponseEntity.ok().body(response);
        }

        response.put("error","Invalid Credentials");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name){
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return createResponse(doctors);
    }



    /*
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {

        if((name==null||name.equals("null")) && (specialty==null || specialty.equals("null"))){
            System.out.println("test1 "+specialty+name);
            List<Doctor> doctors = doctorRepository.findAll();

            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            return createResponse(filtered_doctors);
        }
        else if(name==null||name.equals("null")){
            System.out.println("test2 "+specialty+name);
            return filterDoctorByTimeAndSpecility(specialty,time);
        } else if (specialty==null || specialty.equals("null")) {
            return filterDoctorByNameAndTime(name,time);
        }else{
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name,specialty);
            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            return createResponse(filtered_doctors);
        }


    }*/


    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {

        if((name==null||name.equals("null")) && (specialty==null || specialty.equals("null"))){
            System.out.println("test1 "+specialty+name);
            List<Doctor> doctors = doctorRepository.findAll();

            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            filtered_doctors.forEach(doctor -> Hibernate.initialize(doctor.getAvailableTimes()));
            return createResponse(filtered_doctors);
        }
        else if(name==null||name.equals("null")){
            System.out.println("test2 "+specialty+name);
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            filtered_doctors.forEach(doctor -> Hibernate.initialize(doctor.getAvailableTimes()));
            return createResponse(filtered_doctors);
        } else if (specialty==null || specialty.equals("null")) {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            filtered_doctors.forEach(doctor -> Hibernate.initialize(doctor.getAvailableTimes()));
            return createResponse(filtered_doctors);
            
        }else{
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name,specialty);
            List<Doctor> filtered_doctors = filterDoctorByTime(doctors, time);
            filtered_doctors.forEach(doctor -> Hibernate.initialize(doctor.getAvailableTimes()));
            return createResponse(filtered_doctors);
        }


    }


    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm){
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        List<Doctor> filtered_doctors = filterDoctorByTime(doctors, amOrPm);
        return createResponse(filtered_doctors);
    }


    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return createResponse(doctors);
    }


    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        return createResponse(filteredDoctors);
    }


    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return createResponse(doctors);
    }


    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);
        return createResponse(filteredDoctors);
    }



    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm){
        return doctors;
    }

    private Map<String, Object> createResponse(List<Doctor> doctors) {
        return Map.of(
                "doctors", doctors,
                "count", doctors.size()
        );
    }



// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.

   
}
