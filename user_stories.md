# User Story 

## Admin User Stories

**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**  
1. Login page accepts admin username/password.  
2. Invalid credentials show an error message.  
3. Successful login redirects to the admin dashboard.  
4. Session expires after 30 minutes of inactivity.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Integrate with existing auth system.  
- Audit logs should record login attempts.  

---

**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access when done._

**Acceptance Criteria:**  
1. Logout button is visible in the admin dashboard.  
2. Clicking it terminates the session and redirects to the login page.  
3. Session tokens are invalidated immediately.  

**Priority:** Medium  
**Story Points:** 2  
**Notes:**  
- Test with multiple concurrent sessions.  

---

**Title:**  
_As an admin, I want to add doctors to the portal, so that they can manage patient appointments._

**Acceptance Criteria:**  
1. Form collects: Name, Email, Specialty, License Number.  
2. System validates email/license uniqueness.  
3. Success notification appears, and the doctor receives a welcome email.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Integrate with email service for notifications.  

---

**Title:**  
_As an admin, I want to delete a doctor’s profile, so that I can remove inactive accounts._

**Acceptance Criteria:**  
1. Admin can search/select a doctor from a list.  
2. Confirmation dialog appears before deletion.  
3. All related data (appointments, etc.) are archived.  

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Implement soft delete to preserve data integrity.  

---

**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to get monthly appointment counts, so that I can track platform usage._

**Acceptance Criteria:**  
1. Stored procedure `sp_monthly_appointments` exists in MySQL.  
2. CLI command returns: Month, Total Appointments, Unique Patients.  
3. Results are exportable to CSV.  

**Priority:** Low  
**Story Points:** 8  
**Notes:**  
- Document the CLI command in the admin manual.  
- Schedule automated monthly reports (future enhancement).  

---

##Patient User Stories

**Title:**  
_As a patient, I want to view a list of doctors without logging in, so I can explore options before registering._

**Acceptance Criteria:**  
1. Public page displays doctor names, photos, and specialties.  
2. No authentication required to view the list.  
3. Search/filter by specialty is available.  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Display only basic info (full profiles require login).  

---

**Title:**  
_As a patient, I want to sign up using email and password, so I can book appointments._

**Acceptance Criteria:**  
1. Signup form collects: Email, Password, Name, Phone.  
2. Email verification is sent after submission.  
3. Duplicate emails are blocked.  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Integrate with email service (e.g., SendGrid).  

---

**Title:**  
_As a patient, I want to log into the portal to manage my bookings._

**Acceptance Criteria:**  
1. Login with email/password.  
2. Invalid attempts show error messages.  
3. Successful login redirects to dashboard.  

**Priority:** High  
**Story Points:** 2  
**Notes:**  
- Implement session timeout after 30 mins.  

---

**Title:**  
_As a patient, I want to log out to secure my account._

**Acceptance Criteria:**  
1. Logout button is visible in the dashboard.  
2. Session terminates immediately on logout.  
3. Redirects to public homepage.  

**Priority:** Medium  
**Story Points:** 1  
**Notes:**  
- Audit logs should record logout events.  

---

**Title:**  
_As a patient, I want to book an hour-long appointment with a doctor._

**Acceptance Criteria:**  
1. Calendar shows available slots for selected doctor.  
2. Confirmation email is sent after booking.  
3. Prevents double-booking same slot.  

**Priority:** High  
**Story Points:** 8  
**Notes:**  
- Integrate with calendar API (e.g., Google Calendar).  

---

## Doctor User Stories

**Title:**  
_As a doctor, I want to log into the portal to manage my appointments._

**Acceptance Criteria:**  
1. Secure login with email/password or SSO  
2. Two-factor authentication option  
3. Redirect to doctor dashboard after login  

**Priority:** High  
**Story Points:** 3  
**Notes:**  
- Differentiate from patient login flow  

---

**Title:**  
_As a doctor, I want to log out of the portal to protect my data._

**Acceptance Criteria:**  
1. Clear logout button in navigation  
2. Immediate session termination  
3. Redirect to public homepage  

**Priority:** Medium  
**Story Points:** 2  
**Notes:**  
- Should invalidate JWT tokens  

---

**Title:**  
_As a doctor, I want to view my appointment calendar to stay organized._

**Acceptance Criteria:**  
1. Daily/weekly/monthly calendar views  
2. Color-coded appointment statuses  
3. Sync with external calendars (Google/Outlook)  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- Should support timezone conversion  

---

**Title:**  
_As a doctor, I want to mark my unavailability to inform patients of available slots._

**Acceptance Criteria:**  
1. Block out personal/unavailable time  
2. Set recurring unavailable periods  
3. Real-time updates to patient booking system  

**Priority:** High  
**Story Points:** 8  
**Notes:**  
- Should prevent double-booking  

---

**Title:**  
_As a doctor, I want to update my profile with specialization and contact information._

**Acceptance Criteria:**  
1. Edit bio, specialties, credentials  
2. Upload professional photo  
3. Changes reflect immediately on public profile  

**Priority:** Medium  
**Story Points:** 3  
**Notes:**  
- Admin approval optional for certain fields  

---

**Title:**  
_As a doctor, I want to view patient details for upcoming appointments._

**Acceptance Criteria:**  
1. See patient medical history summary  
2. View contact information  
3. Download patient forms  

**Priority:** High  
**Story Points:** 5  
**Notes:**  
- HIPAA-compliant data display  