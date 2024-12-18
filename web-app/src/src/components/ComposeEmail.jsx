import React, { useState } from 'react';
import axios from 'axios'; // Import axios

const ComposeEmail = ({ onSend }) => {
  const [email, setEmail] = useState({
    from: '', // Added from field
    to: '',
    subject: '',
    body: '',
    attachments: [],
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
  };

  // Handle file selection
  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    setEmail({ ...email, attachments: [...email.attachments, ...files] });
  };

  const handleSend = async () => {
    const formData = new FormData();
    formData.append('from', email.from);
    formData.append('to', email.to);
    formData.append('subject', email.subject);
    formData.append('body', email.body);
  
    email.attachments.forEach((file) => {
      formData.append('attachments', file);
    });
  
    try {
      const response = await axios.post('http://localhost:5000/api/send-email', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      if (response.status === 200) {
        onSend(email);
        alert('Email sent successfully');
        setEmail({ from: '', to: '', subject: '', body: '', attachments: [] });
      }
    } catch (error) {
      console.error('Error details:', error.response || error.message);
      alert('Failed to send email. Please check the console for details.');
    }
  };
  
  

  // Remove attachment
  const removeAttachment = (index) => {
    const newAttachments = email.attachments.filter((_, i) => i !== index);
    setEmail({ ...email, attachments: newAttachments });
  };

  return (
    <div>
      <h2>Compose Email</h2>
      <div>
        <label htmlFor="from">From:</label>
        <input
          type="email"
          id="from"
          name="from"
          placeholder="Your Email"
          value={email.from}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label htmlFor="to">To:</label>
        <input
          type="email"
          id="to"
          name="to"
          placeholder="Recipient's Email"
          value={email.to}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label htmlFor="subject">Subject:</label>
        <input
          type="text"
          id="subject"
          name="subject"
          placeholder="Email Subject"
          value={email.subject}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label htmlFor="body">Body:</label>
        <textarea
          id="body"
          name="body"
          placeholder="Write your message here"
          value={email.body}
          onChange={handleChange}
          required
        />
      </div>
      
      <div>
        {/* File attachment icon */}
        <label htmlFor="attachment" className="attachment-icon">
          <span className="material-icons">attach_file</span>
        </label>
        <input
          id="attachment"
          type="file"
          multiple
          style={{ display: 'none' }}
          onChange={handleFileChange}
        />
        
        {/* Display selected attachments */}
        <div>
          {email.attachments.length > 0 && (
            <ul>
              {email.attachments.map((file, index) => (
                <li key={index}>
                  {file.name} <button onClick={() => removeAttachment(index)}>Remove</button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <button onClick={handleSend} disabled={!email.from || !email.to || !email.subject || !email.body}>
        Send
      </button>
    </div>
  );
};

export default ComposeEmail;
