package edu.alexu.mail.service;

import edu.alexu.mail.facade.AttachmentServer;
import edu.alexu.mail.facade.proxy.AttachmentServerProxy;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentServer attachmentServer;

    public AttachmentService(AttachmentServerProxy attachmentServerProxy) {
        this.attachmentServer = attachmentServerProxy.getProxyInstance();
    }

    public List<String> uploadAttachments(MultipartFile[] multipartFiles, int emailId) throws IOException {
        return attachmentServer.uploadAttachments(multipartFiles, emailId);
    }

    public List<String> getAttachmentsFileNames(int emailId) throws IOException {
        return attachmentServer.getAttachmentsFileNames(emailId);
    }

    public ResponseEntity<Resource> downloadAttachment(int emailId, String attachmentFileName) throws IOException {
        return attachmentServer.downloadAttachment(emailId, attachmentFileName);
    }

    public void deleteAttachment(int emailId, String attachmentFileName) throws IOException {
        attachmentServer.deleteAttachment(emailId, attachmentFileName);
    }
}
