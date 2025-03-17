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

    // Insert a class with empty stacks
    public void insertClass(String className, String parentName) {
        if (!map.containsKey(className)) {
            map.put(className, new ClassEntry(parentName));
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

    // Insert a method into a class
    public void insertMethod(String className, String methodName) {
        ClassEntry classEntry = map.get(className);
        if (classEntry == null) {
            return;
        }

        METHOD_ENTRY newMethod = new METHOD_ENTRY(methodName, className);
        classEntry.methods.push(newMethod);

    }


    // Retrieve all fields (including inherited)
    public List<FIELD_ENTRY> getFields(String className) {
        List<FIELD_ENTRY> fields = new ArrayList<>();
        collectFields(className, fields, new HashSet<>());
        return fields;
    }

    private void collectFields(String className, List<FIELD_ENTRY> fields, Set<String> visitedClasses) {
        if (className == null || visitedClasses.contains(className)) return;

        ClassEntry classEntry = map.get(className);
        if (classEntry == null) return;  // Avoid NullPointerException

        visitedClasses.add(className);

        // First, recursively collect fields from the parent class
        if (classEntry.parentName != null && !classEntry.parentName.isEmpty()) {
            collectFields(classEntry.parentName, fields, visitedClasses);
        }

        // Then add the fields from the current class
        fields.addAll(classEntry.fields);
    }

// Retrieve all methods (including inherited, without duplicates)
    public List<METHOD_ENTRY> getMethods(String className) {
        List<METHOD_ENTRY> methods = new ArrayList<>();
        collectMethods(className, methods, new HashSet<>());
        return methods;
    }

    private void collectMethods(String className, List<METHOD_ENTRY> methods, Set<String> visitedClasses) {
        if (className == null || visitedClasses.contains(className)) return;

        visitedClasses.add(className);
        ClassEntry classEntry = map.get(className);
        if (classEntry != null) {
            for (METHOD_ENTRY method : classEntry.methods) {
                if (methods.stream().noneMatch(m -> m.methodName.equals(method.methodName))) {
                    methods.add(method);
                }
            }
            collectMethods(classEntry.parentName, methods, visitedClasses);
        }
    }

    // get field offset
    public int getFieldOffset(String className, String fieldName) {
        List<FIELD_ENTRY> fields = getFields(className);
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).fieldName.equals(fieldName)) {
                return (i+1)*4;
            }
        }
        return -1;
    }

    // get number of fields for a specific class
    public int getNumOfFields(String className) {
        return getFields(className).size();
    }

    // print the classes map
    public void printTable() {
        System.out.println("CLASSES MAP:");
        for (Map.Entry<String, ClassEntry> entry : map.entrySet()) {
            System.out.println("Class: " + entry.getKey() + " | Parent: " + entry.getValue().parentName);
            System.out.println("Fields:");
            for (FIELD_ENTRY field : entry.getValue().fields) {
                System.out.println(field);
            }
            System.out.println("Methods:");
            for (METHOD_ENTRY method : entry.getValue().methods) {
                System.out.println(method);
            }
        }
    }
}
