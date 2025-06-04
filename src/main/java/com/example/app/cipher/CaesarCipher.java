package com.example.app.cipher;

import com.example.app.exception.CipherException;

import static com.example.app.util.MenuChoiceHandler.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CaesarCipher {
    private static final String ENGLISH_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String RUSSIAN_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    // Most frequent letters in English and Russian
    private static final char MOST_FREQUENT_ENGLISH = 'e';
    private static final char MOST_FREQUENT_RUSSIAN = 'о';

    public String encrypt(String text, int shift) {
        return process(text, shift);
    }

    public String decrypt(String text, int shift) {
        return process(text, -shift);
    }

    public String decryptWithoutShift(String text, Scanner scanner) {
        if (text == null || text.isEmpty()) {
            throw new CipherException("Input text cannot be empty");
        }

        // Determine if the text is primarily English or Russian
        boolean isEnglish = isEnglishText(text);
        
        // Count letter frequencies
        Map<Character, Integer> frequencies = calculateFrequencies(text.toLowerCase());
        if (frequencies.isEmpty()) {
            throw new CipherException("No letters found in the text");
        }

        // Find the most frequent letter in the text
        char mostFrequent = findMostFrequentLetter(frequencies);
        
        // Calculate the required shift
        int shift;
        if (isEnglish) {
            shift = calculateShift(mostFrequent, MOST_FREQUENT_ENGLISH, ENGLISH_ALPHABET);
        } else {
            shift = calculateShift(mostFrequent, MOST_FREQUENT_RUSSIAN, RUSSIAN_ALPHABET);
        }

        List<String> alternatives = new ArrayList<>();
        // Try the most likely shift
        alternatives.add(decrypt(text, shift));

        // Also get the next two most likely alternatives
        alternatives.add(decrypt(text, (shift + 1) % (isEnglish ? 26 : 33)));
        alternatives.add(decrypt(text, (shift - 1 + (isEnglish ? 26 : 33)) % (isEnglish ? 26 : 33)));

        // Let user choose from alternatives
        System.out.println("\nPossible decryptions (most likely first):");
        for (int i = 0; i < alternatives.size(); i++) {
            System.out.printf("%d: %s%n", i + 1, alternatives.get(i));
        }

        int choice = getNumChoice(scanner, 3);

        return alternatives.get(choice - 1);
    }

    public String encryptFromFile(String filePath, int shift) throws IOException {
        String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        return encrypt(content, shift);
    }

    private String process(String text, int shift) {
        if (text == null || text.isEmpty()) {
            throw new CipherException("Input text cannot be empty");
        }

        StringBuilder result = new StringBuilder();
        
        for (char ch : text.toCharArray()) {
            result.append(shiftChar(ch, shift));
        }
        
        return result.toString();
    }

    private char shiftChar(char ch, int shift) {
        if (!Character.isLetter(ch)) {
            return ch;
        }

        String alphabet;
        int index;
        boolean isUpperCase = Character.isUpperCase(ch);
        ch = Character.toLowerCase(ch);

        if (ENGLISH_ALPHABET.indexOf(ch) != -1) {
            alphabet = ENGLISH_ALPHABET;
            index = ENGLISH_ALPHABET.indexOf(ch);
        } else if (RUSSIAN_ALPHABET.indexOf(ch) != -1) {
            alphabet = RUSSIAN_ALPHABET;
            index = RUSSIAN_ALPHABET.indexOf(ch);
        } else {
            return ch;
        }

        // Handle wrap-around with modulo
        int length = alphabet.length();
        int newIndex = (index + shift) % length;
        if (newIndex < 0) {
            newIndex += length;
        }

        char shiftedChar = alphabet.charAt(newIndex);
        return isUpperCase ? Character.toUpperCase(shiftedChar) : shiftedChar;
    }

    private boolean isEnglishText(String text) {
        int englishCount = 0;
        int russianCount = 0;
        
        for (char c : text.toLowerCase().toCharArray()) {
            if (ENGLISH_ALPHABET.indexOf(c) != -1) {
                englishCount++;
            } else if (RUSSIAN_ALPHABET.indexOf(c) != -1) {
                russianCount++;
            }
        }
        
        return englishCount >= russianCount;
    }

    private Map<Character, Integer> calculateFrequencies(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                frequencies.merge(c, 1, Integer::sum);
            }
        }
        
        return frequencies;
    }

    private char findMostFrequentLetter(Map<Character, Integer> frequencies) {
        return frequencies.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new CipherException("No letters found in text"));
    }

    private int calculateShift(char mostFrequent, char expectedMostFrequent, String alphabet) {
        int currentPos = alphabet.indexOf(Character.toLowerCase(mostFrequent));
        int expectedPos = alphabet.indexOf(Character.toLowerCase(expectedMostFrequent));
        if (currentPos == -1 || expectedPos == -1) {
            throw new CipherException("Invalid character in frequency analysis");
        }
        return (currentPos - expectedPos + alphabet.length()) % alphabet.length();
    }
}
