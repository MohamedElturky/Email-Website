import React from 'react';
import EmailItem from './EmailItem';

const Mailbox = ({ emails, onDelete }) => (
  <div>
    <h2>Mailbox</h2>
    {emails.map((email) => (
      <EmailItem key={email.id} email={email} onDelete={onDelete} />
    ))}
  </div>
);

export default Mailbox;
