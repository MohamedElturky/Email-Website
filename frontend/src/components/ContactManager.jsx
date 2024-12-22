import { useState, useEffect,useCallback } from "react";
import axios from "axios";
import PropTypes from "prop-types";

const ContactManager = ({ userId }) => {
  const [contacts, setContacts] = useState([]);
  const [newContact, setNewContact] = useState({
    name: "",
    emailAddresses: [""],
  });
  const [searchQuery, setSearchQuery] = useState("");
  const [editContactId, setEditContactId] = useState(null);
  const [sortOrder, setSortOrder] = useState("asc");

  ContactManager.propTypes = {
    userId: PropTypes.number.isRequired,
  };

  const API_BASE_URL = "http://localhost:8080/api/contact";
  console.log("Received userId:", userId);
  // Fetch all contacts
  const fetchContacts = useCallback(async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/all`, {
        params: { userId },
      });
      setContacts(response.data);
    } catch (error) {
      console.error("Error fetching contacts:", error);
    }
  }, [userId]);

  // Add or update a contact
  const saveContact = async () => {
    if (!newContact.name || !newContact.emailAddresses[0]) return;

    try {
      if (editContactId) {

        await axios.put(`${API_BASE_URL}/rename`,
            null,
            {
              params: { id: editContactId, name: newContact.name}
            });

        await axios.put(`${API_BASE_URL}/update-email-addresses`,
            newContact.emailAddresses,
            {
              params: { id: editContactId }
            });

      } else {
        // Add new contact with userId included
        const payload = {
          userId,
          name: newContact.name,
          emailAddresses: newContact.emailAddresses,
        };
        console.log("Payload being sent:", payload);
        await axios.post(API_BASE_URL, payload);
      }

      setNewContact({ name: "", emailAddresses: [""] });
      setEditContactId(null);
      await fetchContacts();
    } catch (error) {
      console.error("Error saving contact:", error);
    }
  };

  // Delete a contact
  const deleteContact = async (id) => {
    try {
      await axios.delete(API_BASE_URL, { params: { id } });
      fetchContacts();
    } catch (error) {
      console.error("Error deleting contact:", error);
    }
  };

  // Search contacts by name or email
  const searchContacts = async () => {
    try {
      if (searchQuery) {
        const response = await axios.get(`${API_BASE_URL}/all/name`, {
          params: { userId, name: searchQuery },
        });
        setContacts(response.data);
      } else {
        fetchContacts();
      }
    } catch (error) {
      console.error("Error searching contacts:", error);
    }
  };

  // Sort contacts by name
  const sortContacts = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/all/sorted`, {
        params: { userId },
      });
      setContacts(
        sortOrder === "asc" ? response.data : response.data.reverse()
      );
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } catch (error) {
      console.error("Error sorting contacts:", error);
    }
  };

  // Edit a contact
  const editContact = (contact) => {
    setEditContactId(contact.id);
    setNewContact({
      name: contact.name,
      emailAddresses: contact.emailAddresses,
    });
  };

  // Input handler
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === "emailAddresses") {
      setNewContact({ ...newContact, emailAddresses: value.split(",") });
    } else {
      setNewContact({ ...newContact, [name]: value });
    }
  };

  useEffect(() => {
    fetchContacts();
  }, [fetchContacts]);

  return (
    <div>
      <h2>Contact Manager</h2>
      <div>
        <input
          name="name"
          value={newContact.name}
          onChange={handleInputChange}
          placeholder="Contact Name"
        />
        <input
          name="emailAddresses"
          value={newContact.emailAddresses.join(",")}
          onChange={handleInputChange}
          placeholder="Email Addresses (comma-separated)"
        />
        <button onClick={saveContact}>
          {editContactId ? "Update Contact" : "Add Contact"}
        </button>
      </div>
      <div>
        <input
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search Contacts"
        />
        <button onClick={searchContacts}>Search</button>
        <button onClick={sortContacts}>
          Sort by Name ({sortOrder === "asc" ? "Ascending" : "Descending"})
        </button>
      </div>
      <ul>
        {contacts.map((contact) => (
          <li key={contact.id}>
            <span>
              {contact.name} - {contact.emailAddresses.join(", ")}
            </span>
            <button onClick={() => editContact(contact)}>Edit</button>
            <button onClick={() => deleteContact(contact.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ContactManager;
