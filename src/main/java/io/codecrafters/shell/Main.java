package io.codecrafters.shell;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            System.out.println(scanner.nextLine() + ": command not found");
        }
    }
}
