import React, { useState } from 'react';

const ComposeEmail = ({ onSend }) => {
  const [email, setEmail] = useState({
    to: '',
    subject: '',
    body: '',
    attachments: [],
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
  };

  const handleSend = () => {
    onSend(email);
    setEmail({ to: '', subject: '', body: '', attachments: [] });
  };

  return (
    <div>
      <h2>Compose Email</h2>
      <input
        type="email"
        name="to"
        placeholder="To"
        value={email.to}
        onChange={handleChange}
      />
      <input
        type="text"
        name="subject"
        placeholder="Subject"
        value={email.subject}
        onChange={handleChange}
      />
      <textarea
        name="body"
        placeholder="Body"
        value={email.body}
        onChange={handleChange}
      />
      <button onClick={handleSend}>Send</button>
    </div>
  );
};

export default ComposeEmail;
