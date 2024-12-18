package edu.alexu.mail.service;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.FolderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

        if (folder != null) {
            if (Folder.defaultFolders.contains(folder.getLabel())) {
                throw new RuntimeException("Can't delete default folder");
            }
            else {
                folderRepository.deleteById(id);
            }
        }
        else {
            throw new RuntimeException("Folder not found");
        }
    }

    public Folder renameFolder(int id, String newLabel) {
        Folder folder = folderRepository.findById(id).orElse(null);
        if (folder != null) {
            folder.setLabel(newLabel);
            return folderRepository.save(folder);
        }
        else {
            throw new RuntimeException("Folder not found");
        }
    }

    public Folder getFolder(int id) {
        return folderRepository.findById(id).orElse(null);
    }

    public List<Folder> getAllFolders(int userId) {
        return folderRepository.findAllByUserId(userId);
    }

    public void moveEmail(int emailId, int fromId, int toId) {
        Folder fromFolder = getFolder(fromId);
        Folder toFolder = getFolder(toId);

        fromFolder.getEmailsIds().remove(emailId);
        toFolder.getEmailsIds().add(emailId);

        folderRepository.save(fromFolder);
        folderRepository.save(toFolder);
    }

    @Scheduled(cron = "@midnight")
    private void clearTrashFolder() {
        List<Folder> trashFolders = folderRepository.findAllByLabel("Trash");
        for (Folder folder : trashFolders) {
            folder.empty();
        }
    }
}
