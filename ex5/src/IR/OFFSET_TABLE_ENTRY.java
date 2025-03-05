package IR;
import java.util.*;
public class OFFSET_TABLE_ENTRY {
    public String name;       // Variable name
    public int offset;        // Offset in stack frame
    public String type;       // Variable type
    public boolean isClassInstance; // If this is a class instance, this will hold the class name
    public String className;  // If this is a class instance, this will hold the class name
    public int scopeLevel;    // Scope level
    public String funcName;   // The function this variable belongs to
    public List<String> args = new ArrayList<>(); // The arguments of the function

    public OFFSET_TABLE_ENTRY(String name, int offset, String type, boolean isClassInstance, String className, int scopeLevel, String funcName, List<String> args) {
        this.name = name;
        this.offset = offset;
        this.type = type;
        this.scopeLevel = scopeLevel;
        this.funcName = funcName;
        this.args = args;
        this.isClassInstance = isClassInstance;
        this.className = className;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Offset: %d, Type: %s, Scope: %d, FuncName: %s, Args: %s, isClassInstance: %b, ClassName: %s", name, offset, type, scopeLevel, funcName, args, isClassInstance, className);
    }
}
