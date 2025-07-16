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


// Import modules
import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// 📌 Add Doctor Button → Open Modal
document.getElementById('addDocBtn')?.addEventListener('click', () => {
  openModal('addDoctor');
});

// 📌 Load doctor cards when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  loadDoctorCards();
});

// 📌 Fetch and display all doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error('Failed to load doctors:', error);
  }
}

// 📌 Render doctors into the content area
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById('content');
  contentDiv.innerHTML = '';

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = '<p>No doctors found.</p>';
    return;
  }

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// 📌 Filter event listeners
document.getElementById('searchBar')?.addEventListener('input', filterDoctorsOnChange);
document.getElementById('filterTime')?.addEventListener('change', filterDoctorsOnChange);
document.getElementById('filterSpecialty')?.addEventListener('change', filterDoctorsOnChange);

// 📌 Handle filtering logic
async function filterDoctorsOnChange() {
  const name = document.getElementById('searchBar')?.value.trim() || '';
  const time = document.getElementById('filterTime')?.value.trim() || '';
  const specialty = document.getElementById('filterSpecialty')?.value.trim() || '';

  try {
    const { doctors } = await filterDoctors(name || null, time || null, specialty || null);

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById('content');
      contentDiv.innerHTML = '<p>No doctors found with the given filters.</p>';
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('Failed to filter doctors. Please try again.');
  }
}

// 📌 Add new doctor
window.adminAddDoctor = async function () {
  const name = document.getElementById('docName')?.value.trim();
  const email = document.getElementById('docEmail')?.value.trim();
  const phone = document.getElementById('docPhone')?.value.trim();
  const password = document.getElementById('docPassword')?.value.trim();
  const specialty = document.getElementById('docSpecialty')?.value.trim();

  // Get selected times (checkboxes with class "availability-time")
  const timeNodes = document.querySelectorAll('.availability-time:checked');
  const times = Array.from(timeNodes).map(input => input.value);

  const token = localStorage.getItem('token');
  if (!token) {
    alert('You must be logged in as admin to perform this action.');
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    times
  };

  try {
    const result = await saveDoctor(doctor, token);

    if (result.success) {
      alert('Doctor added successfully!');
      document.getElementById('addDoctorModal').classList.remove('is-active'); // Close modal
      loadDoctorCards(); // Refresh list
    } else {
      alert(result.message || 'Failed to add doctor.');
    }
  } catch (error) {
    console.error('Add doctor error:', error);
    alert('An error occurred while saving the doctor.');
  }
};
