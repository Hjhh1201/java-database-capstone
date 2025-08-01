/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';




document.addEventListener('DOMContentLoaded', () => {
  loadDoctorCards();


  document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

  waitForAddDoctorButton();

});

function waitForAddDoctorButton() {
  const checkExist = setInterval(() => {
    const addBtn = document.getElementById("addDocBtn");
    if (addBtn) {
      addBtn.addEventListener("click", () => openModal("addDoctor"));
      clearInterval(checkExist);
    }
  }, 100); // 每100ms检测一次
}



async function loadDoctorCards() {

     const contentDiv = document.getElementById("content");
     contentDiv.innerHTML = "";

    const doctors = await getDoctors();

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
}



export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}


async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar").value.trim();
  const time = document.getElementById("filterTime").value;
  const specialty = document.getElementById("filterSpecialty").value;

  
  const { doctors } = await filterDoctors(name, time, specialty);
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
  } else {
    renderDoctorCards(doctors);
  }
}




window.adminAddDoctor = async function () {
  const name = document.getElementById("doctorName").value.trim();
  const specialization = document.getElementById("specialization").value;
  const email = document.getElementById("doctorEmail").value.trim();
  const password = document.getElementById("doctorPassword").value;
  const phone = document.getElementById("doctorPhone").value.trim();

  // get availability checkbox value
  const checkboxes = document.querySelectorAll("input[name='availability']:checked");
  const availability = Array.from(checkboxes).map(cb => cb.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("You must be logged in as admin to perform this action.");
    return;
  }



  const doctorData = {
    name,
    specialty: specialization,
    email,
    password,
    phone,
    availableTimes: availability
  };


  try {
    const result = await saveDoctor(doctorData, token);
    if (result.success) {
      console.log("test1");
      document.getElementById('modal').style.display = 'none';
      alert(result.message || "Doctor saved successfully.");
      document.getElementById("modal").style.display = "none";
      
      
      console.log("test3");
      loadDoctorCards();  // reload doctor cards
      console.log("test4");
    } else {
      console.log("test0");
      alert("Failed to save doctor.");
      document.getElementById("modal").style.display = "none";
    }
  } catch (error) {
    alert("Error saving doctor. Please try again.");
    document.getElementById("modal").style.display = "none";
    console.error("Add doctor error:", error);
  }
}


