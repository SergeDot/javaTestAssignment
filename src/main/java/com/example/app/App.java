package com.example.app;

import com.example.app.cipher.CaesarCipher;
import com.example.app.calculator.ArithmeticEvaluator;

import java.io.IOException;
import java.util.Scanner;

import static com.example.app.util.MenuChoiceHandler.*;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CaesarCipher cipher = new CaesarCipher();
    private static final ArithmeticEvaluator evaluator = new ArithmeticEvaluator();

    public static void main(String[] args) {
        displayWelcomeMessage();

        while (true) {
            displayMenu();
            int choice = getNumChoice(scanner, 4);
            try {
                processChoice(choice);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (!continueProgram()) {
                break;
            }
        }
        scanner.close();
        System.out.println("Thank you for using the application. Goodbye!");
    }

    private static void displayWelcomeMessage() {
        System.out.println("Welcome to Gehtsoft Technical Assessment by Serge Kabarin");
        System.out.println("=======================================");
    }

    private static void displayMenu() {
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Caesar Cipher Encryption");
        System.out.println("2. Caesar Cipher Decryption");
        System.out.println("3. Arithmetic Expression Evaluation");
        System.out.println("4. Exit");
    }

    private static void processChoice(int choice) throws IOException {
        switch (choice) {
            case 1:
                handleEncryption();
                break;
            case 2:
                handleDecryption();
                break;
            case 3:
                handleArithmeticEvaluation();
                break;
            case 4:
                System.exit(0);
                break;
        }
    }

    private static void handleEncryption() throws IOException {
        System.out.println("\n=== Caesar Cipher Encryption ===");
        System.out.println("Choose input method:");
        System.out.println("1. Console input");
        System.out.println("2. File input");
        
        int choice = getNumChoice(scanner, 2);
        String text;
        
        if (choice == 1) {
            System.out.print("Enter text to encrypt: ");
            text = scanner.nextLine();
        } else {
            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine();
            String encryptedText = cipher.encryptFromFile(filePath, getShiftValue());
            System.out.println("File content loaded successfully.");
            System.out.println("Result: " + encryptedText);
            return;
        }

        int shift = getShiftValue();
        String encryptedText = cipher.encrypt(text, shift);
        System.out.println("Result: " + encryptedText);
    }

    private static void handleDecryption() {
        System.out.println("\n=== Caesar Cipher Decryption ===");
        System.out.println("Do you know the shift value? (y/n)");
        boolean hasShift = scanner.nextLine().trim().toLowerCase().startsWith("y");

        System.out.print("Enter text to decrypt: ");
        String text = scanner.nextLine();

        String decryptedText;
        if (hasShift) {
            int shift = getShiftValue();
            decryptedText = cipher.decrypt(text, shift);
        } else {
            decryptedText = cipher.decryptWithoutShift(text, scanner);
        }
        System.out.println("Result: " + decryptedText);
    }

    private static void handleArithmeticEvaluation() {
        System.out.println("\n=== Arithmetic Expression Evaluation ===");
        System.out.print("Enter arithmetic expression: ");
        String expression = scanner.nextLine();
        
        double result = evaluator.evaluate(expression);
        System.out.printf("Result: %s", result);
    }

    private static int getShiftValue() {
        while (true) {
            System.out.print("Enter shift value: ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static boolean continueProgram() {
        System.out.print("\nContinue? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.startsWith("y");
    }
}
