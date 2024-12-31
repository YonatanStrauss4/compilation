package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

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
        if (SYMBOL_TABLE.getInstance().get_scope_level() != 0)
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


        // build class with semant me of class body
        TYPE_CLASS class_build = new TYPE_CLASS(null, className, classBody.SemantMe());

        // TYPE_LIST curr = ((TYPE_CLASS)class_build).data_members;
        // while(curr != null){
        //     System.out.println(curr.head.name);
        //     curr = curr.tail;
        // }

        // end scope, adjust inside class and current class and enter class into symbol table
        SYMBOL_TABLE.getInstance().currentClassFunctionMembers.clear();
        SYMBOL_TABLE.getInstance().currentClassVariableMembers.clear();
        SYMBOL_TABLE.getInstance().set_inside_class(false);
        SYMBOL_TABLE.getInstance().set_current_class(null);
        SYMBOL_TABLE.getInstance().endScope();
        SYMBOL_TABLE.getInstance().enter(className, class_build);

        // TYPE cls = SYMBOL_TABLE.getInstance().find(className);
        // TYPE_LIST curr = ((TYPE_CLASS)cls).data_members;
        // while(curr != null){
        //     System.out.println(curr.head.name);
        //     curr = curr.tail;
        // }

        return null;
    }
}
