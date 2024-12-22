package edu.alexu.mail.filter.email;

import edu.alexu.mail.filter.*;
import edu.alexu.mail.filter.connective.ConnectiveType;
import edu.alexu.mail.filter.email.attribute.*;
import edu.alexu.mail.filter.email.datetime.AfterDateTimeFilter;
import edu.alexu.mail.filter.email.datetime.BeforeDateTimeFilter;
import edu.alexu.mail.filter.email.datetime.OnDateTimeFilter;
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
    private final FilterBuilder<Email> filterBuilder;

    public EmailFilterFactory(UserService userService, AttachmentService attachmentService, FilterBuilder<Email> filterBuilder) {
        this.userService = userService;
        this.attachmentService = attachmentService;
        this.filterBuilder = filterBuilder;
    }

    public final Filter<Email> createFilter(EmailFilterType filterType,
                                            Object... arg) {
        switch (filterType) {
            case ON_OR_BEFORE -> {
                return filterBuilder
                        .addFilter(new OnDateTimeFilter((LocalDateTime) arg[0]))
                        .or(new BeforeDateTimeFilter((LocalDateTime) arg[0]))
                        .build();
            }
            case ON_OR_AFTER -> {
                return filterBuilder
                        .addFilter(new OnDateTimeFilter((LocalDateTime) arg[0]))
                        .or(new AfterDateTimeFilter((LocalDateTime) arg[0]))
                        .build();
            }
            case ON_OR_BETWEEN -> {
                LocalDateTime startDateTime = (LocalDateTime) arg[0];
                LocalDateTime endDateTime = (LocalDateTime) arg[1];

                if (startDateTime.isAfter(endDateTime)) {
                    throw new RuntimeException("Start date cannot be after end date.");
                }

                return filterBuilder
                        .not(new AfterDateTimeFilter(endDateTime))
                        .not(new BeforeDateTimeFilter(startDateTime))
                        .and()
                        .build();
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
