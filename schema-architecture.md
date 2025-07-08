## Architecture Summary

This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules including Appointment, Patient Dashboard and Patient Record. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories including MySQL Repository and MongoDB Repository. MySQL uses JPA entities while MongoDB uses document models.



## Numbered flow of data and control

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST Controllers.
3. The controller calls the service layer that can apply business rules and validations.
4. The service layer communicates with the MySQL Repository which uses Spring Data JPA or MongoDB Repository which uses Spring Data MongoDB to perform data access operations.
5. Each repository interfaces directly with the underlying database engine: MySQL or MongoDB.
6. Once data is retrieved from the database, it is mapped into Java model classes that the application can work with like JAP entities or document objects.
7. Finally, the bound models are used in the response layer.