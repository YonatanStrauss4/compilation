package IR;
import java.util.*;
class OFFSET_TABLE_ENTRY {
    String name;       // Variable name
    int offset;        // Offset in stack frame
    int scopeLevel;    // Scope level
    String funcName;   // The function this variable belongs to
    List<String> args = new ArrayList<>(); // The arguments of the function

    public OFFSET_TABLE_ENTRY(String name, int offset, int scopeLevel, String funcName, List<String> args) {
        this.name = name;
        this.offset = offset;
        this.scopeLevel = scopeLevel;
        this.funcName = funcName;
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, Offset: %d, Scope Level: %d, Function: %s, Args: %s", name, offset, scopeLevel, funcName, args);
    }
}
