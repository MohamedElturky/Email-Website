package edu.alexu.mail.controller;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.service.FolderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/email")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/folder")
    public Folder addFolder(@RequestBody Folder folder) {
        return folderService.addFolder(folder);
    }

    @DeleteMapping("/folder")
    public void removeFolder(@RequestParam int id) {
        folderService.removeFolder(id);
    }

    @PutMapping("/folder")
    public Folder renameFolder(@RequestParam int id, @RequestParam String name) {
        return folderService.renameFolder(id, name);
    }

    @GetMapping("/folder")
    public Folder getFolder(@RequestParam int id) {
        return folderService.getFolderById(id);
    }

    @GetMapping("/folders")
    public List<Folder> getAllFolders() {
        return folderService.getAllFolders();
    }

}
