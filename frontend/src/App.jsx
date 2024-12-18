import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import ComposeEmail from "./components/ComposeEmail";
import ContactManager from "./components/ContactManager";
import "./styles.css";

const App = () => {
  const [theme, setTheme] = useState("light");
  const [emails, setEmails] = useState([]);
  const [currentPage, setCurrentPage] = useState("login");
  const [draggedEmail, setDraggedEmail] = useState(null);
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ username: "", password: "" });

  const toggleTheme = (newTheme) => {
    document.documentElement.setAttribute("data-theme", newTheme);
    setTheme(newTheme);
  };

  const handleInputChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  // Login user
  const loginUser = async () => {
    try {
      const response = await axios.get("http://localhost:8081/api/user/auth", {
        params: {
          emailAddress: form.email,
          password: form.password,
        },
      });

      console.log("Login successful:", response.data);

      // Store user data locally (optional)
      const user = response.data;
      localStorage.setItem("user", JSON.stringify(user));

      // Navigate to home page
      setUser(user);
      setCurrentPage("home");
    } catch (error) {
      console.error("Login failed:", error.response?.data || error.message);
      alert("Login failed. Please check your email and password.");
    }
  };

  // Register user
  const registerUser = async () => {
    try {
      // Use axios to send a POST request with query parameters
      const response = await axios.post(
        "http://localhost:8081/api/user/register",
        null,
        {
          params: {
            emailAddress: form.email, // Assuming form contains `email` and `password` fields
            password: form.password,
          },
        }
      );
      console.log("Registration successful:", response.data);

      // Display success message
      alert(
        `Registration successful for ${response.data.emailAddress}. Please log in.`
      );

      // Redirect to login page
      setCurrentPage("login");
    } catch (error) {
      // Handle errors
      console.error(
        "Registration failed:",
        error.response?.data || error.message
      );
      alert("Registration failed. Please try again.");
    }
  };

  const loadEmails = useCallback(async () => {
    try {
      if (user) {
        const response = await axios.get(
          "http://localhost:8081/api/email/all",
          {
            params: { userId: user.id },
          }
        );

        // Map response data to include 'from', 'to', and 'subject'
        const updatedEmails = response.data.map((email) => ({
          ...email,
          from: email.senderId, // You might want to map senderId to a name/email
          to: email.receiversEmailAddresses.join(", "), // Join receivers if there are multiple
          subject: email.topic, // Map 'topic' to 'subject'
        }));

        setEmails(updatedEmails);
      }
    } catch (error) {
      console.error("Error loading emails:", error.message);
    }
  }, [user]);

  const handleDragStart = (event, emailId) => {
    setDraggedEmail(emailId);
    event.target.style.opacity = "0.5";
  };

  const handleDragEnd = (event) => {
    setDraggedEmail(null);
    event.target.style.opacity = "1";
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = (event, targetEmailId) => {
    event.preventDefault();
    if (draggedEmail !== targetEmailId) {
      const reorderedEmails = [...emails];
      const draggedIndex = reorderedEmails.findIndex(
        (email) => email.id === draggedEmail
      );
      const targetIndex = reorderedEmails.findIndex(
        (email) => email.id === targetEmailId
      );
      const [draggedEmailObj] = reorderedEmails.splice(draggedIndex, 1);
      reorderedEmails.splice(targetIndex, 0, draggedEmailObj);
      setEmails(reorderedEmails);
    }
  };

  const deleteSelectedEmails = async () => {
    // Get all the selected checkboxes
    const selectedEmails = document.querySelectorAll(".email-checkbox:checked");

    // Get the IDs of selected emails
    const selectedIds = Array.from(selectedEmails).map((checkbox) =>
      parseInt(checkbox.dataset.id)
    );

    try {
      // Send DELETE requests to the backend for each selected email
      for (const id of selectedIds) {
        await axios.delete("http://localhost:8081/api/email", {
          params: { id },
        });
      }

      // Filter out the deleted emails from the state
      const filteredEmails = emails.filter(
        (email) => !selectedIds.includes(email.id)
      );
      setEmails(filteredEmails); // Update the state to reflect deleted emails
    } catch (error) {
      console.error("Error deleting emails:", error);
      alert("Failed to delete some emails. Please try again.");
    }
  };

  useEffect(() => {
    if (currentPage === "mailbox") {
      loadEmails();
    }
  }, [currentPage, loadEmails]);

  return (
    <div>
      {currentPage === "login" && (
        <div>
          <h2>Login</h2>
          <input
            type="text"
            name="email"
            placeholder="Email"
            value={form.email}
            onChange={handleInputChange}
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={form.password}
            onChange={handleInputChange}
          />
          <button onClick={loginUser}>Login</button>
          <p>
            Don&apos;t have an account?{" "}
            <button onClick={() => setCurrentPage("register")}>Register</button>
          </p>
        </div>
      )}
      {currentPage === "register" && (
        <div>
          <h2>Register</h2>
          <input
            type="text"
            name="email"
            placeholder="Email"
            value={form.email}
            onChange={handleInputChange}
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={form.password}
            onChange={handleInputChange}
          />
          <button onClick={registerUser}>Register</button>
          <p>
            Already have an account?{" "}
            <button onClick={() => setCurrentPage("login")}>Login</button>
          </p>
        </div>
      )}
      {currentPage === "home" && (
        <>
          <header>
            <h1>Email Application</h1>
            {user && <p>Welcome, {user.username}!</p>}
            <button
              onClick={() => toggleTheme("light")}
              disabled={theme === "light"}
            >
              Light Theme
            </button>
            <button
              onClick={() => toggleTheme("dark")}
              disabled={theme === "dark"}
            >
              Dark Theme
            </button>
            <button
              onClick={() => toggleTheme("colorful")}
              disabled={theme === "colorful"}
            >
              Colorful Theme
            </button>
          </header>

          <ComposeEmail
            onSend={(email) => {
              setEmails([...emails, email]);
            }}
            defaultSender={user?.email || ""}
          />
          <button onClick={() => setCurrentPage("mailbox")}>
            Open Mailbox
          </button>
          <ContactManager />
        </>
      )}
      {currentPage === "mailbox" && (
        <div>
          <header>
            <h2>Your Mailbox</h2>
          </header>
          <button onClick={() => setCurrentPage("home")}>Back to Home</button>
          <button onClick={loadEmails}>Refresh</button>
          <button onClick={deleteSelectedEmails}>Delete Selected</button>
          <ul id="emails-list">
            {emails.map((email) => (
              <li
                key={email.id}
                draggable="true"
                onDragStart={(e) => handleDragStart(e, email.id)}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, email.id)}
                onDragEnd={handleDragEnd}
              >
                <input
                  type="checkbox"
                  className="email-checkbox"
                  data-id={email.id}
                />
                <strong>Subject:</strong> {email.subject} <br />
                <strong>From:</strong> {email.sender} <br />
                <strong>To:</strong> {email.to}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default App;
