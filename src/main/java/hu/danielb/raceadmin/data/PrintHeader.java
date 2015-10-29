package hu.danielb.raceadmin.data;

public class PrintHeader {

    public static final String TABLE = "print_header";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TEXT = "text";

    int id;
    String name;
    String text;

    public PrintHeader(int id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFlText() {
        return text;
    }

    @Override
    public String toString() {
        return name;
    }
}
