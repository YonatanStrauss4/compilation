package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import java.util.*;


public class AST_CLASS_DEC_EXTENDS extends AST_CLASS_DEC
{
    public String className;
    public String superClassName;
    public AST_CFIELD_REC classBody ;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    
    public AST_CLASS_DEC_EXTENDS(String className, String superClassName, AST_CFIELD_REC classBody, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== classDec -> CLASS ID( %s ) EXTENDS ID( %s ) LBRACE cFieldRec RBRACE\n", className, superClassName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.className = className;
        this.superClassName = superClassName;
        this.classBody = classBody;
        this.line = line;
    }


    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = CLASS DEC EXTENDS */
        /*********************************/
        System.out.println("AST NODE CLASS DECLARATION EXTENDS");

        /******************************************/
        /* PRINT class name, superclass, and body ... */
        /******************************************/
        System.out.format("CLASS NAME( %s )\n", className);
        System.out.format("SUPERCLASS( %s )\n", superClassName);
        if (classBody != null) classBody.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("CLASS DEC %s\nEXTENDS\n...->%s", className, superClassName));

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (classBody != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, classBody.SerialNumber);
    }

    public TYPE SemantMe()
    {

        //check if class has been defined in global scope
        if (SYMBOL_TABLE.getInstance().getCurrentScopeLevel() != 0)
        {
            System.out.format(">> ERROR(%d) classes can only be defined in global scope\n", this.line, className);
            printError(this.line);
        }
        
        //check if super class has been defined, if extends is legal
        TYPE father = SYMBOL_TABLE.getInstance().find(superClassName);
        if(father == null || !(father instanceof TYPE_CLASS) || !superClassName.equals(father.name))
        {
            System.out.format(">> ERROR(%d) class %s extends a class %s that has not been defined\n", this.line, className, superClassName);
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
        SYMBOL_TABLE.getInstance().set_current_class(new TYPE_CLASS((TYPE_CLASS)father, className, new TYPE_LIST(null, null)));
        SYMBOL_TABLE.getInstance().set_inside_class(true);
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelUp();


        // build class with semant me of class body
        TYPE_CLASS class_build = new TYPE_CLASS((TYPE_CLASS)father, className, classBody.SemantMe());

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
        // find the class in the symbol table
        TYPE_CLASS cls = (TYPE_CLASS)SYMBOL_TABLE.getInstance().find(className);
        TYPE_CLASS currCls = null;

        // make another class for inheritance traversal
        if(cls != null){
            currCls = cls;
        }

        // initialize list for the fields of the class
        List<String> fieldNames = new ArrayList<>();

        // insert class to classes map  with its father
        CLASSES_MAP.getInstance().insertClass(cls.name, cls.father.name);

        // begin class parse for the offset table
        OFFSET_TABLE.getInstance().enterClass(cls.name);

        // we look for all the fields of the class and its ancestors
        while (cls != null) {
            for (TYPE_LIST t = cls.data_members; t != null; t = t.tail) {
                if (t.head instanceof TYPE_CLASS_VAR_DEC) {
                    TYPE_CLASS_VAR_DEC varDec = (TYPE_CLASS_VAR_DEC) t.head;
                    fieldNames.add(varDec.name);
                }
            }
            cls = cls.father;
        }

        // add the class declaration as an IR command with the list of fields names
        IR.getInstance().Add_IRcommand(new IRcommand_Class_Dec(className, fieldNames, IR.getInstance().currLine));

        // set inside class and current class
        SYMBOL_TABLE.getInstance().set_inside_class(true);
        SYMBOL_TABLE.getInstance().set_current_class(currCls);

        // recursively call the IRme of the class body
        if (classBody != null) classBody.IRme();

        // end the class declaration in the IR, this will add the virtual table to the .data section in the MIPS file
        IR.getInstance().Add_IRcommand(new IRcommand_Class_End_Dec(SYMBOL_TABLE.getInstance().get_current_class().name, IR.getInstance().currLine));

        // end the class parse in the offset table
        OFFSET_TABLE.getInstance().endClassParse();

        // reset the current class and inside class
        SYMBOL_TABLE.getInstance().set_current_class(null);
        SYMBOL_TABLE.getInstance().set_inside_class(false);

        
        return null;
    }

}
