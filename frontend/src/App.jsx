import { useState } from "react";
import axios from "axios";
import ComposeEmail from "./components/ComposeEmail";
import ContactManager from "./components/ContactManager";
import Mailbox from "./components/Mailbox";
import "./styles.css";

const App = () => {
  const [theme, setTheme] = useState("light");
  const [emails, setEmails] = useState([]);
  const [currentPage, setCurrentPage] = useState("login");
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

      // Check if the response contains user data
      if (!response.data || !response.data.id) {
        throw new Error("Invalid login credentials."); // Reject login if user data is missing
      }

      console.log("Login successful:", response.data);

      // Store user data locally
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
          {currentPage === "home" && user && (
            <ContactManager userId={user.id} />
          )}
        </>
      )}
      {currentPage === "mailbox" && (
        <div>
          <header>
            <h2>Your Mailbox</h2>
          </header>
          <button onClick={() => setCurrentPage("home")}>Back to Home</button>
          <Mailbox user={user} />
        </div>
      )}
    </div>
  );
};

export default App;
