import PropTypes from 'prop-types';

const EmailItem = ({ email, onDelete }) => (
  <div className="email-item">
    <h3>{email.subject}</h3>
    <p><strong>From:</strong> {email.sender}</p>
    <p>{email.body}</p>
    <button onClick={() => onDelete(email.id)}>Delete</button>
  </div>
);

// Add PropTypes for validation
EmailItem.propTypes = {
  email: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    subject: PropTypes.string.isRequired,
    sender: PropTypes.string.isRequired,
    body: PropTypes.string.isRequired,
  }).isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default EmailItem;
