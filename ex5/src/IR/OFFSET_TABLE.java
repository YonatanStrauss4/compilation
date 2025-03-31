package IR;
import java.util.*;

public class OFFSET_TABLE {
    public Stack<OFFSET_TABLE_ENTRY> offsetTable = new Stack<>();
    public Stack<OFFSET_TABLE_ENTRY> globalTable = new Stack<>();
    public int localOffset = -44;
    public int numOfLocals = 0;
    int scope = 1;

    // Method to enter a global variable
    public void enterGlobal(String varName, String type) {
        globalTable.push(new OFFSET_TABLE_ENTRY(varName, 0, type, false, null, 0, null, null));
    }

    // Method to get the type of a global variable
    public String getGlobalType(String varName) {
        for (OFFSET_TABLE_ENTRY entry : globalTable) {
            if (entry.name.equals(varName)) {
                return entry.type;
            }
        }
        return null;
    }

    // Method to enter a class
    public void enterClass(String className) {
        offsetTable.push(new OFFSET_TABLE_ENTRY(className, 0, "CLASS", false, className, 0, null, null));
    }

    // Method to enter a function
    public void enterFunction(String funcName, List<String> args) {
        offsetTable.push(new OFFSET_TABLE_ENTRY(funcName, 0, "FUNCTION", false, null, 1, funcName, args));
    }

    // Method to push the start of an inner scope
    public void pushInnerScopeStart() {
        offsetTable.push(new OFFSET_TABLE_ENTRY("inner_scope_start", 0, "SCOPE", false, null, scope, null, null));
        scope++;
    }

    // Method to push a variable
    public void pushVariable(String varName, String type, boolean isClassInstance, String className) {
        if(isClassInstance){
            offsetTable.push(new OFFSET_TABLE_ENTRY(varName, localOffset, type, isClassInstance, className,  scope, null, null));
        }
        else{
            offsetTable.push(new OFFSET_TABLE_ENTRY(varName, localOffset, type, isClassInstance, null,  scope, null, null));
        }
        localOffset -= 4;
        numOfLocals++;
    }

    // Method to find the offset of a variable
    public int findVariableOffset(String varName, int k) {
        ListIterator<OFFSET_TABLE_ENTRY> iterator = offsetTable.listIterator(offsetTable.size());
        while (iterator.hasPrevious()) {
            OFFSET_TABLE_ENTRY entry = iterator.previous();
            if (entry.name.equals(varName)) {
                return entry.offset;
            }
        }
        // If not found in variable entries, check function entry
        iterator = offsetTable.listIterator(offsetTable.size());
        while (iterator.hasPrevious()) {
            OFFSET_TABLE_ENTRY entry = iterator.previous();
            if ("FUNCTION".equals(entry.type) && entry.args != null) {
                for (int i = 0; i < entry.args.size(); i++) {
                    if (entry.args.get(i).equals(varName)) {
                        return (i+k) * 4 + 8;
                    }
                }
            }
        }
        return -1;
    }

    // Method to find the type of a variable
    public String findVariableType(String varName) {
        ListIterator<OFFSET_TABLE_ENTRY> iterator = offsetTable.listIterator(offsetTable.size());
        while (iterator.hasPrevious()) {
            OFFSET_TABLE_ENTRY entry = iterator.previous();
            if (entry.name.equals(varName)) {
                return entry.type;
            }
        }
        return null;
    }

    // Method to find a class instance
    public OFFSET_TABLE_ENTRY findClassInstance(String varName){
        ListIterator<OFFSET_TABLE_ENTRY> iterator = offsetTable.listIterator(offsetTable.size());
        while (iterator.hasPrevious()) {
            OFFSET_TABLE_ENTRY entry = iterator.previous();
            if (entry.name.equals(varName) && entry.isClassInstance) {
            return entry;
            }
        }
        return null;
    }

    // Method to end an inner scope
    public void endInnerScope() {
        Stack<OFFSET_TABLE_ENTRY> stack = offsetTable;
        int variableCount = 0;
        while (!stack.isEmpty() && !stack.peek().name.equals("inner_scope_start")) {
            stack.pop();
            variableCount++;
        }
        if (!stack.isEmpty()) {
            stack.pop(); // pop the inner_scope_start
        }
        localOffset += 4 * variableCount;
        scope--;
    }

    // Method to end function parsing
    public void endFunctionParse() {
        while (!offsetTable.isEmpty() && !"FUNCTION".equals(offsetTable.peek().type)) {
            offsetTable.pop();
        }
        if (!offsetTable.isEmpty() && "FUNCTION".equals(offsetTable.peek().type)) {
            offsetTable.pop();
        }

        localOffset = -44;
        scope = 1;
        numOfLocals = 0;
    }

    // Method to end class parsing
    public void endClassParse() {
        while (!offsetTable.isEmpty() && !"CLASS".equals(offsetTable.peek().type)) {
            offsetTable.pop();
        }
        if (!offsetTable.isEmpty() && "CLASS".equals(offsetTable.peek().type)) {
            offsetTable.pop();
        }
    }

    // Method to print the offset table
    public void printTable() {
        System.out.println("OFFSET TABLE:");
        for (OFFSET_TABLE_ENTRY entry : offsetTable) {
            System.out.println(entry);
        }
    }

    private static OFFSET_TABLE instance = null;

    // Constructor to defeat instantiation
    protected OFFSET_TABLE() {
        // Exists only to defeat instantiation.
    }

    // Method to get the singleton instance of OFFSET_TABLE
    public static OFFSET_TABLE getInstance() {
        if (instance == null) {
            instance = new OFFSET_TABLE();
        }
        return instance;
    }

    // Method to get the number of local variables
    public int getNumOfLocals() {
        System.out.println("num of locals: " + numOfLocals);
        return numOfLocals;
    }

}