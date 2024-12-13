import React, { useState } from 'react';
import ComposeEmail from '../components/ComposeEmail';
import axios from 'axios';
import '../styles.css';

const Homepage = () => {
  const [theme, setTheme] = useState('light');
  const [emails, setEmails] = useState([]); // State to store fetched emails

  const toggleTheme = (newTheme) => {
    document.documentElement.setAttribute('data-theme', newTheme);
    setTheme(newTheme);
  };

  const loadEmails = async () => {
    try {
      const response = await axios.get('http://localhost:5000/api/emails'); // Replace with your backend URL
      setEmails(response.data); // Update emails state with the response data
    } catch (error) {
      console.error('Error loading emails:', error.message);
    }
  };
  return (
      <div>
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
            setEmails([...emails, email]); // Add sent email to the state
          }}
        />
      </div>
      
  );
};

export default Homepage;