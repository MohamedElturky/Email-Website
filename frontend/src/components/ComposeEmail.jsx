import { useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useEffect } from "react";

const ComposeEmail = ({ onSend, defaultSender }) => {
  const [email, setEmail] = useState({
    from: defaultSender,
    to: "",
    subject: "",
    body: "",
    attachments: [],
    priority: 1, // Default priority
  });

  useEffect(() => {
    // Retrieve user data from localStorage
    const user = JSON.parse(localStorage.getItem("user"));
    const userId = user?.id || null;

    if (userId) {
      setEmail((prevEmail) => ({ ...prevEmail, from: userId.toString() }));
    } else {
      console.error("User ID not found in localStorage");
    }
  }, []);

  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
    if (name === "to") setError("");
  };

  const validateEmails = (emails) => {
    const emailList = emails.split(",").map((email) => email.trim());
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailList.every((email) => emailRegex.test(email));
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
    // Validate recipient email addresses
    const recipientList = email.to
      .split(",")
      .map((recipient) => recipient.trim());
    if (!validateEmails(email.to)) {
      setError(
        "One or more recipient emails are invalid. Please check the format."
      );
      return;
    }
    console.log("Sender ID (email.from):", email.from);

    // Map data to backend expected format
    const emailData = {
      senderId: parseInt(email.from, 10), // Backend expects sender ID as an integer
      receiversEmailAddresses: recipientList, // Array of recipient emails
      topic: email.subject, // Subject maps to topic
      body: email.body, // Body remains the same
      priority: parseInt(email.priority, 10), // Priority as an integer
    };

    setIsLoading(true);
    try {
      // Send the POST request to the backend
      await axios.post("http://localhost:8081/api/email", emailData, {
        headers: { "Content-Type": "application/json" },
      });

      // Success actions
      onSend(email);
      alert("Email sent successfully!");
      setEmail({
        from: JSON.parse(localStorage.getItem("user"))?.id?.toString() || "",
        to: "",
        subject: "",
        body: "",
        attachments: [], // Clear attachments
        priority: 1, // Reset priority to default
      });
    } catch (error) {
      console.error(
        "Error sending email:",
        error.response?.data || error.message
      );
      alert("Failed to send email. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2>Compose Email</h2>
      <p>
        <strong>Sender:</strong> Logged-in User
      </p>
      <input
        type="text"
        name="to"
        placeholder="Recipients (separate emails with commas)"
        value={email.to}
        onChange={handleChange}
      />
      {error && <p style={{ color: "red" }}>{error}</p>}
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
                  style={{ marginLeft: "10px" }}
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      <button onClick={handleSend} disabled={isLoading}>
        {isLoading ? "Sending..." : "Send"}
      </button>
    </div>
  );
};

ComposeEmail.propTypes = {
  onSend: PropTypes.func.isRequired,
  defaultSender: PropTypes.string.isRequired,
};

export default ComposeEmail;
