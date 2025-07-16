package com.example.demo.util;

import lombok.Getter;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketVectorizer {

    @Getter
    private Word2Vec word2Vec;

    @PostConstruct
    public void init() {
        System.out.println("🚀 Chargement du modèle Word2Vec...");
        File file = new File("word2vec-model.zip");  // ou .csv si tu as exporté ainsi
        if (file.exists()) {
            try {
                word2Vec = WordVectorSerializer.readWord2VecModel(file);
                System.out.println("✅ Modèle chargé avec succès.");
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement du modèle : " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Aucun modèle pré-entrainé trouvé. Veuillez entraîner Word2Vec d'abord.");
        }
    }

    private static final File MODEL_FILE = new File("models/word2vec_model.zip");

    public void train(List<String> texts) {
        System.out.println("🚀 Démarrage de l'entraînement Word2Vec...");

        if (MODEL_FILE.exists()) {
            System.out.println("📦 Modèle déjà entraîné trouvé. Chargement...");
            word2Vec = WordVectorSerializer.readWord2VecModel(MODEL_FILE);
            return;
        }

        // ✅ Nettoyage et suppression de stopwords
        Set<String> stopwords = Set.of("le", "la", "les", "un", "une", "de", "des", "du", "en", "et", "à", "au", "aux",
                "ce", "cet", "cette", "ces", "pour", "par", "sur", "dans", "avec", "sans", "ne", "pas");

        List<String> cleanedTexts = texts.stream()
                .map(text -> Arrays.stream(
                        text.toLowerCase()
                                .replaceAll("[^a-zàâçéèêëîïôûùüÿñæœ\\s]", "")
                                .replaceAll("\\s+", " ")
                                .trim()
                                .split(" "))
                        .filter(token -> !stopwords.contains(token))
                        .collect(Collectors.joining(" ")))
                .toList();

        CollectionSentenceIterator iterator = new CollectionSentenceIterator(cleanedTexts);
        DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(token -> token.toLowerCase().trim());

        word2Vec = new Word2Vec.Builder()
                .minWordFrequency(2)
                .iterations(20)
                .epochs(1)
                .layerSize(200)
                .windowSize(10)
                .learningRate(0.025)
                .seed(42)
                .tokenizerFactory(tokenizerFactory)
                .iterate(iterator)
                .build();

        long start = System.currentTimeMillis();
        word2Vec.fit();
        long duration = System.currentTimeMillis() - start;

        // 🔐 Sauvegarde du modèle entraîné
        try {
            MODEL_FILE.getParentFile().mkdirs(); // Crée le dossier models/ si inexistant
            WordVectorSerializer.writeWord2VecModel(word2Vec, MODEL_FILE);
            System.out.println("✅ Modèle sauvegardé dans : " + MODEL_FILE.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde du modèle : " + e.getMessage());
        }

        // 🔍 Exemple
        System.out.println("📚 Vocabulaire appris : " + word2Vec.getVocab().numWords() + " mots");
        System.out.println("🕒 Temps d'entraînement : " + duration + " ms");
        if (word2Vec.hasWord("connexion")) {
            System.out.println("🔤 Mots proches de 'connexion' : " + word2Vec.wordsNearest("connexion", 5));
        }
    }

    public void loadModelIfExists() {
        if (MODEL_FILE.exists()) {
            word2Vec = WordVectorSerializer.readWord2VecModel(MODEL_FILE);
            System.out.println("✅ Modèle Word2Vec chargé depuis : " + MODEL_FILE.getAbsolutePath());
        } else {
            System.err.println("⚠️ Aucun modèle trouvé à : " + MODEL_FILE.getAbsolutePath());
        }
    }

    public INDArray vectorize(String text) {
        if (word2Vec == null) return Nd4j.zeros(200);

        List<String> tokens = Arrays.asList(text.toLowerCase().split("\\s+"));

        List<INDArray> vectors = tokens.stream()
                .filter(word2Vec::hasWord)
                .map(word2Vec::getWordVectorMatrix)
                .collect(Collectors.toList());

        if (vectors.isEmpty()) return Nd4j.zeros(word2Vec.getLayerSize());

        INDArray sum = vectors.get(0).dup();
        for (int i = 1; i < vectors.size(); i++) {
            sum.addi(vectors.get(i));
        }

        return sum.div(vectors.size());
    }

    public void exportWordVectorsAsCSV(String outputPath) {
        System.out.println("💾 Export des vecteurs Word2Vec vers " + outputPath);

        try (PrintWriter writer = new PrintWriter(new File(outputPath))) {
            int vectorSize = word2Vec.getLayerSize();
            StringBuilder header = new StringBuilder("word");
            for (int i = 0; i < vectorSize; i++) {
                header.append(",dim").append(i);
            }
            writer.println(header);

            for (String word : word2Vec.getVocab().words()) {
                double[] vector = word2Vec.getWordVector(word);
                writer.print(word);
                for (double val : vector) {
                    writer.print("," + val);
                }
                writer.println();
            }

            System.out.println("✅ Vecteurs enregistrés dans " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadModel(String path) {
        try {
            System.out.println("📦 Chargement du modèle Word2Vec depuis " + path);
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("❌ Le fichier du modèle n'existe pas : " + path);
                return;
            }
            word2Vec = WordVectorSerializer.readWord2VecModel(file);
            System.out.println("✅ Modèle Word2Vec chargé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
