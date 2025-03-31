package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

class ClassEntry {
    Stack<FIELD_ENTRY> fields;
    Stack<METHOD_ENTRY> methods;
    String parentName;

    public ClassEntry(String parentName) {
        this.fields = new Stack<>();
        this.methods = new Stack<>();
        this.parentName = parentName;
    }
}

public class CLASSES_MAP {
    private HashMap<String, ClassEntry> map;
    private static CLASSES_MAP instance = null;

    // build the classes map
    private CLASSES_MAP() {
        map = new HashMap<>();
    }

    // Singleton
    public static CLASSES_MAP getInstance() {
        if (instance == null) {
            instance = new CLASSES_MAP();
        }
        return instance;
    }

    // Insert a class with inherited fields and methods from its parent
    public void insertClass(String className, String parentName) {
        if (!map.containsKey(className)) {
            ClassEntry newClassEntry = new ClassEntry(parentName);

            // Inherit fields and methods from the parent class
            if (parentName != null && map.containsKey(parentName)) {
                ClassEntry parentEntry = map.get(parentName);

                // Copy fields
                newClassEntry.fields.addAll(parentEntry.fields);

                // Copy methods
                newClassEntry.methods.addAll(parentEntry.methods);
            }

            map.put(className, newClassEntry);
        }
    }

    // Insert a field into a class
    public void insertField(String fieldName, String className, String fieldType, int value) {
        ClassEntry classEntry = map.get(className);
        if (classEntry == null) {
            return;
        }

        FIELD_ENTRY newField = new FIELD_ENTRY(fieldName, className, fieldType, value);
        classEntry.fields.push(newField);
    }

    public void insertMethod(String className, String methodName) {
        ClassEntry classEntry = map.get(className);
        if (classEntry == null) {
            return;
        }

        // Iterate through the stack to check for the method
        for (int i = 0; i < classEntry.methods.size(); i++) {
            if (classEntry.methods.get(i).methodName.equals(methodName)) {
                // Modify the method entry in place instead of removing and reinserting
                classEntry.methods.set(i, new METHOD_ENTRY(methodName, className));
                return;
            }
        }

        // If the method doesn't exist, push a new one onto the stack
        classEntry.methods.push(new METHOD_ENTRY(methodName, className));
    }

    // Retrieve all fields (only those defined in the class, no inherited fields)
    public List<FIELD_ENTRY> getFields(String className) {
        ClassEntry classEntry = map.get(className);
        if (classEntry == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(classEntry.fields);
    }

    // Retrieve all methods (only those defined in the class, no inherited methods)
    public List<METHOD_ENTRY> getMethods(String className) {
        ClassEntry classEntry = map.get(className);
        if (classEntry == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(classEntry.methods);
    }

    // get field offset
    public int getFieldOffset(String className, String fieldName) {
        List<FIELD_ENTRY> fields = getFields(className);
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).fieldName.equals(fieldName)) {
                return (i + 1) * 4;
            }
        }
        return -1;
    }

    // get number of fields for a specific class
    public int getNumOfFields(String className) {
        return getFields(className).size();
    }

    // get method offset
    public int getMethodOffset(String className, String methodName) {
        List<METHOD_ENTRY> methods = getMethods(className);
        for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i).methodName.equals(methodName)) {
                return i * 4;
            }
        }
        return -1;
    }

    // get number of methods for a specific class
    public int getNumOfMethods(String className) {
        return getMethods(className).size();
    }

    // print the classes map
    public void printTable() {
        System.out.println("CLASSES MAP:");
        for (Map.Entry<String, ClassEntry> entry : map.entrySet()) {
            System.out.println("Class: " + entry.getKey() + " | Parent: " + entry.getValue().parentName);
            System.out.println("Fields:");
            // Print fields from bottom to top
            List<FIELD_ENTRY> fields = new ArrayList<>(entry.getValue().fields);
            Collections.reverse(fields);
            for (FIELD_ENTRY field : fields) {
                System.out.println(field);
            }
            System.out.println("Methods:");
            // Print methods from bottom to top
            List<METHOD_ENTRY> methods = new ArrayList<>(entry.getValue().methods);
            Collections.reverse(methods);
            for (METHOD_ENTRY method : methods) {
                System.out.println(method);
            }
        }
    }
}
