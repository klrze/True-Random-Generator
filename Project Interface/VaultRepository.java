package com.example.trng;

// Template provided by Spring w/ standard database logic
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indicates that this is a repository class
public interface VaultRepository extends JpaRepository<SecureEntry, Long> {
}
