import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import PropTypes from "prop-types";


// Create an Axios instance with a base URL
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
});

const Mailbox = ({ user }) => {
  const [folders, setFolders] = useState([]);
  const [emails, setEmails] = useState([]);
  const [currentFolderId, setCurrentFolderId] = useState(null);
  const [loading, setLoading] = useState(false);
  // Filter states
  const [filterSender, setFilterSender] = useState('');
  const [filterTopic, setFilterTopic] = useState('');
  const [filterBody, setFilterBody] = useState('');
  const [filterReceivers, setFilterReceivers] = useState('');
  const [filterDateOption, setFilterDateOption] = useState(''); // New state for filter type
const [dateTime, setDateTime] = useState(''); // For single date input
const [startDateTime, setStartDateTime] = useState('');
const [endDateTime, setEndDateTime] = useState('');
  const [filterAttachments, setFilterAttachments] = useState('');

  // States for managing attachments
  const [selectedEmailId, setSelectedEmailId] = useState(null);
  const [attachments, setAttachments] = useState([]);
  const [attachmentFiles, setAttachmentFiles] = useState([]);

    // State for sorting option
    const [sortOption, setSortOption] = useState('default')

  // Fetch all folders for the user
  const fetchFolders = async () => {
    try {
      const response = await apiClient.get("/folder/all", {
        params: { userId: user.id },
      });
      setFolders(response.data);
      if (response.data.length > 0) {
        setCurrentFolderId(response.data[0].id);
      }
    } catch (error) {
      console.error("Error fetching folders:", error.message);
    }
  };


  const fetchEmails = useCallback(async (folderId, sortBy) => {
    try {
      const response = await apiClient.get(`/email/folder/${sortBy}`, {
        params: { folderId },
      });
      setEmails(response.data);
    } catch (error) {
      console.error("Error fetching emails:", error.message);
    }
  }, [])
  
  // Fetch emails when currentFolderId or sortOption changes
  useEffect(() => {

    const fetchEmailsWrapper = async () => {
      await fetchEmails(currentFolderId, sortOption);
    }

    fetchEmailsWrapper()

  }, [currentFolderId, sortOption, fetchEmails]);


  // Apply filters and fetch filtered emails
  const applyFilters = async () => {
    try {
      let filteredEmails = [];

      // Fetch by sender if provided
    if (filterSender) {
      const senderResponse = await apiClient.get("/email/all/sender", {
        params: { userId: user.id, sender: filterSender },
      });
      filteredEmails = senderResponse.data;
    }

      // Fetch by topic
      if (filterTopic) {
        const topicResponse = await apiClient.get("/email/all/topic", {
          params: { userId: user.id, topic: filterTopic },
        });
        filteredEmails = filteredEmails.length
          ? filteredEmails.filter((email) => topicResponse.data.some((e) => e.id === email.id))
          : topicResponse.data;
      }

      // Fetch by body if provided
      if (filterBody) {
        const bodyResponse = await apiClient.get("/email/all/body", {
          params: { userId: user.id, body: filterBody },
        });
        filteredEmails = filteredEmails.length
          ? filteredEmails.filter((email) => bodyResponse.data.some((e) => e.id === email.id))
          : bodyResponse.data;
      }

     // Fetch by receivers if provided
    if (filterReceivers) {
      const receiversString = filterReceivers.split(",").map((email) => email.trim()).join(","); // Join email addresses into a string
      const receiversResponse = await apiClient.get("/email/all/receivers", {
        params: { userId: user.id, receiver: receiversString }, // Send as a string
      });

      filteredEmails = filteredEmails.length
        ? filteredEmails.filter((email) =>
            receiversResponse.data.some((e) => e.id === email.id)
          )
        : receiversResponse.data;
    }

     // Date filtering logic
  if (filterDateOption && dateTime) {
  let dateResponse;
  if (filterDateOption === 'after') {
    dateResponse = await apiClient.get("/email/all/on-or-after", {
      params: { userId: user.id, dateTime },
    });
    filteredEmails = filteredEmails.length
      ? filteredEmails.filter((email) => dateResponse.data.some((e) => e.id === email.id))
      : dateResponse.data;
  } else if (filterDateOption === 'before') {
    dateResponse = await apiClient.get("/email/all/on-or-before", {
      params: { userId: user.id, dateTime },
    });
    filteredEmails = filteredEmails.length
      ? filteredEmails.filter((email) => dateResponse.data.some((e) => e.id === email.id))
      : dateResponse.data;
  } else if (filterDateOption === 'between' && startDateTime && endDateTime) {
    dateResponse = await apiClient.get("/email/all/on-or-between", {
      params: { userId: user.id, startDateTime, endDateTime },
    });
    filteredEmails = filteredEmails.length
      ? filteredEmails.filter((email) => dateResponse.data.some((e) => e.id === email.id))
      : dateResponse.data;
  }
}

      // Fetch by attachments
      if (filterAttachments) {
        const attachmentsArray = filterAttachments.split(",").map((attachment) => attachment.trim());
        const attachmentsResponse = await apiClient.get("/email/all/attachments", {
          params: { userId: user.id, attachments: attachmentsArray },
        });
        filteredEmails = filteredEmails.length
          ? filteredEmails.filter((email) => attachmentsResponse.data.some((e) => e.id === email.id))
          : attachmentsResponse.data;
      }

      // Set the filtered emails to state
      setEmails(filteredEmails);
    } catch (error) {
      console.error("Error fetching filtered emails:", error.message);
    }
  };

  // Add a new folder
  const addFolder = async (label) => {
    if (!label) return; // Prevent empty folder names
    try {
      const response = await apiClient.post("/folder", {
        label,
        userId: user.id,
      });
      setFolders([...folders, response.data]);
    } catch (error) {
      console.error("Error adding folder:", error.message);
    }
  };

  // Delete a folder
  const deleteFolder = async (folderId) => {
    try {
      await apiClient.delete("/folder", {
        params: { id: folderId },
      });
      setFolders(folders.filter((folder) => folder.id !== folderId));
      if (currentFolderId === folderId) {
        setCurrentFolderId(folders[0]?.id || null);
      }
    } catch (error) {
      console.error("Error deleting folder:", error.message);
    }
  };

  // Rename a folder
  const renameFolder = async (folderId, newLabel) => {
    if (!newLabel) return; // Prevent empty folder names
    try {
      const response = await apiClient.put("/folder", null, {
        params: { id: folderId, label: newLabel },
      });
      setFolders(
        folders.map((folder) =>
          folder.id === folderId ? { ...folder, label: response.data.label } : folder
        )
      );
    } catch (error) {
      console.error("Error renaming folder:", error.message);
    }
  };

  const deleteEmail = async (emailId) => {
    try {
      if (!emailId || !user.id) {
        throw new Error("Missing required parameters: userId or emailId.");
      }
  
      setLoading(true);
      const response = await apiClient.delete("/email", {
        params: {
          userId: user.id, // Ensure userId is correctly passed
          emailId,         // Ensure emailId is correctly passed
        },
      });
  
      if (response.status === 200) {
        setEmails((prevEmails) => prevEmails.filter((email) => email.id !== emailId));
        alert("Email deleted successfully.");
      } else {
        throw new Error(`Unexpected response: ${response.status}`);
      }
    } catch (error) {
      console.error("Error deleting email:", error.message);
      alert("Failed to delete email. Please try again.");
    } finally {
      setLoading(false);
    }
  };
  
  const uploadAttachments = async (emailId) => {
    const formData = new FormData();
    Array.from(attachmentFiles).forEach((file) => {
      formData.append("files", file);
    });
    formData.append("emailId", emailId);
  
    try {
      const response = await apiClient.post("/attachment", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("Attachments uploaded successfully: " + response.data.join(", "));
      fetchAttachments(emailId); // Refresh attachments after upload
    } catch (error) {
      console.error("Error uploading attachments:", error.message);
      alert("Failed to upload attachments. Please try again.");
    }
  };

  const fetchAttachments = async (emailId) => {
    try {
      const response = await apiClient.get("/attachment", {
        params: { emailId },
      });
      setAttachments(response.data);
    } catch (error) {
      console.error("Error fetching attachments:", error.message);
      alert("Failed to fetch attachments. Please check if the email has attachments or try again later.");
    }
  };

  const deleteAttachment = async (emailId, fileName) => {
    try {
      await apiClient.delete("/attachment", {
        params: { emailId, attachmentFileName: fileName },
      });
      alert("Attachment deleted successfully.");
      fetchAttachments(emailId); // Refresh attachments after deletion
    } catch (error) {
      console.error("Error deleting attachment:", error.message);
      alert("Failed to delete attachment. Please try again.");
    }
  };
  const getDownloadLink = async (emailId, fileName) => {
    try {
      const response = await apiClient.get("/attachment/download", {
        params: { emailId, attachmentFileName: fileName },
        responseType: "blob", // Ensure the response type is 'blob'
      });

      if (response.status === 200) {
        const blob = new Blob([response.data]);
        const downloadUrl = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = downloadUrl;
        link.download = fileName; // Set the file name for download
        document.body.appendChild(link);
        link.click(); // Trigger the download
        document.body.removeChild(link);
        URL.revokeObjectURL(downloadUrl); // Clean up the URL
      } else {
        alert("Failed to download attachment. Please try again.");
      }
    } catch (error) {
      console.error("Error downloading attachment:", error.message);
      alert("Failed to download attachment. Please try again.");
    }
  };

  useEffect(() => {
    fetchFolders();
  }, [user]);

  return (
      <div>
        <button onClick={() => fetchEmails(currentFolderId, sortOption)}>Refresh</button>
        <div className="folder-list">
          {folders.map((folder) => (
              <button
                  key={folder.id}
                  onClick={() => setCurrentFolderId(folder.id)}
                  className={currentFolderId === folder.id ? "active" : ""}
              >
                {folder.label}
              </button>

          ))}
          <button onClick={() => addFolder(prompt("Enter folder name:"))}>+ Add Folder</button>
        </div>

        {/* Sorting Options */}
        <div className="sorting-section">
          <label>
            <select value={sortOption} onChange={(e) => setSortOption(e.target.value)}>
              <option value="default">Sort by Date</option>
              <option value="priority">Sort by Priority</option>
            </select>
          </label>
        </div>

        <div className="filter-section">
          <input
              type="text"
              placeholder="Filter by Sender"
              value={filterSender}
              onChange={(e) => setFilterSender(e.target.value)}
          />
          <input
              type="text"
              placeholder="Filter by Topic"
              value={filterTopic}
              onChange={(e) => setFilterTopic(e.target.value)}
          />
          <input
              type="text"
              placeholder="Filter by Body"
              value={filterBody}
              onChange={(e) => setFilterBody(e.target.value)}
          />
          <input
              type="text"
              placeholder="Filter by Receivers (comma separated)"
              value={filterReceivers}
              onChange={(e) => setFilterReceivers(e.target.value)}
          />
          <input
              type="text"
              placeholder="Filter by Attachments (comma separated)"
              value={filterAttachments}
              onChange={(e) => setFilterAttachments(e.target.value)}
          />
          <div>
            <label>
              <input
                  type="radio"
                  value="after"
                  checked={filterDateOption === 'after'}
                  onChange={() => setFilterDateOption('after')}
              />
              After
            </label>
            <label>
              <input
                  type="radio"
                  value="before"
                  checked={filterDateOption === 'before'}
                  onChange={() => setFilterDateOption('before')}
              />
              Before
            </label>
            <label>
              <input
                  type="radio"
                  value="between"
                  checked={filterDateOption === 'between'}
                  onChange={() => setFilterDateOption('between')}
              />
              Between
            </label>
          </div>

          {(filterDateOption === 'after' || filterDateOption === 'before') && (
              <input
                  type="datetime-local"
                  value={dateTime}
                  onChange={(e) => setDateTime(e.target.value)}
              />
          )}

          {filterDateOption === 'between' && (
              <>
                <input
                    type="datetime-local"
                    value={startDateTime}
                    onChange={(e) => setStartDateTime(e.target.value)}
                />
                <input
                    type="datetime-local"
                    value={endDateTime}
                    onChange={(e) => setEndDateTime(e.target.value)}
                />
              </>

          )}


          <button onClick={applyFilters}>Apply Filters</button>
        </div>

        <div className="email-list">
          <h2>
            Emails
          </h2>
          {emails.length === 0 ? (
              <p>No emails in this folder.</p>
          ) : (
              <ul>
                {emails.map((email) => (
                    <li key={email.id}>
                      <strong>Subject:</strong> {email.topic || "No Subject"} <br/>
                      <strong>Body:</strong> {email.body || "No Body"} <br/>
                      <strong>Priority:</strong> {email.priority} <br/>
                      <strong>Sender:</strong> {email.senderId || "Unknown Sender"} <br/>
                      <strong>Receivers:</strong>{" "}
                      {email.receiversEmailAddresses.join(", ")} <br/>
                      <strong>Sent At:</strong> {email.creationDateTime} <br/>
                      <button onClick={() => deleteEmail(email.id)} disabled={loading}>
                        Delete
                      </button>


                      <button onClick={() => {
                        setSelectedEmailId(email.id);
                        fetchAttachments(email.id); // Fetch attachments when viewing
                      }}>View Attachments
                      </button>

                      {selectedEmailId === email.id && (
                          <div>
                            <strong>Attachments:</strong>
                            <ul>
                              {attachments.map((fileName) => (
                                  <li key={fileName}>
                                    {fileName}
                                    <button onClick={() => getDownloadLink(email.id, fileName)}>Download</button>
                                    <button onClick={() => deleteAttachment(email.id, fileName)}>Delete</button>
                                  </li>
                              ))}
                            </ul>
                            <input type="file" multiple onChange={(e) => setAttachmentFiles(e.target.files)}/>
                            <button onClick={() => uploadAttachments(email.id)}>Upload Attachments</button>
                          </div>
                      )}
                    </li>
                ))}
              </ul>
          )}
        </div>


        <div className="folder-actions">
          <button
              onClick={() =>
                  renameFolder(
                      currentFolderId,
                      prompt(
                          "Enter new name for folder:",
                          folders.find((f) => f.id === currentFolderId)?.label || ""
                      )
                  )
              }
          >
            Rename Folder
          </button>
          <button onClick={() => currentFolderId && deleteFolder(currentFolderId)}>Delete Folder</button>
        </div>
      </div>
  );
};

Mailbox.propTypes = {
  user: PropTypes.object.isRequired,
};

export default Mailbox;