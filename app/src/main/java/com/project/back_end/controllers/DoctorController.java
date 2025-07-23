package com.project.back_end.controllers;


import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;


    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }


    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token
    ){
        if(!service.validateToken(token,user)){
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Invalid token for role: " + user
            ));
        }


        try{
            List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
            return ResponseEntity.ok(Map.of(
                    "doctorId", doctorId,
                    "date", date.toString(),
                    "availableSlots", availability
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch availability",
                    "details", e.getMessage()
            ));
        }
    }


    @GetMapping
    @Transactional
    public ResponseEntity<?> getDoctor(){
        List<Doctor> doctors = doctorService.getDoctors();
        
        doctors.forEach(doctor -> Hibernate.initialize(doctor.getAvailableTimes()));
        
        return ResponseEntity.ok(Map.of("doctors",doctors));
    }


    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ){
        if(!service.validateToken(token,"admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","unauthorized"));
        }

        try{
            int result = doctorService.saveDoctor(doctor);

            return switch (result) {
                case 1 -> ResponseEntity.status(201).body(Map.of(
                        "message", "Doctor added successfully",
                        "doctorId", doctor.getId()
                ));
                case -1 -> ResponseEntity.status(409).body(Map.of(
                        "error", "Doctor already exists"
                ));
                default -> ResponseEntity.internalServerError().body(Map.of(
                        "error", "Failed to add doctor"
                ));
            };

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch availability",
                    "details", e.getMessage()
            ));
        }
    }




    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }


    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ){

        if(!service.validateToken(token,"admin")){
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Admin access required"
            ));
        }



        try {
            int result = doctorService.updateDoctor(doctor);
            return switch (result) {
                case 1 -> ResponseEntity.ok(Map.of(
                        "message", "Doctor updated successfully",
                        "doctorId", doctor.getId()
                ));
                case -1 -> ResponseEntity.status(404).body(Map.of(
                        "error", "Doctor not found"
                ));
                default -> ResponseEntity.internalServerError().body(Map.of(
                        "error", "Failed to update doctor"
                ));
            };
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Internal server error",
                    "details", e.getMessage()
            ));
        }
    }



    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ){

        if(!service.validateToken(token,"admin")){
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Admin access required"
            ));
        }

        try{
            int result =doctorService.deleteDoctor(id);

            return switch (result) {
                case 1 -> ResponseEntity.ok(Map.of(
                        "message", "Doctor deleted successfully",
                        "doctorId", id
                ));
                case -1 -> ResponseEntity.status(404).body(Map.of(
                        "error", "Doctor not found"
                ));
                default -> ResponseEntity.internalServerError().body(Map.of(
                        "error", "Failed to delete doctor"
                ));
            };

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Internal server error",
                    "details", e.getMessage()
            ));
        }
    }


    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filterDoctor(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ){

        try {
            Map<String, Object> result = doctorService.filterDoctorsByNameSpecilityandTime(
                    name, speciality, time);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to filter doctors",
                    "details", e.getMessage()
            ));
        }
    }





    // 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
