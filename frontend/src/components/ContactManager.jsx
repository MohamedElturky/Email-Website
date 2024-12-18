import { useState } from 'react';

const ContactManager = () => {
  const [contacts, setContacts] = useState([]);
  const [newContact, setNewContact] = useState({ name: '', emails: [''] });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === 'emails') {
      setNewContact({ ...newContact, emails: [value] });
    } else {
      setNewContact({ ...newContact, [name]: value });
    }
  };

  const addContact = () => {
    if (newContact.name && newContact.emails[0]) {
      setContacts([...contacts, newContact]);
      setNewContact({ name: '', emails: [''] });
    }
  };

  const deleteContact = (index) => {
    const updatedContacts = contacts.filter((_, i) => i !== index);
    setContacts(updatedContacts);
  };

  return (
    <div>
      <h2>Contact Manager</h2>
      <input
        name="name"
        value={newContact.name}
        onChange={handleInputChange}
        placeholder="Contact Name"
      />
      <input
        name="emails"
        value={newContact.emails[0]}
        onChange={handleInputChange}
        placeholder="Email"
      />
      <button onClick={addContact}>Add Contact</button>
      <ul>
        {contacts.map((contact, index) => (
          <li key={index}>
            {contact.name} - {contact.emails.join(', ')}
            <button onClick={() => deleteContact(index)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ContactManager;