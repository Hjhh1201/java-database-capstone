/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/


import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor'


// === Function: Get All Doctors ===
export async function getDoctors() {
  try {
    console.log("666");
    const response = await fetch(`${DOCTOR_API}`);
    const data = await response.json();
    if(response.ok){
        return data.doctors;
    }
    return [];
  } catch (error) {
    console.error("Failed to fetch doctors:", error);
    return [];
  }
}

// === Function: Delete Doctor by ID ===
export async function deleteDoctor(id, token) {
  try {

    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
          method: "DELETE",
        });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor deleted",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Failed to delete doctor. Please try again.",
    };
  }
}


export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Doctor saved successfully"
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Failed to save doctor. Please try again."
    };
  }
}


// === Function: Filter Doctors ===
export async function filterDoctors(name, time, specialty) {
  try {
    // Normalize parameters to 'null' if not present
    name = name || 'null';
    time = time || 'null';
    specialty = specialty || 'null';

    console.log(name, time, specialty);

    const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${specialty}`);
    if (!response.ok) {
      console.error("Filter request failed:", response.status);
      return { doctors: [] };
    }

    const data = await response.json();
    return { doctors: data.doctors || [] };
  } catch (error) {
    alert("Error filtering doctors. Please try again.");
    console.error("Filter error:", error);
    return { doctors: [] };
  }
}


