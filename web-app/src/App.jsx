import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import axios from 'axios'; // Import axios
import Homepage from './pages/Homepage';
import Mailbox from './pages/Mailbox';
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
      const response = await axios.get('http://localhost:5000/api/emails'); // Replace with your backend URL
      setEmails(response.data); // Update emails state with the response data
    } catch (error) {
      console.error('Error loading emails:', error.message);
    }
  };

  return (
    <BrowserRouter>
      <div>
        <Routes>
          <Route index element={<Homepage />} />
          <Route path="/mailbox" element={<Mailbox emails={emails} loadEmails={loadEmails} />} />
        </Routes>
        <Link to="/mailbox" style={{ textDecoration: 'none' }}>
              <button>
                Mailbox
              </button>
        </Link>
      </div>
    </BrowserRouter>
  );
};

export default App;
