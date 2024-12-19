import PropTypes from "prop-types";

const EmailItem = ({ email, onDelete }) => {
  // If sender's email is not provided directly, you can fetch it or display a placeholder
  const senderEmail = email.senderId ? `` : "";

  return (
    <li>
      <input type="checkbox" className="email-checkbox" data-id={email.id} />
      <strong>Subject:</strong> {email.topic || "No Subject"} <br />{" "}
      {/* Use topic as subject */}
      <strong>From:</strong> {senderEmail} <br />{" "}
      {/* Use senderId to fetch or display sender's email */}
      <strong>To:</strong>{" "}
      {email.receiversEmailAddresses.join(", ") || "Unknown"} <br />{" "}
      {/* Use receiversEmailAddresses */}
      <strong>Priority:</strong> {email.priority} <br />{" "}
      {/* Display the priority */}
      <p>
        <strong>Body:</strong> {email.body}
      </p>{" "}
      {/* Display the body */}
      <button onClick={() => onDelete(email.id)}>Delete</button>
    </li>
  );
};

EmailItem.propTypes = {
  email: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    topic: PropTypes.string.isRequired, // topic for subject
    receiversEmailAddresses: PropTypes.arrayOf(PropTypes.string).isRequired, // to
    senderId: PropTypes.number.isRequired, // sender's ID
    body: PropTypes.string.isRequired,
    priority: PropTypes.number.isRequired,
  }).isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default EmailItem;
