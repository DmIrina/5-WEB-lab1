package output;

import java.io.*;
import java.util.regex.Pattern;

public class OutputWriter {
    private File outputFile;

    private final Pattern searchedNumPattern = Pattern.compile(
            "^([1-9]\\d{2,4})[[:punct:]]?$|^(\\d{1}\\.\\d{1,3}|\\d{2}\\.\\d{1,2}|\\d{3}\\.\\d{1})$");

    public OutputWriter() {
        this.outputFile = new File("src\\output\\output.txt");
        outputFile.delete();
        this.outputFile = new File("src\\output\\output.txt");
    }

    public File writeToOutputFile(File inputFile) {
        String path = String.valueOf(inputFile.getAbsoluteFile());
        StringBuilder originalContent = new StringBuilder();

        int numsCounter = 0;
        try (FileWriter fileWriter = new FileWriter(outputFile.getAbsoluteFile(), true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String temp = word;
                    String lastChar = "";
                    StringBuilder lastChars = new StringBuilder();
                    if (word.length() > 1) {
                        while (Pattern.compile("\\p{Punct}").matcher(
                                        String.valueOf(temp.charAt(temp.length() - 1)))
                                .matches()) {

                            lastChar = String.valueOf(temp.charAt(temp.length() - 1));
                            temp = temp.substring(0, temp.length() - 1);
                            lastChars.append(lastChar);

                        }
                    }
                    if (searchedNumPattern.matcher(temp).matches()) {
                        numsCounter++;
                        originalContent.append(lastChars.reverse());
                    } else {
                        originalContent.append(word);
                    }
                    originalContent.append(" ");
                }
                originalContent.append("\n");
            }

            if (numsCounter > 0) {
                synchronized (this) {
                    bufferedWriter.write(inputFile.getName() +
                            ", кількість видалених чисел: " + numsCounter + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (numsCounter > 0) {
            inputFile.delete();
            File newFile = new File(path);
            return rewriteFile(originalContent.toString(), newFile);
        } else {
            return inputFile;
        }
    }

    private File rewriteFile(String content, File newFile) {
        try (FileWriter fileWriter = new FileWriter(newFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

}
