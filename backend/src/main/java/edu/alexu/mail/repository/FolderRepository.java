package edu.alexu.mail.repository;

import edu.alexu.mail.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
}
