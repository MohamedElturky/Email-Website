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
  });

  const [error, setError] = useState(''); // State for validation errors
  const [isLoading, setIsLoading] = useState(false); // Loading state

  // Handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
    if (name === 'to') setError(''); // Clear error when typing
  };

  // Validate email format for multiple recipients
  const validateEmails = (emails) => {
    const emailList = emails.split(',').map((email) => email.trim());
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Regex for email validation
    return emailList.every((email) => emailRegex.test(email)); // Returns true if all emails are valid
  };

  // Handle file attachment
  const handleAttachment = (e) => {
    const files = Array.from(e.target.files);
    setEmail({ ...email, attachments: [...email.attachments, ...files] });
  };

  // Remove an attachment
  const handleRemoveAttachment = (index) => {
    const updatedAttachments = email.attachments.filter((_, i) => i !== index);
    setEmail({ ...email, attachments: updatedAttachments });
  };

  // Handle email send
  const handleSend = async () => {
    const recipientList = email.to.split(',').map((recipient) => recipient.trim());

    // Validate the emails
    if (!validateEmails(email.to)) {
      setError('One or more recipient emails are invalid. Please check the format.');
      return;
    }

    // Create FormData for file upload
    const formData = new FormData();
    formData.append('from', email.from);
    formData.append('to', recipientList.join(',')); // Send recipients as a comma-separated string
    formData.append('subject', email.subject);
    formData.append('body', email.body);

    // Append attachments to FormData
    email.attachments.forEach((file) => {
      formData.append('attachments', file);
    });

    setIsLoading(true); // Set loading state
    try {
      await axios.post('http://localhost:5000/api/send-email', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      onSend(email);
      alert('Email sent successfully!');
      // Clear the form after sending
      setEmail({
        from: defaultSender,
        to: '',
        subject: '',
        body: '',
        attachments: [],
      });
    } catch (error) {
      console.error('Error sending email:', error);
      alert('Failed to send email. Please try again.');
    } finally {
      setIsLoading(false); // Reset loading state
    }
  };

  return (
    <div>
      <h2>Compose Email</h2>
      <input
        type="email"
        name="from"
        value={email.from}
        readOnly
        placeholder="Sender"
      />
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

      {/* Attachment Section */}
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
