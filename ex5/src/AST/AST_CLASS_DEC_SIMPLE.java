package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import java.util.*;

public class AST_CLASS_DEC_SIMPLE extends AST_CLASS_DEC
{
    public String className;
    public AST_CFIELD_REC classBody;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_CLASS_DEC_SIMPLE(String className, AST_CFIELD_REC classBody, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== classDec -> CLASS ID( %s ) LBRACE cFieldRec RBRACE\n", className);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.className = className;
        this.classBody = classBody;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = CLASS DEC SIMPLE */
        /*********************************/
        System.out.println("AST NODE CLASS DEC SIMPLE");

        /******************************************/
        /* PRINT class name and cFR ... */
        /******************************************/
        System.out.format("CLASS NAME( %s )\n", className);
        if (classBody != null) classBody.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("CLASS DEC\nSIMPLE\n...->%s", className));

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (classBody != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, classBody.SerialNumber);
    }

    public TYPE SemantMe()
    {

        // check if class has been defined in global scope
        if (SYMBOL_TABLE.getInstance().getCurrentScopeLevel() != 0)
        {
            System.out.format(">> ERROR(%d) classes can only be defined in global scope\n", this.line);
            printError(this.line);
        }

        // check if class name is already used or is a reserved word
        if (SYMBOL_TABLE.getInstance().find(className) != null || isReservedWord(className))
        {
            System.out.format(">> ERROR(%d) the name %s already been used or is a reserved word\n", this.line, className);
            printError(this.line);
        }

        // begin scope of class in symbol table, adjust inside class and current class and set current class
        SYMBOL_TABLE.getInstance().beginScope();
        SYMBOL_TABLE.getInstance().set_current_class(new TYPE_CLASS(null, className, new TYPE_LIST(null, null)));
        SYMBOL_TABLE.getInstance().set_inside_class(true);
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelUp();

        // build class with semant me of class body
        TYPE_CLASS class_build = new TYPE_CLASS(null, className, classBody.SemantMe());



        SYMBOL_TABLE.getInstance().currentClassFunctionMembers.clear();
        SYMBOL_TABLE.getInstance().currentClassVariableMembers.clear();
        SYMBOL_TABLE.getInstance().set_inside_class(false);
        SYMBOL_TABLE.getInstance().set_current_class(null);
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelDown();
        SYMBOL_TABLE.getInstance().endScope();
        SYMBOL_TABLE.getInstance().enter(className, class_build, true, false);

        TYPE cls = SYMBOL_TABLE.getInstance().find(className);
        for(TYPE_LIST t = ((TYPE_CLASS)cls).data_members; t != null; t = t.tail){
            if(t.head instanceof TYPE_CLASS_FUNC_DEC){
                continue;
            }
             if (((TYPE_CLASS_VAR_DEC)t.head).type.name.equals(className)) {
                // Update the variable's type to classBuild
                ((TYPE_CLASS_VAR_DEC)t.head).type = class_build;
            }
        }
        return null;
    }
    
    public TEMP IRme()
    {
        TYPE_CLASS cls = (TYPE_CLASS)SYMBOL_TABLE.getInstance().findClassInSymbolTable(className);
        List<String> fieldNames = new ArrayList<>();

        if (cls != null) {
            for (TYPE_LIST t = cls.data_members; t != null; t = t.tail) {
                if (t.head instanceof TYPE_CLASS_VAR_DEC) {
                    TYPE_CLASS_VAR_DEC varDec = (TYPE_CLASS_VAR_DEC) t.head;
                    fieldNames.add(varDec.name);
                }
            }
            IR.getInstance().Add_IRcommand(new IRcommand_Class_Dec(className, fieldNames, IR.getInstance().currLine));
        }
        SYMBOL_TABLE.getInstance().set_inside_class(true);
        SYMBOL_TABLE.getInstance().set_current_class(cls);
        if (classBody != null) classBody.IRme();
        SYMBOL_TABLE.getInstance().set_current_class(null);
        SYMBOL_TABLE.getInstance().set_inside_class(false);
        
        return null;
    }
}
