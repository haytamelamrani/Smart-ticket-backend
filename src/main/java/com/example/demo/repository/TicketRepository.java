package com.example.demo.repository;

import com.example.demo.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    long countByEtat(String etat);

    @Query("SELECT t.etat AS etat, COUNT(t) AS count FROM Ticket t GROUP BY t.etat")
    List<Object[]> countByEtatGroup();

    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (etat_updated_at - created_at)) / 3600) 
        FROM ticket 
        WHERE etat_updated_at IS NOT NULL
    """, nativeQuery = true)
    Double avgResolutionTimeInHours();

    @Query("SELECT AVG(t.clientRating) FROM Ticket t WHERE t.clientRating IS NOT NULL")
    Double averageClientRating();

    @Query(value = """
        SELECT TO_CHAR(created_at, 'YYYY-MM-DD') AS day, COUNT(*) 
        FROM ticket 
        GROUP BY day 
        ORDER BY day ASC
    """, nativeQuery = true)
    List<Object[]> countByDay();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo IS NULL")
    Long countUnassigned();

    @Query("SELECT t.priority AS priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> countGroupByPriority();

    @Query("SELECT t.type AS type, COUNT(t) FROM Ticket t GROUP BY t.type")
    List<Object[]> countGroupByType();
}
