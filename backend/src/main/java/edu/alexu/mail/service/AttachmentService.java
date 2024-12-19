package edu.alexu.mail.service;

import org.springframework.stereotype.Service;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentService {

    private static final String FTP_SERVER = "156.194.233.110";
    private static final int FTP_PORT = 21;
    private static final String FTP_USER = "ftp-root";
    private static final  String FTP_PASS = "root";

    public List<String> uploadAttachments(MultipartFile[] multipartFiles, int emailId) throws IOException {
        List<String> attachmentsFileNames = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();
        File tempFile;

        // Connect to the FTP server
        ftpClient.connect(FTP_SERVER, FTP_PORT);
        boolean isSuccessfulLogin = ftpClient.login(FTP_USER, FTP_PASS);

        // Check if login was successful
        if (!isSuccessfulLogin) {
            throw new RuntimeException("FTP login failed.");
        }

        // Set FTP settings
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        for (MultipartFile multipartFile : multipartFiles) {

            // Save the MultipartFile to a temporary file
            tempFile = File.createTempFile("upload-", null);
            multipartFile.transferTo(tempFile);

            // Upload file
            FileInputStream inputStream = new FileInputStream(tempFile);
            if (!tempFile.delete()) System.out.println("Failed to delete temp file.");

            String directory = "/" + emailId + "/";
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

    public List<String> getAttachmentsFileNames(int emailId) throws IOException {
        List<String> attachmentsFileNames = new ArrayList<>();

        FTPClient ftpClient = new FTPClient();

        // Connect to the FTP server
        ftpClient.connect(FTP_SERVER, FTP_PORT);
        boolean isSuccessfulLogin = ftpClient.login(FTP_USER, FTP_PASS);

        // Check if login was successful
        if (!isSuccessfulLogin) {
            throw new RuntimeException("FTP login failed.");
        }

        // Set FTP settings
        ftpClient.enterLocalPassiveMode();

        String directory = "/" + emailId;
        if (!ftpClient.changeWorkingDirectory(directory)) {
            throw new IOException("Can not change working directory.");
        }

        FTPFile[] ftpFiles = ftpClient.listFiles();

        for (FTPFile ftpFile : ftpFiles) {
            attachmentsFileNames.add(ftpFile.getName());
        }
        return attachmentsFileNames;
    }

    public String getDownloadLink(int emailId, String attachmentFileName) {
        return "ftp://" + FTP_USER + ":" + FTP_PASS + "@" + FTP_SERVER
                + "/"
                + emailId + "/" + attachmentFileName;
    }

    public void deleteAttachment(int emailId, String attachmentFileName) throws IOException {
        FTPClient ftpClient = new FTPClient();

        // Connect to the FTP server
        ftpClient.connect(FTP_SERVER, FTP_PORT);
        boolean isSuccessfulLogin = ftpClient.login(FTP_USER, FTP_PASS);

        // Check if login was successful
        if (!isSuccessfulLogin) {
            throw new RuntimeException("FTP login failed.");
        }

        // Set FTP settings
        ftpClient.enterLocalPassiveMode();

        String path = "/" + emailId + "/" + attachmentFileName;

        if (!ftpClient.deleteFile(path)) {
            throw new RuntimeException("Can not delete attachment file.");
        }
    }
}
