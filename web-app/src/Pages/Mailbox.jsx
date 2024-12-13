import React from 'react';

const Mailbox = ({ emails, loadEmails }) => {
  return (
    <div>
      <h2>Your Mailbox</h2>
      <button onClick={loadEmails}>Refresh</button>
      <button id="delete-selected">Delete Selected</button>
      <ul id="emails-list">
        {emails.map((email) => (
          <li key={email.id} draggable="true" data-id={email.id}>
            <input type="checkbox" className="email-checkbox" data-id={email.id} />
            <strong>Subject:</strong> {email.subject} <br />
            <strong>From:</strong> {email.sender} <br />
            <strong>To:</strong> {email.to}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Mailbox;
