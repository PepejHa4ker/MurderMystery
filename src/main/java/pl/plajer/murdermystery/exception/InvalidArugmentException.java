package pl.plajer.murdermystery.exception;


public class InvalidArugmentException extends RuntimeException {

    public InvalidArugmentException(String message) {
        super(message);
    }

    public InvalidArugmentException() {
        super("Invalid argument");
    }


}
