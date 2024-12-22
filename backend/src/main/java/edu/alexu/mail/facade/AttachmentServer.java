package edu.alexu.mail.facade;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AttachmentServer {
    void connect() throws IOException;

    void disconnect() throws IOException;

    List<String> uploadAttachments(MultipartFile[] multipartFiles,
                                   int emailId) throws IOException;

    List<String> getAttachmentsFileNames(int emailId) throws IOException;

    ResponseEntity<Resource> downloadAttachment(int emailId, String attachmentFileName)
            throws IOException;

    void deleteAttachment(int emailId, String attachmentFileName) throws IOException;
}
