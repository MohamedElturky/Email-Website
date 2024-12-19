package edu.alexu.mail.controller;

import edu.alexu.mail.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public List<String> uploadAttachments(@RequestPart MultipartFile[] files,
                                          @RequestParam int emailId) throws IOException {
        return attachmentService.uploadAttachments(files, emailId);
    }

    @GetMapping
    public List<String> getAttachmentsFileNames(@RequestParam int emailId) throws IOException {
        return attachmentService.getAttachmentsFileNames(emailId);
    }

    @DeleteMapping
    public void deleteAttachment(@RequestParam int emailId,
                                 @RequestParam String attachmentFileName) throws IOException {
        attachmentService.deleteAttachment(emailId, attachmentFileName);
    }

    @GetMapping("/download")
    public String getDownloadLink(@RequestParam int emailId,
                                  @RequestParam String attachmentFileName) {
        return attachmentService.getDownloadLink(emailId, attachmentFileName);
    }
}
