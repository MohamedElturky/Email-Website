package edu.alexu.mail.repository;

import edu.alexu.mail.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findAllByUserId(int userId);

    List<Folder> findAllByLabel(String label);
}
