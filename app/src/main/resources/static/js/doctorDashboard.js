/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/


import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";



// Global variables
const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // YYYY-MM-DD
const token = localStorage.getItem("token");
let patientName = "null";

document.getElementById("searchBar").addEventListener("input", (e) => {
  const value = e.target.value.trim();
  patientName = value === "" ? "null" : value;
  loadAppointments();
});


document.getElementById("todayButton").addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});


document.getElementById("datePicker").addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});

async function loadAppointments() {
    try{

        const result = await getAllAppointments(selectedDate, patientName, token);
        const appointments = result.appointments || [];

        console.log("getAllAppointments result:", appointments);

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
              const tr = document.createElement("tr");
              tr.innerHTML = `<td colspan="5">No Appointments found for today.</td>`;
              tableBody.appendChild(tr);
              return;
            }


        appointments.forEach((appointment) => {
              const patient = {
              id: appointment.patientId,
              name: appointment.patientName,
              phone: appointment.patientPhone, 
              email: appointment.patientEmail, 
               };
              

              const row = createPatientRow(patient, appointment.id, appointment.doctorId);
              tableBody.appendChild(row);
            });

    } catch (error){
        console.error("Error loading appointments:", error);
        const tr = document.createElement("tr");
        tr.innerHTML = `<td colspan="5">Error loading appointments. Try again later.</td>`;
        tableBody.innerHTML = "";
        tableBody.appendChild(tr);

    }
}



document.addEventListener("DOMContentLoaded", () => {

  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

