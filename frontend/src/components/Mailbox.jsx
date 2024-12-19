import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import EmailItem from "./EmailItem";

const Mailbox = ({ emails, onDelete, onFolderChange }) => {
  const [currentFolder, setCurrentFolder] = useState("Inbox");
  const [sortOrder, setSortOrder] = useState("default"); // Added state for sorting

  useEffect(() => {
    // Automatically load emails for the default folder (Inbox) on mount
    onFolderChange(currentFolder, sortOrder);
  }, [currentFolder, onFolderChange, sortOrder]);

  const handleFolderClick = (folderName) => {
    setCurrentFolder(folderName); // Update the current folder state
  };

  const handleSortChange = (event) => {
    const newSortOrder = event.target.value;
    setSortOrder(newSortOrder); // Update the sort order based on dropdown
  };

  return (
    <div>
      <div className="folder-list">
        {/* Display the default folders */}
        {["Inbox", "Trash", "Draft", "Sent"].map((folderName) => (
          <button
            key={folderName}
            onClick={() => handleFolderClick(folderName)}
            className={currentFolder === folderName ? "active" : ""}
          >
            {folderName}
          </button>
        ))}

        {/* Buttons for adding, renaming, and deleting folders */}
        <div className="user-folder-actions">
          <button
            onClick={() => {
              /* handle adding folder later */
            }}
          >
            Add Folder
          </button>
          <button
            onClick={() => {
              /* handle renaming folder later */
            }}
          >
            Rename Folder
          </button>
          <button
            onClick={() => {
              /* handle deleting folder later */
            }}
          >
            Delete Folder
          </button>
        </div>
      </div>

      {/* Dropdown to select sorting method */}
      <div className="sort-dropdown">
        <label htmlFor="sort">Sort By: </label>
        <select id="sort" value={sortOrder} onChange={handleSortChange}>
          <option value="default">Default</option>
          <option value="priority">Priority</option>
        </select>
      </div>

      <ul id="emails-list">
        {emails.map((email) => (
          <EmailItem key={email.id} email={email} onDelete={onDelete} />
        ))}
      </ul>
    </div>
  );
};

// Prop validation
Mailbox.propTypes = {
  emails: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      subject: PropTypes.string.isRequired,
      to: PropTypes.string.isRequired,
      senderEmail: PropTypes.string.isRequired,
      body: PropTypes.string.isRequired,
      priority: PropTypes.number.isRequired,
    })
  ).isRequired,
  user: PropTypes.object.isRequired,
  onDelete: PropTypes.func.isRequired,
  onFolderChange: PropTypes.func.isRequired,
};

export default Mailbox;
