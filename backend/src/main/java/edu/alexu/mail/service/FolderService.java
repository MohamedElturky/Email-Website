package edu.alexu.mail.service;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.model.FolderType;
import edu.alexu.mail.repository.FolderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class FolderService {

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public Folder addFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public void deleteFolder(int id) {
        Folder folder = getFolder(id);
        if (isDefaultFolder(folder)) {
            throw new RuntimeException("Can not delete default folder.");
        }
        folderRepository.deleteById(id);
    }

    public Folder renameFolder(int id, String newLabel) {
        Folder folder = getFolder(id);
        if (isDefaultFolder(folder)) {
            throw new RuntimeException("Can not rename default folder.");
        }
        folder.setLabel(newLabel);
        return folderRepository.save(folder);
    }

    public Folder getFolder(int id) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder != null) {
            return folder;
        }
        else throw new RuntimeException("Folder not found.");
    }

    public List<Folder> getAllFolders(int userId) {
        return folderRepository.findAllByUserId(userId);
    }

    public void moveEmail(Integer emailId, int fromId, int toId) {
        Folder fromFolder = getFolder(fromId);
        Folder toFolder = getFolder(toId);
        fromFolder.getEmailsIds().remove(emailId);
        toFolder.getEmailsIds().add(emailId);
        folderRepository.save(fromFolder);
        folderRepository.save(toFolder);
    }

    public void moveEmail(int emailId, int toId) {
        Folder toFolder = getFolder(toId);
        toFolder.getEmailsIds().add(emailId);
        folderRepository.save(toFolder);
    }

    public void createDefaultUserFolders(int userId) {
        for (FolderType defaultFolder : FolderType.values()) {
            Folder folder = new Folder(defaultFolder.toString(),
                                        userId);
            folderRepository.save(folder);
        }
    }

    public Folder getFolder(int userId, FolderType folderType) {
        Folder folder = folderRepository.findByUserIdAndLabel(userId,
                folderType.toString()).orElse(null);
        if (folder != null) {
            return folder;
        }
        else {
            throw new RuntimeException("Folder not found.");
        }
    }

    public void deleteAllFoldersByUserId(int userId) {
        getAllFolders(userId)
                .forEach(folder -> folderRepository.deleteById(folder.getId()));
    }

    public void deleteEmailFromFolder(Integer emailId, int folderId) {
        Folder folder = getFolder(folderId);
        folder.getEmailsIds().remove(emailId);
        folderRepository.save(folder);
    }

    public void addEmailToFolder(int emailId, int folderId) {
        Folder folder = getFolder(folderId);
        folder.getEmailsIds().add(emailId);
        folderRepository.save(folder);
    }

    public void emptyFolder(int folderId) {
        Folder folder = getFolder(folderId);
        folder.getEmailsIds().clear();
        folderRepository.save(folder);
    }

    @Scheduled(cron = "@midnight")
    private void clearTrashFolder() {
        folderRepository
                .findAllByLabel(FolderType.TRASH.toString())
                .forEach(folder -> {
                    emptyFolder(folder.getId());
                    folderRepository.save(folder);
                });
    }

    private boolean isDefaultFolder(Folder folder) {
        List<String> defaultFolders = Arrays.stream(FolderType
                        .values())
                .map(FolderType::toString)
                .toList();
        return defaultFolders.contains(folder.getLabel());
    }
}
