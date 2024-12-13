package io.codecrafters.shell;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Shell(new Scanner(System.in), System.out).execute();
    }
}
