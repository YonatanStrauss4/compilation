package IR;

public class FIELD_ENTRY {
    public String fieldName;
    public String className;
    public String type;
    public int value;

    public FIELD_ENTRY(String fieldName, String className, String type, int value) {
        this.fieldName = fieldName;
        this.className = className;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}