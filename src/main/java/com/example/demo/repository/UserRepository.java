package com.example.demo.repository;

import com.example.demo.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /**
     * 🧍 Nombre total d’utilisateurs
     */
    long count();

    /**
     * 🧑‍💻 Nombre d’agents actifs : ceux qui ont au moins traité un ticket
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN Ticket t ON t.assignedTo = u WHERE u.role = 'AGENT'")
    long countActiveAgents();
}
