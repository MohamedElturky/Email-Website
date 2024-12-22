package edu.alexu.mail.filter;

import edu.alexu.mail.filter.email.*;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.service.AttachmentService;
import edu.alexu.mail.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class EmailFilterFactory {
    private final UserService userService;
    private final AttachmentService attachmentService;

    public EmailFilterFactory(UserService userService, AttachmentService attachmentService) {
        this.userService = userService;
        this.attachmentService = attachmentService;
    }

    public final  Filter<Email> createFilter(EmailFilterType filterType,
                                             Object... arg) {
        switch (filterType) {
            case ON_OR_BEFORE -> {
                Filter<Email> onDateTimeFilter = new OnDateTimeFilter((LocalDateTime) arg[0]);
                Filter<Email> beforeDateTimeFilter = new BeforeDateTimeFilter((LocalDateTime) arg[0]);
                return new OrFilter<>(onDateTimeFilter, beforeDateTimeFilter);
            }
            case ON_OR_AFTER -> {
                Filter<Email> onDateTimeFilter = new OnDateTimeFilter((LocalDateTime) arg[0]);
                Filter<Email> afterDateTimeFilter = new AfterDateTimeFilter((LocalDateTime) arg[0]);
                return new OrFilter<>(onDateTimeFilter, afterDateTimeFilter);
            }
            case ON_OR_BETWEEN -> {
                LocalDateTime startDateTime = (LocalDateTime) arg[0];
                LocalDateTime endDateTime = (LocalDateTime) arg[1];
                if (startDateTime.isAfter(endDateTime)) {
                    throw new RuntimeException("Start date cannot be after end date.");
                }
                Filter<Email> notAfterDatTimeFilter = new NotFilter<>(new AfterDateTimeFilter(endDateTime));
                Filter<Email> notBeforeDateTimeFilter = new NotFilter<>(new BeforeDateTimeFilter(startDateTime));
                return new AndFilter<>(notBeforeDateTimeFilter, notAfterDatTimeFilter);
            }
            case RECEIVERS -> {
                return new ReceiversFilter((List<String>) arg[0]);
            }
            case SENDER -> {
                return new SenderFilter((String) arg[0], userService);
            }
            case TOPIC -> {
                return new TopicFilter((String) arg[0]);
            }
            case BODY -> {
                return new BodyFilter((String) arg[0]);
            }
            case ATTACHMENTS -> {
                return new AttachmentFileNameFilter((List<String>) arg[0], attachmentService);
            }
            default -> throw new RuntimeException("Invalid email attribute filter type.");
        }
    }
}
