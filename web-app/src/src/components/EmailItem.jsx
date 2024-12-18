import React from 'react';

const EmailItem = ({ email, onDelete }) => (
  <div className="email-item">
    <h3>{email.subject}</h3>
    <p><strong>From:</strong> {email.sender}</p>
    <p>{email.body}</p>
    <button onClick={() => onDelete(email.id)}>Delete</button>
  </div>
);

export default EmailItem;
