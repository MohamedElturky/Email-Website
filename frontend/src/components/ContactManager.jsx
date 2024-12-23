import { useState, useEffect,useCallback } from "react";
import axios from "axios";
import PropTypes from "prop-types";

const ContactManager = ({ userId }) => {
  const [contacts, setContacts] = useState([]);
  const [newContact, setNewContact] = useState({
    name: '',
    emailAddresses: '',
  });
  const [searchQuery, setSearchQuery] = useState({ name: '', emailAddresses: '' });
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
    if (!newContact.name || !newContact.emailAddresses) return;

    try {
      if (editContactId) {

        await axios.put(`${API_BASE_URL}/rename`,
            null,
            {
              params: { id: editContactId, name: newContact.name}
            });

        const trimmedEmailAddresses = newContact.emailAddresses
            .split(',')
            .map(item => item.trim())
            .filter(item => item.length > 0)

        await axios.put(`${API_BASE_URL}/update-email-addresses`,
            trimmedEmailAddresses,
            {
              params: { id: editContactId }
            });

      } else {
        // Add new contact with userId included

        const trimmedEmailAddresses = newContact.emailAddresses
            .split(',')
            .map(item => item.trim())
            .filter(item => item.length > 0)

        console.log('New contact trimmed email addresses: ', trimmedEmailAddresses)

        const payload = {
          userId,
          name: newContact.name,
          emailAddresses: trimmedEmailAddresses,
        };

        console.log("Payload being sent:", payload);
        await axios.post(API_BASE_URL, payload);
      }

      setNewContact({ name: '', emailAddresses: '' });
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
      await fetchContacts();
    } catch (error) {
      console.error("Error deleting contact:", error);
    }
  };

  // Search contacts by name or email
  const searchContacts = async () => {
    try {

      if (searchQuery.name.length > 0 || searchQuery.emailAddresses.length > 0) {
        let nameFilter;
        let emailAddressFilter;
        if (searchQuery.name.length > 0) {
          let response = await axios.get(`${API_BASE_URL}/all/name`, {
            params: { userId, name: searchQuery.name },
          })
          nameFilter = response.data
        }
        if (searchQuery.emailAddresses.length > 0) {
          let response = await axios.get(`${API_BASE_URL}/all/email-addresses`, {
            params: { userId, emailAddress: searchQuery.emailAddresses
                  .split(',')
                  .map(item => item.trim())
                  .join(',')},
          });
          emailAddressFilter = response.data
        }
        if (nameFilter && emailAddressFilter) {
          console.log(emailAddressFilter)
          const finalFilter = emailAddressFilter.filter(item1 => nameFilter
                                                        .some(item2 => item1.id === item2.id))
          console.log(finalFilter)
          setContacts(finalFilter);
        }
        else {
          if (nameFilter) setContacts(nameFilter);
          else setContacts(emailAddressFilter);
        }
      }
      else {
        await fetchContacts();
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
    if (name === 'emailAddresses') {
      setNewContact({ ...newContact, emailAddresses: value });
    } else {
      setNewContact({ ...newContact, name: value });
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
          value={newContact.emailAddresses}
          onChange={handleInputChange}
          placeholder="Email Addresses (comma-separated)"
        />
        <button onClick={saveContact}>
          {editContactId ? "Update Contact" : "Add Contact"}
        </button>
      </div>
      <div>
        <input
            value={searchQuery.name}
            onChange={(e) => setSearchQuery({ ...searchQuery, name: e.target.value})}
            placeholder="Search by name"
        />
        <input
            value={searchQuery.emailAddresses}
            onChange={(e) => setSearchQuery({ ...searchQuery, emailAddresses: e.target.value})}
            placeholder="Search by email addresses (comma-seperated)"
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
