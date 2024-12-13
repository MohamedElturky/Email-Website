import React, { useState } from 'react';
import axios from 'axios'; // Import axios
import ComposeEmail from './components/ComposeEmail';
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

  const openMailboxWindow = () => {
    const mailboxWindow = window.open('', '_blank', 'width=600,height=500');
    mailboxWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>Mailbox</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            background-color: #f0f4f8;
            color: #333;
            padding: 20px;
            text-align: center;
          }
          h2 {
            color: #4CAF50;
            font-size: 24px;
          }
          button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin: 10px 0;
            font-size: 16px;
          }
          button:hover {
            background-color: #45a049;
          }
          #emails-list {
            list-style-type: none;
            padding: 0;
            margin: 0;
          }
          #emails-list li {
            background-color: #ffffff;
            margin: 10px 0;
            padding: 15px;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
            cursor: move;
          }
        </style>
      </head>
      <body>
        <h2>Your Mailbox</h2>
        <button id="refresh">Refresh</button>
        <button id="delete-selected">Delete Selected</button>
        <ul id="emails-list">
          ${emails
            .map(
              (email) => `
            <li id="email-${email.id}" draggable="true" data-id="${email.id}" ondragstart="handleDragStart(event)">
              <input type="checkbox" class="email-checkbox" data-id="${email.id}" /> 
              <strong>Subject:</strong> ${email.subject} <br />
              <strong>From:</strong> ${email.sender} <br />
              <strong>To:</strong> ${email.to}
            </li>
          `
            )
            .join('')}
        </ul>
      </body>
      <script>
        let draggedEmail = null;

        function handleDragStart(event) {
          draggedEmail = event.target;
          event.target.style.opacity = '0.5';
        }

        function handleDragOver(event) {
          event.preventDefault();
        }

        function handleDrop(event) {
          event.preventDefault();
          const targetEmail = event.target.closest('li');
          if (targetEmail && targetEmail !== draggedEmail) {
            const emailsList = document.getElementById('emails-list');
            const allEmails = Array.from(emailsList.querySelectorAll('li'));
            const draggedEmailIndex = allEmails.indexOf(draggedEmail);
            const targetEmailIndex = allEmails.indexOf(targetEmail);
            if (draggedEmailIndex < targetEmailIndex) {
              emailsList.insertBefore(draggedEmail, targetEmail.nextSibling);
            } else {
              emailsList.insertBefore(draggedEmail, targetEmail);
            }
          }
        }

        function addDragEvents() {
          const emailItems = document.querySelectorAll('#emails-list li');
          emailItems.forEach(item => {
            item.addEventListener('dragstart', handleDragStart);
            item.addEventListener('dragover', handleDragOver);
            item.addEventListener('drop', handleDrop);
            item.addEventListener('dragend', () => {
              draggedEmail.style.opacity = '1';
              draggedEmail = null;
            });
          });
        }

        addDragEvents();

        const refreshButton = document.getElementById('refresh');
        refreshButton.addEventListener('click', async () => {
          await loadEmails();
          renderMailbox();
        });

        // Delete and  buttons event listeners would be here (same as before)
      </script>
      </html>
    `);

    const renderMailbox = () => {
      mailboxWindow.document.body.innerHTML = `
        <h2>Mailbox</h2>
        <button id="refresh">Refresh</button>
        <button id="delete-selected">Delete Selected</button>
        <ul id="emails-list">
          ${emails
            .map(
              (email) => `
            <li id="email-${email.id}" draggable="true" data-id="${email.id}" ondragstart="handleDragStart(event)">
              <input type="checkbox" class="email-checkbox" data-id="${email.id}" /> 
              <strong>Subject:</strong> ${email.subject} <br />
              <strong>From:</strong> ${email.sender} <br />
              <strong>To:</strong> ${email.to}
            </li>
          `
            )
            .join('')}
        </ul>
      `;

      addDragEvents(); // Add the drag event listeners after rendering
    };

    renderMailbox();
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
      <button onClick={openMailboxWindow}>Open Mailbox</button>
    </div>
  );
};

export default App;
