package exceptions;

public class WrongInputException extends Exception {

    public WrongInputException() {
        super("Дані введено некоректно.");
    }

    public WrongInputException(String s) {
        super(s);
    }
}
