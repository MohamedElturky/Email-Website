import PropTypes from 'prop-types';
import EmailItem from './EmailItem';

const Mailbox = ({ emails, onDelete }) => (
  <div>
    <h2>Mailbox</h2>
    {emails.map((email) => (
      <EmailItem key={email.id} email={email} onDelete={onDelete} />
    ))}
  </div>
);

// Add PropTypes for validation
Mailbox.propTypes = {
  emails: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      subject: PropTypes.string.isRequired,
      sender: PropTypes.string.isRequired,
      to: PropTypes.string.isRequired,
    })
  ).isRequired,
  onDelete: PropTypes.func.isRequired,
};

export default Mailbox;
