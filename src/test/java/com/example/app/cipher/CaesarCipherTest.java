package com.example.app.cipher;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import com.example.app.exception.CipherException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CaesarCipherTest {
    private CaesarCipher cipher;

    @BeforeMethod
    public void setUp() {
        cipher = new CaesarCipher();
    }

    @Test(description = "Test English text encryption")
    public void testEnglishEncryption() {
        String plaintext = "Hello World";
        String encrypted = cipher.encrypt(plaintext, 3);
        Assert.assertEquals(encrypted, "Khoor Zruog");
    }

    @Test(description = "Test Russian text encryption")
    public void testRussianEncryption() {
        String plaintext = "Привет Мир";
        String encrypted = cipher.encrypt(plaintext, 5);
        Assert.assertEquals(encrypted, "Фхнжйч Снх");
    }

    @DataProvider(name = "wrapAroundCases")
    public Object[][] wrapAroundCases() {
        return new Object[][] {
            {"abc", 1, "bcd"},
            {"xyz", 1, "yza"},
            {"ABC", 1, "BCD"},
            {"XYZ", 1, "YZA"},
            {"абв", 1, "бвг"},
            {"эюя", 1, "юяа"},
            {"АБВ", 1, "БВГ"},
            {"ЭЮЯ", 1, "ЮЯА"}
        };
    }

    @Test(dataProvider = "wrapAroundCases", description = "Test wrap-around cases")
    public void testWrapAround(String input, int shift, String expected) {
        Assert.assertEquals(cipher.encrypt(input, shift), expected);
    }

    @Test(description = "Test case preservation")
    public void testCasePreservation() {
        String plaintext = "HeLLo WoRLD";
        String encrypted = cipher.encrypt(plaintext, 3);
        Assert.assertEquals(encrypted, "KhOOr ZrUOG");
    }

    @Test(description = "Test non-alphabetic characters")
    public void testNonAlphabeticChars() {
        String plaintext = "Hello, World! 123";
        String encrypted = cipher.encrypt(plaintext, 3);
        Assert.assertEquals(encrypted, "Khoor, Zruog! 123");
    }

    @Test(description = "Test decryption")
    public void testDecryption() {
        String ciphertext = "Khoor Zruog";
        String decrypted = cipher.decrypt(ciphertext, 3);
        Assert.assertEquals(decrypted, "Hello World");
    }

    @Test(description = "Test negative shift")
    public void testNegativeShift() {
        String plaintext = "Hello";
        String encrypted = cipher.encrypt(plaintext, -3);
        Assert.assertEquals(encrypted, "Ebiil");
    }

    @Test(description = "Test empty input", expectedExceptions = CipherException.class)
    public void testEmptyInput() {
        cipher.encrypt("", 3);
    }

    @Test(description = "Test null input", expectedExceptions = CipherException.class)
    public void testNullInput() {
        cipher.encrypt(null, 3);
    }

    @Test(description = "Test file encryption")
    public void testFileEncryption() throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Hello World");

        String encrypted = cipher.encryptFromFile(tempFile.toString(), 3);
        Assert.assertEquals(encrypted, "Khoor Zruog");

        // Clean up
        Files.delete(tempFile);
    }

    @Test(description = "Test decryption without shift")
    public void testDecryptionWithoutShift() {
        String ciphertext = "Jrypbzr gb Trugfbsg Grpuavpny Nffrffzrag ol Fretr Xnoneva. Cyrnfr ragre n ahzore orgjrra 1 naq 4.";

        // Simulate user input selecting the first option
        ByteArrayInputStream in = new ByteArrayInputStream("1\n".getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        String decrypted = cipher.decryptWithoutShift(ciphertext, scanner);

        // Clean up
        scanner.close();
        System.setIn(System.in);

        Assert.assertEquals(decrypted,
                "Welcome to Gehtsoft Technical Assessment by Serge Kabarin. Please enter a number between 1 and 4.");
    }
    @Test(description = "Test decryption without shift - Russian text")
    public void testDecryptionWithoutShiftRussian() {
        String ciphertext = "Ечрлмшщрж — еъц яъц-ъц йшцлм хмчцхжъхцс рщъцшрр, — игщъшц чмшмймуз луж щмиж Хзщъж";

        // Simulate user input selecting the first option
        ByteArrayInputStream in = new ByteArrayInputStream("1\n".getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);

        String decrypted = cipher.decryptWithoutShift(ciphertext, scanner);

        // Clean up
        scanner.close();
        System.setIn(System.in);

        Assert.assertEquals(decrypted,
                "Эпидерсия — это что-то вроде непонятной истории, — быстро перевела для себя Настя");
    }

    @Test(description = "Test decryption without shift - empty input",
            expectedExceptions = CipherException.class,
            expectedExceptionsMessageRegExp = "Input text cannot be empty")
    public void testDecryptionWithoutShiftEmptyInput() {
        Scanner scanner = new Scanner(System.in);
        try {
            cipher.decryptWithoutShift("", scanner);
        } finally {
            // Clean up
            scanner.close();
        }
    }

}
