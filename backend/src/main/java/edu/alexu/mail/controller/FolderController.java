package edu.alexu.mail.controller;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.service.FolderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/folder")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public Folder addFolder(@RequestBody Folder folder) {
        return folderService.addFolder(folder);
    }

    @DeleteMapping
    public void removeFolder(@RequestParam int id) {
        folderService.deleteFolder(id);
    }

    @PutMapping
    public Folder renameFolder(@RequestParam int id, @RequestParam String label) {
        return folderService.renameFolder(id, label);
    }

    @GetMapping
    public Folder getFolderById(@RequestParam int id) {
        return folderService.getFolder(id);
    }

    @GetMapping("/all")
    public List<Folder> getAllFoldersByUserId(@RequestParam int userId) {
        return folderService.getAllFolders(userId);
    }

    @PutMapping("/move")
    public void moveEmails(List<Integer> emailIds, int fromId, int toId) {
        folderService.moveEmails(emailIds, fromId, toId);
    }

}
