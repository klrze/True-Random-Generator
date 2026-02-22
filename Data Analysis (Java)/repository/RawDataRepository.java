package repository;

import entity.RawData; // Indicates object that is being managed
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
