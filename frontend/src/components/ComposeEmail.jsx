import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import axios from "axios";

const ComposeEmail = ({ onSend, defaultSender, userId}) => {
  const [email, setEmail] = useState({
    from: defaultSender,
    to: "",
    subject: "",
    body: "",
    attachments: [],
    priority: 1,
  });

  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    const userId = user?.id || null;

    if (userId) {
      setEmail((prevEmail) => ({ ...prevEmail, from: userId.toString() }));
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmail({ ...email, [name]: value });
    if (name === "to") setError("");
  };

  const handleAttachment = async (e) => {
    const files = Array.from(e.target.files);
    setEmail((prevEmail) => ({
      ...prevEmail,
      attachments: [...prevEmail.attachments, ...files],
    }));

    // Process the first file to populate subject, body, and priority
    if (files.length > 0) {
      const file = files[0];
      if (file.type === "text/plain" || file.type === "application/json") {
        const content = await file.text();

        // Attempt to parse JSON or use plain text
        try {
          const parsedData = JSON.parse(content);
          setEmail((prevEmail) => ({
            ...prevEmail,
            subject: parsedData.subject || prevEmail.subject,
            body: parsedData.body || prevEmail.body,
            priority: parsedData.priority || prevEmail.priority,
          }));
        } catch {
          // If not JSON, treat as plain text
          setEmail((prevEmail) => ({
            ...prevEmail,
            subject: prevEmail.subject || "Extracted from File",
            body: content || prevEmail.body,
          }));
        }
      }
    }
  };

  const handleRemoveAttachment = (index) => {
    const updatedAttachments = email.attachments.filter((_, i) => i !== index);
    setEmail({ ...email, attachments: updatedAttachments });
  };

  const handleSend = async () => {
    const recipientList = email.to.split(",").map((recipient) => recipient.trim());

    const emailData = {
        senderId: userId,
        receiversEmailAddresses: recipientList,
        topic: email.subject,
        body: email.body,
        priority: parseInt(email.priority, 10),
    };

    setIsLoading(true);
    try {
        // Send email data
        const response = await axios.post("http://localhost:8080/api/email", emailData, {
            headers: { "Content-Type": "application/json" },
        });

        const emailId = response.data.id;

        // Prepare to upload attachments if present
        if (email.attachments.length > 0) {
            const formData = new FormData();
            email.attachments.forEach((file) => {
                formData.append("files", file);
            });

            // Send the attachments to the backend
            const attachmentResponse = await axios.post(`http://localhost:8080/api/attachment`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                params: { emailId }, // Ensure emailId is included in the request
            });

            if (attachmentResponse.status !== 200) {
                throw new Error("Failed to upload attachments.");
            }
        }

        onSend(email);
        alert("Email sent successfully!");
        setEmail({
            from: defaultSender,
            to: "",
            subject: "",
            body: "",
            attachments: [],
            priority: 1,
        });
    } catch (error) {
        console.error("Error sending email:", error.message);

        // Log the detailed error response if available
        if (error.response) {
            console.error("Error details:", error.response.data);
            alert(`Failed to send email: ${error.response.data.message || "Unknown error"}`);
        } else {
            alert("Failed to send email. Please try again.");
        }
    } finally {
        setIsLoading(false);
    }
};
  return (
    <div>
      <h2>Compose Email</h2>
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

      <div>
        <label htmlFor="file-upload" style={{ cursor: "pointer" }}>
          <i className="fas fa-upload" style={{ marginRight: "8px" }}></i>
          Upload Attachments
        </label>
        <input
          id="file-upload"
          type="file"
          multiple
          onChange={handleAttachment}
          style={{ display: "none" }}
        />
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
  userId: PropTypes.number.isRequired
};

export default ComposeEmail;