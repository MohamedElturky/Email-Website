import { useState } from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

const ComposeEmail = ({ onSend, defaultSender }) => {
  const [email, setEmail] = useState({
    from: defaultSender,
    to: '',
    subject: '',
    body: '',
    attachments: [],
    priority: 1, // Default priority
  });

  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
    if (name === 'to') setError('');
  };

  const validateEmails = (emails) => {
    const emailList = emails.split(',').map(email => email.trim());
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailList.every(email => emailRegex.test(email));
  };

  const handleAttachment = (e) => {
    const files = Array.from(e.target.files);
    setEmail({ ...email, attachments: [...email.attachments, ...files] });
  };

  const handleRemoveAttachment = (index) => {
    const updatedAttachments = email.attachments.filter((_, i) => i !== index);
    setEmail({ ...email, attachments: updatedAttachments });
  };

  const handleSend = async () => {
    const recipientList = email.to.split(',').map(recipient => recipient.trim());

    if (!validateEmails(email.to)) {
      setError('One or more recipient emails are invalid. Please check the format.');
      return;
    }

    const formData = new FormData();
    formData.append('from', email.from);
    formData.append('to', recipientList.join(','));
    formData.append('subject', email.subject);
    formData.append('body', email.body);
    formData.append('priority', email.priority); // Send priority

    // Append attachments to FormData
    email.attachments.forEach(file => {
      formData.append('attachments', file); // Append each file
    });

    setIsLoading(true);
    try {
      await axios.post('http://localhost:5000/api/send-email', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      onSend(email);
      alert('Email sent successfully!');
      setEmail({ from: defaultSender, to: '', subject: '', body: '', attachments: [], priority: 1 });
    } catch (error) {
      console.error('Error sending email:', error);
      alert('Failed to send email. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2>Compose Email</h2>
      <input type="email" name="from" value={email.from} readOnly placeholder="Sender" />
      <input
        type="text"
        name="to"
        placeholder="Recipients (separate emails with commas)"
        value={email.to}
        onChange={handleChange}
      />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <input
        type="text"
        name="subject"
        placeholder="Subject"
        value={email.subject}
        onChange={handleChange}
      />
      <textarea
        name="body"
        placeholder="Write your message here"
        value={email.body}
        onChange={handleChange}
      ></textarea>
      <select name="priority" value={email.priority} onChange={handleChange}>
        <option value="1">Priority 1 (High)</option>
        <option value="2">Priority 2 (Medium)</option>
        <option value="3">Priority 3 (Low)</option>
        <option value="4">Priority 4 (None)</option>
      </select>

      {/* Attachment Input */}
      <div>
        <input type="file" multiple onChange={handleAttachment} />
        {email.attachments.length > 0 && (
          <ul>
            {email.attachments.map((file, index) => (
              <li key={index}>
                {file.name} ({(file.size / 1024).toFixed(2)} KB)
                <button
                  type="button"
                  onClick={() => handleRemoveAttachment(index)}
                  style={{ marginLeft: '10px' }}
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      <button onClick={handleSend} disabled={isLoading}>
        {isLoading ? 'Sending...' : 'Send'}
      </button>
    </div>
  );
};

ComposeEmail.propTypes = {
  onSend: PropTypes.func.isRequired,
  defaultSender: PropTypes.string.isRequired,
};

export default ComposeEmail;