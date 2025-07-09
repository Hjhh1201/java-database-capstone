## MySQL Database Design

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), NOT NULL
- patient_id: INT, Foreign Key → patients(id), NOT NULL **ON DELETE CASCADE**
- appointment_time: DATETIME, Not Null
- duration_minutes: INT DEFAULT 60
- status: ENUM('Scheduled', 'Completed', 'Cancelled'), DEFAULT 'Scheduled'

> Represents bookings. Application logic should prevent overlapping appointments.. 
> Appointments will be automatically deleted if the patient is deleted.

### Table: admins
- id: INT, Primary Key, Auto Increment  
- email: VARCHAR(100), Unique, Not Null  
- password: VARCHAR(255), Not Null  <!--Optional, Validate via code -->
- name: VARCHAR(100), Not Null  


### Table: patients
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- name: VARCHAR(100), NOT NULL  
- date_of_birth: DATE, NOT NULL  
- gender: ENUM('Male', 'Female', 'Other') 
- phone: VARCHAR(20)
- email: VARCHAR(100), UNIQUE   <!--Optional, Validate via code -->
- password: VARCHAR(200), NOT NULL <!--Optional, Validate via code -->


### Table: doctors
- id: INT, Primary Key, Auto Increment  
- email: VARCHAR(100), Unique, Not Null  
- password: VARCHAR(255), Not Null  
- name: VARCHAR(100), Not Null  
- specialization: VARCHAR(100)  
- phone: VARCHAR(20)  
- bio: TEXT  

> Stores information for doctors. Availability fields define daily working hours.

### Table: doctor_availability
- id: INT, Primary Key, Auto Increment  
- doctor_id: INT, Foreign Key → doctors(id)  
- weekday: ENUM('Monday', 'Tuesday', ..., 'Sunday'), Not Null  
- start_time: TIME, Not Null  
- end_time: TIME, Not Null  

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment  
- name: VARCHAR(100), Not Null  
- address: TEXT, Not Null  
- phone: VARCHAR(20) 

---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "address": "Market Street"
  }
}
```