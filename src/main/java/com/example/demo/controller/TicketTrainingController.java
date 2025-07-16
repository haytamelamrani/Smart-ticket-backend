package com.example.demo.controller;

import com.example.demo.entity.Ticket;
import com.example.demo.repository.TicketRepository;
import com.example.demo.util.TicketVectorizer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
public class TicketTrainingController {

    private final TicketRepository ticketRepository;
    private final TicketVectorizer vectorizer;

    @PostMapping("/word2vec")
    public ResponseEntity<?> trainWord2Vec() {
        List<Ticket> tickets = ticketRepository.findAll();

        List<String> corpus = tickets.stream()
                .map(t -> t.getTitle() + " " + t.getDescription())
                .toList();

        vectorizer.train(corpus);
        vectorizer.exportWordVectorsAsCSV("word-vectors.csv");
        return ResponseEntity.ok("✅ Word2Vec model trained on " + corpus.size() + " tickets.");
    }
}
