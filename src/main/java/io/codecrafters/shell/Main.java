package io.codecrafters.shell;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            var input = scanner.nextLine();
            if (input.equals("exit 0")) {
                System.exit(0);
            }
            System.out.println(input + ": command not found");
        }
    }
}
