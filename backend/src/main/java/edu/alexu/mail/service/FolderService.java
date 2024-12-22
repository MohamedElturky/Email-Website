package edu.alexu.mail.service;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.model.Folders;
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
        Folder folder = folderRepository.findById(id).orElse(null);
        List<String> defaultFolders = Arrays.stream(Folders
                .values())
                .map(Folders::getStringRepresentation)
                .toList();

        if (folder != null) {
            if (defaultFolders.contains(folder.getLabel())) {
                throw new RuntimeException("Can not delete default folder.");
            }
            else {
                folderRepository.deleteById(id);
            }
        }
        else {
            throw new RuntimeException("Folder not found.");
        }
    }

    public Folder renameFolder(int id, String newLabel) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder != null) {
            folder.setLabel(newLabel);
            return folderRepository.save(folder);
        }
        else {
            throw new RuntimeException("Folder not found.");
        }
    }

    public Folder getFolder(int id) {
        return folderRepository.findById(id).orElse(null);
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
        for (Folders defaultFolder : Folders.values()) {
            Folder folder = new Folder(defaultFolder.getStringRepresentation(),
                                        userId);
            folderRepository.save(folder);
        }
    }

    public Folder getFolder(int userId, Folders folderEnum) {
        Folder folder = folderRepository.findByUserIdAndLabel(userId,
                folderEnum.getStringRepresentation()).orElse(null);
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

    public void deleteEmailFromFolder(int emailId, int folderId) {
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
                .findAllByLabel(Folders.TRASH.getStringRepresentation())
                .forEach(folder -> {
                    emptyFolder(folder.getId());
                    folderRepository.save(folder);
                });
    }
}
