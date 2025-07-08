package com.example.demo.util;

import java.text.Normalizer;
import java.util.*;

public class SimilarityUtil {

    // 🔧 Nettoyage et normalisation de texte (accents, majuscules, ponctuation)
    private static String normalize(String text) {
        if (text == null) return "";

        text = text.toLowerCase();

        // Supprimer les accents
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                         .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Nettoyer ponctuation
        text = text.replaceAll("[^a-z0-9\\s]", " ");

        // Supprimer les chiffres si souhaité
        text = text.replaceAll("[0-9]", "");

        return text.replaceAll("\\s+", " ").trim();
    }

    // Convertit un texte en vecteur de fréquence des mots
    private static Map<String, Double> getWordFrequency(String text) {
        String[] words = normalize(text).split("\\s+");
        Map<String, Double> freq = new HashMap<>();

        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0.0) + 1.0);
        }

        // Normaliser (TF)
        for (String word : freq.keySet()) {
            freq.put(word, freq.get(word) / words.length);
        }

        return freq;
    }

    // 📏 Calcul de la similarité cosinus entre deux vecteurs de mots
    public static double computeCosineSimilarity(String text1, String text2) {
        Map<String, Double> vec1 = getWordFrequency(text1);
        Map<String, Double> vec2 = getWordFrequency(text2);

        Set<String> allWords = new HashSet<>();
        allWords.addAll(vec1.keySet());
        allWords.addAll(vec2.keySet());

        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (String word : allWords) {
            double v1 = vec1.getOrDefault(word, 0.0);
            double v2 = vec2.getOrDefault(word, 0.0);
            dot += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
