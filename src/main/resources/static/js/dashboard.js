// Set username from localStorage
document.addEventListener("DOMContentLoaded", () => {
  const username = localStorage.getItem("username") || "User";
  document.getElementById("username").innerText = username;
  loadRecentNotes();
});

// Logout function
function logout() {
  localStorage.removeItem("username");
  window.location.href = "login.html"; // or your login page
}

// Modal logic
const modal = document.getElementById("uploadModal");
const uploadBtn = document.getElementById("uploadBtn");
const closeBtn = document.getElementById("closeModal");

uploadBtn.onclick = () => modal.style.display = "block";
closeBtn.onclick = () => modal.style.display = "none";
window.onclick = (event) => {
  if (event.target === modal) modal.style.display = "none";
};

// Upload form
document.getElementById("uploadForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const topic = document.getElementById("topic").value.trim();
  const driveLink = document.getElementById("driveLink").value.trim();

  if (!topic || !driveLink) {
    alert("Please fill all fields.");
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/notes/upload", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ topic, driveLink })
    });

    if (response.ok) {
      alert("Note uploaded successfully!");
      modal.style.display = "none";
      document.getElementById("uploadForm").reset();
      loadRecentNotes();
    } else {
      alert("Failed to upload note.");
    }
  } catch (error) {
    console.error("Error uploading note:", error);
    alert("Something went wrong.");
  }
});

// Load notes from backend
async function loadRecentNotes() {
  try {
    const response = await fetch("http://localhost:8080/api/notes/all");
    const notes = await response.json();

    const noteList = document.querySelector(".note-list");
    noteList.innerHTML = "";

    if (notes.length === 0) {
      noteList.innerHTML = "<li>No notes uploaded yet.</li>";
      document.getElementById("total-notes").innerText = "0";
      return;
    }

    notes.reverse().forEach(note => {
      const li = document.createElement("li");
      const a = document.createElement("a");
      a.href = note.driveLink;
      a.target = "_blank";
      a.textContent = note.topic;
      li.appendChild(a);
      noteList.appendChild(li);
    });

    document.getElementById("total-notes").innerText = notes.length;
  } catch (err) {
    console.error("Failed to load notes:", err);
  }
}
