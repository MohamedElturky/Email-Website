import { useState, useEffect } from 'react';
import axios from 'axios'; // For API calls
import ComposeEmail from './components/ComposeEmail'; // Your email composing component
import './styles.css'; // Add your custom styles here

const App = () => {
  const [theme, setTheme] = useState('light'); // Theme state
  const [emails, setEmails] = useState([]); // State to store emails
  const [currentPage, setCurrentPage] = useState('login'); // Current page (login, register, home, mailbox)
  const [draggedEmail, setDraggedEmail] = useState(null); // State for drag-and-drop
  const [user, setUser] = useState(null); // User state (null if not logged in)
  const [form, setForm] = useState({ username: '', password: '' }); // Form state for login/register

  // Toggle between themes
  const toggleTheme = (newTheme) => {
    document.documentElement.setAttribute('data-theme', newTheme);
    setTheme(newTheme);
  };

  // Handle input changes in login/register form
  const handleInputChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Login user
  const loginUser = async () => {
    try {
      const response = await axios.post('http://localhost:5000/api/login', form); // Replace with your backend URL
      setUser(response.data.user); // Set the logged-in user
      setCurrentPage('home'); // Redirect to home page
    } catch (error) {
      console.error('Login failed:', error.message);
    }
  };

  // Register user
  const registerUser = async () => {
    try {
      await axios.post('http://localhost:5000/api/register', form); // Replace with your backend URL
      alert('Registration successful. Please log in.');
      setCurrentPage('login'); // Redirect to login page after registration
    } catch (error) {
      console.error('Registration failed:', error.message);
    }
  };

  // Load emails from the backend API
  const loadEmails = async () => {
    try {
      const response = await axios.get('http://localhost:5000/api/emails'); // Replace with your backend URL
      setEmails(response.data); // Update emails with response data
    } catch (error) {
      console.error('Error loading emails:', error.message);
    }
  };

  // Handle drag events
  const handleDragStart = (event, emailId) => {
    setDraggedEmail(emailId);
    event.target.style.opacity = '0.5';
  };

  const handleDragEnd = (event) => {
    setDraggedEmail(null);
    event.target.style.opacity = '1';
  };

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = (event, targetEmailId) => {
    event.preventDefault();
    if (draggedEmail !== targetEmailId) {
      const reorderedEmails = [...emails];
      const draggedIndex = reorderedEmails.findIndex((email) => email.id === draggedEmail);
      const targetIndex = reorderedEmails.findIndex((email) => email.id === targetEmailId);
      const [draggedEmailObj] = reorderedEmails.splice(draggedIndex, 1);
      reorderedEmails.splice(targetIndex, 0, draggedEmailObj);
      setEmails(reorderedEmails);
    }
  };

  // Delete selected emails
  const deleteSelectedEmails = () => {
    const selectedEmails = document.querySelectorAll('.email-checkbox:checked');
    const selectedIds = Array.from(selectedEmails).map((checkbox) => parseInt(checkbox.dataset.id));
    const filteredEmails = emails.filter((email) => !selectedIds.includes(email.id));
    setEmails(filteredEmails);
  };

  useEffect(() => {
    if (currentPage === 'mailbox') {
      loadEmails();
    }
  }, [currentPage]);

  return (
    <div>
      {currentPage === 'login' && (
        <div>
          <h2>Login</h2>
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={form.username}
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
            Don&apos;t have an account?{' '}
            <button onClick={() => setCurrentPage('register')}>Register</button>
          </p>
        </div>
      )}
      {currentPage === 'register' && (
        <div>
          <h2>Register</h2>
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={form.username}
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
            Already have an account?{' '}
            <button onClick={() => setCurrentPage('login')}>Login</button>
          </p>
        </div>
      )}
      {currentPage === 'home' && (
        <>
          <header>
            <h1>Email Application</h1>
            {user && <p>Welcome, {user.username}!</p>}
            <button onClick={() => toggleTheme('light')} disabled={theme === 'light'}>
              Light Theme
            </button>
            <button onClick={() => toggleTheme('dark')} disabled={theme === 'dark'}>
              Dark Theme
            </button>
            
            <button onClick={() => toggleTheme('colorful')} disabled={theme === 'colorful'}>
               Colorful Theme
            </button>
          </header>

          <ComposeEmail
            onSend={(email) => {
              setEmails([...emails, email]);
            }}
            defaultSender={user?.email || ''}
          />
          <button onClick={() => setCurrentPage('mailbox')}>Open Mailbox</button>
        </>
      )}
      {currentPage === 'mailbox' && (
        <div>
          <header>
            <h2>Your Mailbox</h2>
          </header>
          <button onClick={() => setCurrentPage('home')}>Back to Home</button>
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
                <input type="checkbox" className="email-checkbox" data-id={email.id} />
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
