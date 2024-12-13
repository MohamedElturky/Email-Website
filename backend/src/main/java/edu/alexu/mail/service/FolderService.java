package edu.alexu.mail.service;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.FolderRepository;
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

    public void removeFolder(int id) {
        folderRepository.deleteById(id);
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

    public Folder getFolderById(int id) {
        return folderRepository.findById(id).orElse(null);
    }

    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }
}
