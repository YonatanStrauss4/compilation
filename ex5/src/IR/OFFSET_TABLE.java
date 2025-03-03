package IR;
import java.util.*;

public class OFFSET_TABLE {
    public Stack<OFFSET_TABLE_ENTRY> offsetTable = new Stack<>();
    public int offset = -44;
    public int numOfLocals = 0;
    int scope = 1;


    public void enterFunction(String funcName, List<String> args) {
        offsetTable.push(new OFFSET_TABLE_ENTRY(funcName, 0, 1, funcName, args));
        printTable();

    }

    public void pushInnerScopeStart() {
        offsetTable.push(new OFFSET_TABLE_ENTRY("inner_scope_start", 0, scope, null, null));
        scope++;
        printTable();

    }

    public void pushVariable(String varName) {
        offsetTable.push(new OFFSET_TABLE_ENTRY(varName, offset, scope, null, null));
        offset -= 4;
        numOfLocals++;
        printTable();

    }

    // public int findReturnVariableOffset(String varName) {
    //     Stack<OFFSET_TABLE_ENTRY> stack = offsetTable.get(funcName);
    //     if (stack != null) {
    //         for (OFFSET_TABLE_ENTRY entry : stack) {
    //             if (entry.name.equals(varName) && entry.scopeLevel == 1) {
    //                 return entry.offset;
    //             }
    //         }
    //     }
    //     return -1;
    // }

    public int findVariableOffset(String varName) {
        ListIterator<OFFSET_TABLE_ENTRY> iterator = offsetTable.listIterator(offsetTable.size());
        while (iterator.hasPrevious()) {
            OFFSET_TABLE_ENTRY entry = iterator.previous();
            if (entry.name.equals(varName)) {
                return entry.offset;
            }
        }
        return -1;
    }

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
        offset += 4 * variableCount;
        scope--;
        printTable();

    }

    public void endFunctionParse(String funcName) {
       while (!offsetTable.isEmpty() && !offsetTable.peek().name.equals(funcName)) {
            offsetTable.pop();
        }
        if (!offsetTable.isEmpty()) {
            offsetTable.pop();
        }
        offset = -44;
        scope = 1;
        numOfLocals = 0;
        printTable();
    }

    public void printTable() {
        System.out.println("OFFSET TABLE:");
        for (OFFSET_TABLE_ENTRY entry : offsetTable) {
            System.out.println(entry);
        }
    }

    // public OFFSET_TABLE_ENTRY getFunction(String funcName) {
    //     if(funcName == null){
    //         return null;
    //     }
    //     Stack<OFFSET_TABLE_ENTRY> stack = offsetTable.get(funcName);
    //     if (stack != null && !stack.isEmpty()) {
    //         OFFSET_TABLE_ENTRY entry = stack.firstElement();
    //         if (entry.name.equals(funcName) && entry.scopeLevel == 0) {
    //             return entry;
    //         }
    //     }
    //     return null;
    // }

    private static OFFSET_TABLE instance = null;

    protected OFFSET_TABLE() {
        // Exists only to defeat instantiation.
    }

    public static OFFSET_TABLE getInstance() {
        if (instance == null) {
            instance = new OFFSET_TABLE();
        }
        return instance;
    }

    public int getNumOfLocals() {
        return numOfLocals;
    }

}