package com.example.app.util;

import com.example.app.exception.SelectionException;
import java.util.Scanner;

public class MenuChoiceHandler {

    public static int getNumChoice(Scanner scanner, int menuItems) {
        while (true) {
            System.out.printf("\nEnter your choice (1-%s): ", menuItems);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= menuItems) {
                    return choice;
                }
                throw new SelectionException();
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (SelectionException e) {
                System.out.printf("Please enter a number between 1 and %s.", menuItems);
            }
        }
    }
}
