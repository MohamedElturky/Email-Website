package edu.alexu.mail.filter.email.attribute;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.service.AttachmentService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AttachmentFileNameFilter implements Filter<Email> {

    private final List<String> attachmentFileNames;
    private final AttachmentService attachmentService;

    public AttachmentFileNameFilter(List<String> attachmentFileNames,
                                    AttachmentService attachmentService) {
        this.attachmentFileNames = attachmentFileNames;
        this.attachmentService = attachmentService;
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> {
                    try {
                        return !Collections.disjoint(attachmentService.
                                getAttachmentsFileNames(email.getId()), attachmentFileNames);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                })
                .toList();
    }
}
