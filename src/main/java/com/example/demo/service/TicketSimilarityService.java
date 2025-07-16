package com.example.demo.service;

import com.example.demo.entity.KnowledgeBaseTicket;
import com.example.demo.entity.Ticket;
import com.example.demo.util.TicketVectorizer;

import lombok.RequiredArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketSimilarityService {

    private final TicketVectorizer vectorizer;  

    public Map<Integer, List<Ticket>> grouperTicketsParSimilarite(List<Ticket> tickets, double seuil) {
        Map<Integer, List<Ticket>> groupes = new HashMap<>();
        int groupId = 0;

        for (Ticket ticket : tickets) {
            boolean ajouté = false;
            for (Map.Entry<Integer, List<Ticket>> entry : groupes.entrySet()) {
                for (Ticket autre : entry.getValue()) {
                    double sim = calculerSimilarite(ticket, autre);
                    if (sim >= seuil) {
                        entry.getValue().add(ticket);
                        ajouté = true;
                        break;
                    }
                }
                if (ajouté) break;
            }

            if (!ajouté) {
                groupes.put(groupId++, new ArrayList<>(List.of(ticket)));
            }
        }

        return groupes;
    }

    public Optional<String> proposerSolution(Ticket ticket, List<KnowledgeBaseTicket> knowledgeBase, double seuil) {
        double maxSim = 0;
        String bestSolution = null;

        for (KnowledgeBaseTicket kb : knowledgeBase) {
            double sim = calculerSimilarite(ticket.getTitle() + " " + ticket.getDescription(), kb.getTitle() + " " + kb.getDescription());
            if (sim > maxSim && sim >= seuil) {
                maxSim = sim;
                bestSolution = kb.getSolution();
            }
        }

        return Optional.ofNullable(bestSolution);
    }

    private double calculerSimilarite(Ticket t1, Ticket t2) {
        return calculerSimilarite(t1.getTitle() + " " + t1.getDescription(),
                                  t2.getTitle() + " " + t2.getDescription());
    }

    private double calculerSimilarite(String text1, String text2) {
        INDArray vec1 = vectorizer.vectorize(text1);
        INDArray vec2 = vectorizer.vectorize(text2);

        double dot = Nd4j.getBlasWrapper().dot(vec1, vec2);
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (norm1 * norm2);
    }
}
