package edu.alexu.mail.facade;

import edu.alexu.mail.server.AttachmentServer;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentServerFacade implements AttachmentServer {

    private static final String FTP_SERVER = "156.194.233.110";
    private static final int FTP_PORT = 21;
    private static final String FTP_USER = "ftp-root";
    private static final  String FTP_PASS = "root";

    private final FTPClient ftpClient;

    public AttachmentServerFacade() {
        this.ftpClient = new FTPClient();
    }

    @Override
    public void connect() throws IOException {
        ftpClient.connect(FTP_SERVER, FTP_PORT);
        boolean isSuccessfulLogin = ftpClient.login(FTP_USER, FTP_PASS);
        if (!isSuccessfulLogin) {
            throw new RuntimeException("FTP login failed.");
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public void disconnect() throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }

    @Override
    public List<String> uploadAttachments(MultipartFile[] multipartFiles,
                                          int emailId) throws IOException {
        List<String> attachmentsFileNames = new ArrayList<>();
        File tempFile;

        for (MultipartFile multipartFile : multipartFiles) {

            tempFile = File.createTempFile("upload-", null);
            multipartFile.transferTo(tempFile);

            FileInputStream inputStream = new FileInputStream(tempFile);
            if (!tempFile.delete()) System.out.println("Failed to delete temp file.");

            String directory = File.separator + emailId + File.separator;
            if (!ftpClient.makeDirectory(directory)) {
                System.out.println("Directory already exits.");
            }

            boolean isStoreSuccessful = ftpClient.storeFile(
                    directory
                            + multipartFile.getOriginalFilename(),
                    inputStream);
            if (!isStoreSuccessful) {
                throw new RuntimeException("FTP store failed.");
            }

            attachmentsFileNames.add(multipartFile.getOriginalFilename());
        }
        return attachmentsFileNames;
    }

    @Override
    public List<String> getAttachmentsFileNames(int emailId) throws IOException {
        List<String> attachmentsFileNames = new ArrayList<>();

        String directory = File.separator + emailId;

        if (!ftpClient.changeWorkingDirectory(directory)) {
            throw new IOException("Can not change working directory.");
        }

        FTPFile[] ftpFiles = ftpClient.listFiles();

        for (FTPFile ftpFile : ftpFiles) {
            attachmentsFileNames.add(ftpFile.getName());
        }

        return attachmentsFileNames;
    }

    @Override
    public ResponseEntity<Resource> downloadAttachment(int emailId, String attachmentFileName)
            throws IOException {
        String directory = File.separator + emailId + File.separator + attachmentFileName;

        InputStream inputStream = ftpClient.retrieveFileStream(directory);

        if (inputStream == null) {
            throw new IOException("Can not retrieve attachment.");
        }

        Resource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + attachmentFileName);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public void deleteAttachment(int emailId, String attachmentFileName) throws IOException {
        String path = File.separator + emailId + File.separator + attachmentFileName;

        if (!ftpClient.deleteFile(path)) {
            throw new RuntimeException("Can not delete attachment file.");
        }
    }

}
