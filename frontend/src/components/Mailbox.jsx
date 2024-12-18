import PropTypes from "prop-types";
import EmailItem from "./EmailItem";

const Mailbox = ({ emails, onDelete }) => (
  <div>
    <h2>Your Mailbox</h2>
    <ul id="emails-list">
      {emails.map((email) => (
        <EmailItem key={email.id} email={email} onDelete={onDelete} />
      ))}
    </ul>
  </div>
);

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
  onDelete: PropTypes.func.isRequired,
};

export default Mailbox;
