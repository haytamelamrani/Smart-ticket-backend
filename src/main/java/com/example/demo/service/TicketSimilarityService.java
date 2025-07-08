package com.example.demo.service;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class TicketSimilarityService {

    // --- 🔤 Nettoyage et normalisation du texte français ---
    private String normalizeFrench(String text) {
        if (text == null) return "";
        text = text.toLowerCase();

        // Supprimer les accents
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                         .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Remplacer les synonymes connus (exemples simples)
        text = text.replace("se connecter", "connexion");
        text = text.replace("connection", "connexion");
        text = text.replace("mot de passe", "password");
        text = text.replace("passworld", "password"); // faute fréquente
        text = text.replace("oublié", "oublier");
        text = text.replace("oubli", "oublier");

        // Nettoyage des caractères
        text = text.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();

        return text;
    }

    // Construction du vocabulaire global
    private Set<String> buildVocabulary(List<String> texts) {
        Set<String> vocab = new HashSet<>();
        for (String text : texts) {
            String[] words = normalizeFrench(text).split("\\s+");
            vocab.addAll(Arrays.asList(words));
        }
        return vocab;
    }

    // TF: fréquence des mots dans un texte
    private Map<String, Double> computeTF(String[] words) {
        Map<String, Double> tf = new HashMap<>();
        for (String word : words) {
            tf.put(word, tf.getOrDefault(word, 0.0) + 1);
        }
        for (String word : tf.keySet()) {
            tf.put(word, tf.get(word) / words.length);
        }
        return tf;
    }

    // IDF: inverse de la fréquence des documents
    private Map<String, Double> computeIDF(List<String[]> allWords, Set<String> vocab) {
        Map<String, Double> idf = new HashMap<>();
        int totalDocs = allWords.size();

        for (String word : vocab) {
            int count = 0;
            for (String[] docWords : allWords) {
                if (Arrays.asList(docWords).contains(word)) {
                    count++;
                }
            }
            idf.put(word, Math.log((double) totalDocs / (1 + count)));
        }

        return idf;
    }

    // TF-IDF vector d'un texte
    private Map<String, Double> computeTFIDF(String[] words, Map<String, Double> idf) {
        Map<String, Double> tf = computeTF(words);
        Map<String, Double> tfidf = new HashMap<>();
        for (String word : tf.keySet()) {
            tfidf.put(word, tf.get(word) * idf.getOrDefault(word, 0.0));
        }
        return tfidf;
    }

    // Similarité cosinus entre deux vecteurs
    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(vec1.keySet());
        allKeys.addAll(vec2.keySet());

        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String key : allKeys) {
            double v1 = vec1.getOrDefault(key, 0.0);
            double v2 = vec2.getOrDefault(key, 0.0);
            dot += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Regrouper les tickets similaires
    public List<List<String>> groupSimilarTickets(List<String> tickets, double threshold) {
        List<String[]> allWords = tickets.stream()
                .map(t -> normalizeFrench(t).split("\\s+"))
                .collect(Collectors.toList());

        Set<String> vocab = buildVocabulary(tickets);
        Map<String, Double> idf = computeIDF(allWords, vocab);

        List<Map<String, Double>> tfidfVectors = allWords.stream()
                .map(words -> computeTFIDF(words, idf))
                .collect(Collectors.toList());

        boolean[] visited = new boolean[tickets.size()];
        List<List<String>> groups = new ArrayList<>();

        for (int i = 0; i < tickets.size(); i++) {
            if (visited[i]) continue;

            List<String> group = new ArrayList<>();
            group.add(tickets.get(i));
            visited[i] = true;

            for (int j = i + 1; j < tickets.size(); j++) {
                if (!visited[j]) {
                    double sim = cosineSimilarity(tfidfVectors.get(i), tfidfVectors.get(j));
                    if (sim >= threshold) {
                        group.add(tickets.get(j));
                        visited[j] = true;
                    }
                }
            }

            groups.add(group);
        }

        return groups;
    }
}
