import exceptions.WrongInputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {

        // path for testing:
        // D:\3grade\WEB\labs\lab1\lab1\src\data\testdata

        System.out.println("------------- Лабораторна робота №1 --------------");
        System.out.println("------------------ Варіант 13 --------------------");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Введіть директорію для пошуку файлів:");
            String directoryPath = reader.readLine();
            Task task = new Task();
            task.lookForFiles(directoryPath);
        } catch (WrongInputException | IOException e) {
            e.printStackTrace();
        }
    }
}