import React, { useState, useEffect } from 'react';
import axios from 'axios'; // For API calls
import ComposeEmail from './components/ComposeEmail'; // Your email composing component
import './styles.css'; // Add your custom styles here

const App = () => {
  const [theme, setTheme] = useState('light'); // Theme state
  const [emails, setEmails] = useState([]); // State to store emails
  const [currentPage, setCurrentPage] = useState('home'); // Current page (home or mailbox)
  const [draggedEmail, setDraggedEmail] = useState(null); // State for drag-and-drop

  // Toggle between themes
  const toggleTheme = (newTheme) => {
    document.documentElement.setAttribute('data-theme', newTheme);
    setTheme(newTheme);
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

  // Handle drag start
  const handleDragStart = (event, emailId) => {
    setDraggedEmail(emailId); // Set the dragged email's ID
    event.target.style.opacity = '0.5'; // Make the dragged email semi-transparent
  };

  // Handle drag end
  const handleDragEnd = (event) => {
    setDraggedEmail(null); // Reset dragged email state
    event.target.style.opacity = '1'; // Reset the email's opacity
  };

  // Handle drag over (to allow dropping)
  const handleDragOver = (event) => {
    event.preventDefault(); // Prevent default to allow drop
  };

  // Handle drop (to reorder emails)
  const handleDrop = (event, targetEmailId) => {
    event.preventDefault();
    if (draggedEmail !== targetEmailId) {
      const reorderedEmails = [...emails];
      const draggedIndex = reorderedEmails.findIndex((email) => email.id === draggedEmail);
      const targetIndex = reorderedEmails.findIndex((email) => email.id === targetEmailId);
      const [draggedEmailObj] = reorderedEmails.splice(draggedIndex, 1); // Remove dragged email
      reorderedEmails.splice(targetIndex, 0, draggedEmailObj); // Insert dragged email at target position
      setEmails(reorderedEmails); // Update emails state with reordered emails
    }
  };

  // Delete selected emails
  const deleteSelectedEmails = () => {
    const selectedEmails = document.querySelectorAll('.email-checkbox:checked');
    const selectedIds = Array.from(selectedEmails).map((checkbox) => parseInt(checkbox.dataset.id));
    const filteredEmails = emails.filter((email) => !selectedIds.includes(email.id));
    setEmails(filteredEmails); // Update emails state with remaining emails
  };

  // Render the mailbox page
  const renderMailbox = () => (
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
            id={`email-${email.id}`}
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
  );

  // UseEffect to fetch emails when the mailbox page is opened
  useEffect(() => {
    if (currentPage === 'mailbox') {
      loadEmails();
    }
  }, [currentPage]);

  return (
    <div>
      {currentPage === 'home' ? (
        <>
          <header>
            <h1>Email Application</h1>
            <div>
              <button onClick={() => toggleTheme('light')} disabled={theme === 'light'}>
                Light Theme
              </button>
              <button onClick={() => toggleTheme('dark')} disabled={theme === 'dark'}>
                Dark Theme
              </button>
              <button onClick={() => toggleTheme('colorful')} disabled={theme === 'colorful'}>
                Colorful Theme
              </button>
            </div>
          </header>
          <ComposeEmail
            onSend={(email) => {
              console.log('Email sent:', email);
              setEmails([...emails, email]); // Add sent email to the emails state
            }}
          />
          <button onClick={() => setCurrentPage('mailbox')}>Open Mailbox</button>
        </>
      ) : (
        renderMailbox()
      )}
    </div>
  );
};

export default App;
