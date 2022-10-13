import exceptions.WrongInputException;
import output.OutputWriter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Task {
    private File directory;
    private final Pattern directoryPattern = Pattern.compile("^[a-zA-Z]:\\\\(?:\\w+\\\\?)*$");
    private OutputWriter outputWriter;
    private File output;


    public Task() {
        this.outputWriter = new OutputWriter();
    }

    // main method
    public File lookForFiles(String directoryPath) throws WrongInputException {
        this.directory = checkInputDirectory(directoryPath);
        searchInDirectory(directory);
        return output;
    }

    private void searchInDirectory(File directory) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            System.out.println("Потік: " + Thread.currentThread().getName() + ". Директорія: " + directory.getName());
            processDirectory(directory);

        });
        executor.shutdown();
    }

    // look for files in a directory
    private void processDirectory(File directory) {
        File[] files = directory.listFiles();
        if (isDirectoryEmpty(files)) {
            System.out.println("Поточна директорія " + directory.getName() + " порожня.");
            return;
        } else {
            System.out.println("Директорія " + directory.getName() + " знайдена, розпочато пошук файлів.");
            System.out.println("Кількість файлів у директорії " + directory.getName() + ": " + files.length);
        }

        // знаходження списку txt.файлів у цій директорії
        List<File> txtFiles = Arrays.stream(files)
                .filter(file -> (file.isFile() && file.getName().endsWith(".txt")))
                .collect(Collectors.toList());

        // для всіх знайдених директорій - (рекурсивний) пошук в них файлів
        Arrays.stream(files)
                .filter(this::isDirectory)
                .forEach(this::searchInDirectory);

        if (isDirectoryEmpty(txtFiles)) {
            System.out.println("У директорії " + directory.getName() + " не знайдено txt.файлів");
        } else {

            ExecutorService service = Executors.newFixedThreadPool(txtFiles.size());
            System.out.println("Кількість txt.файлів у директорії " + directory.getName() + ": " + txtFiles.size());

            for (File file : txtFiles) {
                service.execute(() -> {
                    System.out.println("Потік: " + Thread.currentThread().getName() + ". Файл: " + file.getName());
                    this.output = outputWriter.writeToOutputFile(file);
                });
            }

            service.shutdown();

        }
    }


    private File checkInputDirectory(String directoryPath) throws WrongInputException {
        if (!isDirectoryFormatCorrect(directoryPath)) {
            throw new WrongInputException("Неправильно введений формат директорії (X:\\X\\...\\X).");
        }

        File directory = new File(directoryPath);

        if (!isDirectory(directory)) {
            throw new WrongInputException("Не вдалося знайти директорію по даному шляху.");
        }

        return directory;
    }

    private boolean isDirectoryFormatCorrect(String directoryPath) {
        return directoryPattern.matcher(directoryPath).matches();
    }

    private boolean isDirectory(File directory) {
        return (directory.isDirectory() && directory.exists());
    }

    private boolean isDirectoryEmpty(File[] files) {
        return (files == null || files.length == 0);
    }

    private boolean isDirectoryEmpty(List<File> files) {
        return (files == null || files.size() == 0);
    }
}
