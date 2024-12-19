import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import axios from "axios";
import EmailItem from "./EmailItem";

const Mailbox = ({ user }) => {
  const [emails, setEmails] = useState([]);
  const [folders, setFolders] = useState([]);
  const [currentFolder, setCurrentFolder] = useState("Inbox");
  const [sortOrder, setSortOrder] = useState("default");

  // Fetch folders on component mount or when user changes
  useEffect(() => {
    const fetchFolders = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8081/api/folder/all",
          {
            params: { userId: user.id },
          }
        );
        setFolders(response.data);
      } catch (error) {
        console.error("Error fetching folders:", error.message);
        alert("Failed to load folders. Please try again.");
      }
    };

    if (user) fetchFolders();
  }, [user]);

  // Fetch emails for the selected folder
  const fetchEmails = async (folderName, sortOrder) => {
    try {
      const selectedFolder = folders.find(
        (folder) => folder.label.toLowerCase() === folderName.toLowerCase()
      );
      if (!selectedFolder) throw new Error(`Folder '${folderName}' not found.`);

      const response = await axios.get(
        `http://localhost:8081/api/email/folder/${sortOrder}`,
        { params: { folderId: selectedFolder.id } }
      );
      setEmails(response.data);
    } catch (error) {
      console.error(
        `Error fetching emails for folder '${folderName}':`,
        error.message
      );
      alert(`Failed to load emails for '${folderName}'.`);
    }
  };

  // Handle folder click
  const handleFolderClick = (folderName) => {
    if (folderName !== currentFolder) {
      setCurrentFolder(folderName);
      fetchEmails(folderName, sortOrder);
    }
  };

  // Handle sorting change
  const handleSortChange = (event) => {
    const newSortOrder = event.target.value;
    setSortOrder(newSortOrder);
    fetchEmails(currentFolder, newSortOrder);
  };

  // Add a new folder
  const addFolder = async (folderName) => {
    try {
      const response = await axios.post(
        "http://localhost:8081/api/folder", // Updated endpoint
        {
          userId: user.id,
          label: folderName,
        }
      );
      setFolders((prevFolders) => [...prevFolders, response.data]);
      alert(`Folder "${folderName}" added successfully!`);
    } catch (error) {
      console.error("Error adding folder:", error.message);
      alert("Failed to add folder. Please try again.");
    }
  };

  const renameFolder = async (folderId, newLabel) => {
    try {
      // Pass folderId and newLabel as query parameters in the URL
      await axios.put(
        `http://localhost:8081/api/folder?id=${folderId}&label=${newLabel}`
      );
      setFolders(
        folders.map((folder) =>
          folder.id === folderId ? { ...folder, label: newLabel } : folder
        )
      );
      alert(`Folder renamed to "${newLabel}" successfully!`);
    } catch (error) {
      console.error("Error renaming folder:", error.message);
      alert("Failed to rename folder. Please try again.");
    }
  };

  // Delete a folder
  const deleteFolder = async (folderId) => {
    try {
      await axios.delete(`http://localhost:8081/api/folder`, {
        params: { id: folderId }, // Use the correct parameter name: 'id'
      });
      setFolders(folders.filter((folder) => folder.id !== folderId));
      alert("Folder deleted successfully!");
    } catch (error) {
      console.error("Error deleting folder:", error.message);
      alert("Failed to delete folder. Please try again.");
    }
  };

  return (
    <div>
      <div className="folder-list">
        {/* Render all folders */}
        {folders.map((folder) => (
          <button
            key={folder.id}
            onClick={() => handleFolderClick(folder.label)}
            className={currentFolder === folder.label ? "active" : ""}
          >
            {/* Check if the folder is one of the default folders */}
            {["Inbox", "Trash", "Draft", "Sent"].includes(folder.label) ? (
              folder.label
            ) : (
              <>
                {folder.label} (ID: {folder.id})
              </>
            )}
          </button>
        ))}

        {/* Folder management buttons */}
        <div className="user-folder-actions">
          <button
            onClick={() => {
              const folderName = prompt("Enter the name of the new folder:");
              if (folderName) addFolder(folderName);
            }}
          >
            Add Folder
          </button>
          <button
            onClick={() => {
              const folderId = prompt("Enter the ID of the folder to rename:");
              const newLabel = prompt("Enter the new name for the folder:");
              if (folderId && newLabel)
                renameFolder(Number(folderId), newLabel);
            }}
          >
            Rename Folder
          </button>
          <button
            onClick={() => {
              const folderId = prompt("Enter the ID of the folder to delete:");
              if (folderId) deleteFolder(Number(folderId));
            }}
          >
            Delete Folder
          </button>
        </div>
      </div>

      {/* Sorting dropdown */}
      <div className="sort-dropdown">
        <label htmlFor="sort">Sort By: </label>
        <select id="sort" value={sortOrder} onChange={handleSortChange}>
          <option value="default">Default</option>
          <option value="priority">Priority</option>
        </select>
      </div>

      {/* Email list */}
      <ul id="emails-list">
        {emails.map((email) => (
          <EmailItem
            key={email.id}
            email={email}
            onDelete={(emailId) => {
              setEmails(emails.filter((email) => email.id !== emailId));
            }}
          />
        ))}
      </ul>
    </div>
  );
};

Mailbox.propTypes = {
  user: PropTypes.object.isRequired,
};

export default Mailbox;
