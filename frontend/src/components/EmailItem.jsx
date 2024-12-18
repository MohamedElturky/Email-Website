import PropTypes from "prop-types";

const EmailItem = ({ email, onDelete }) => (
  <li>
    <input type="checkbox" className="email-checkbox" data-id={email.id} />
    <strong>Subject:</strong> {email.subject} <br />
    <strong>From:</strong> {email.senderEmail} <br /> {/* Display the sender */}
    <strong>To:</strong> {email.to} <br />
    <strong>Priority:</strong> {email.priority} <br />{" "}
    {/* Display the priority */}
    <p>
      <strong>Body:</strong> {email.body}
    </p>{" "}
    {/* Display the body */}
    <button onClick={() => onDelete(email.id)}>Delete</button>
  </li>
);

EmailItem.propTypes = {
  email: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    subject: PropTypes.string.isRequired,
    to: PropTypes.string.isRequired,
    senderEmail: PropTypes.string.isRequired,
    body: PropTypes.string.isRequired,
    priority: PropTypes.number.isRequired,
  }).isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default EmailItem;
