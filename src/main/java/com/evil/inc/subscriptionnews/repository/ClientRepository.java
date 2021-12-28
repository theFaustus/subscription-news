package com.evil.inc.subscriptionnews.repository;

import com.evil.inc.subscriptionnews.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findByEmail(String email);
    List<Client> findByCreatedAtAfter(LocalDateTime createdDate);
}
