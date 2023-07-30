package club.someoneice.togocup.recipebook;

public class ObjectNotFindException extends NullPointerException {
    public ObjectNotFindException(String fileName) {
        super("Cannot find a file in jar:" + fileName);
    }
}
