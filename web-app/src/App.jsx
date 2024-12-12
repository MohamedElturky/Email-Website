import React, { useState } from 'react';
import ComposeEmail from './components/ComposeEmail';
import Mailbox from './components/Mailbox';
import './styles.css';

const App = () => {
  const [theme, setTheme] = useState('light');
  const [emails, setEmails] = useState([]); // State to store fetched emails

  const toggleTheme = (newTheme) => {
    document.documentElement.setAttribute('data-theme', newTheme);
    setTheme(newTheme);
  };

  const loadEmails = async () => {
    try {
      const response = await fetch('http://localhost:5000/api/emails'); // Replace with your backend URL
      if (!response.ok) throw new Error('Failed to load emails');
      const data = await response.json();
      setEmails(data); // Update emails state
    } catch (error) {
      console.error(error.message);
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
          <button onClick={loadEmails}>Load Emails</button> {/* New Load Emails button */}
        </div>
      </header>
      <ComposeEmail onSend={(email) => console.log('Email sent:', email)} />
      <Mailbox emails={emails} onDelete={(id) => console.log('Delete email:', id)} />
    </div>
  );
};

export default App;
