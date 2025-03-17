package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_FUNC_DEC_NO_ARGS extends AST_FUNC_DEC
{

    public AST_TYPE t;
    public String funcName;
    public AST_STMT_LIST body;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_DEC_NO_ARGS(AST_TYPE t, String funcName, AST_STMT_LIST body, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== funcDec -> type ID( %s ) LPAREN RPAREN LBRACE stmtList RBRACE\n", funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.t = t;
        this.funcName = funcName;
        this.body = body;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNCDEC NO ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC DEC NO ARGS");

        /******************************************/
        /* RECURSIVELY PRINT t, name and body ... */
        /******************************************/
        if (t != null) t.PrintMe();
        System.out.format("Function name( %s )\n", funcName);
        if (body != null) body.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNC DEC\nNO ARGS\nFunction name:" + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
        if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, body.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // get return type and create function type
        TYPE returnType = t.SemantMe();
        TYPE_FUNCTION currFunc = new TYPE_FUNCTION(returnType, funcName, null);

        // check if function name is already declared in scope or is a reserved word
        if (SYMBOL_TABLE.getInstance().findInScope(funcName) != null || isReservedWord(funcName))
        {
            System.out.format(">> ERROR(%d) the name %s already been used in scope or is a reserved word\n", this.line, funcName);
            printError(this.line);
        }

        // check for overloading
        TYPE_CLASS currCls = SYMBOL_TABLE.getInstance().get_current_class();
        if(currCls != null){
            if(!AST_FUNC_DEC_ARGS.checkIfOverloadingIsCorrect(currCls, currFunc)){
                System.out.format(">> ERROR(%d) Overloading is not allowed\n", line, funcName);
                printError(line);
            }
        }

        // enter the function as a class member of current class
        if(currCls != null){
            SYMBOL_TABLE.getInstance().currentClassFunctionMembers.insertAtEnd(new TYPE_CLASS_VAR_DEC(currFunc, funcName));
        }

        // begin scope of function in symbol table and set current function and inside function
        SYMBOL_TABLE.getInstance().beginScope();
        SYMBOL_TABLE.getInstance().enter(funcName, currFunc, false, false);
        SYMBOL_TABLE.getInstance().set_current_function(currFunc);
        SYMBOL_TABLE.getInstance().set_inside_function(true);
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelUp();

        // semant me of function body
        body.SemantMe();

        // end scope of function in symbol table and set inside function and current function
        SYMBOL_TABLE.getInstance().set_inside_function(false);
        SYMBOL_TABLE.getInstance().set_current_function(null);
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelDown();
        SYMBOL_TABLE.getInstance().endScope();

        // enter function into symbol table (as TYPE_FUNCTION)
        SYMBOL_TABLE.getInstance().enter(funcName, currFunc, false, false);

        // return the function as a class member
        return new TYPE_CLASS_FUNC_DEC(currFunc, funcName);
    }

    public TEMP IRme() {

        // Enter the scope function to the OFFSET_TABLE
        OFFSET_TABLE.getInstance().enterFunction(this.funcName, null);

        // initialize the labels
        String label_func_end = null;
        String label_func_start = null;

        // if its not a method
        if(!SYMBOL_TABLE.getInstance().get_inside_class()){
            label_func_end = IRcommand.getFreshLabel(this.funcName + "_end");
            label_func_start = IRcommand.getFreshLabel(this.funcName + "_start");
        }

        // if its a method
        else{
            label_func_start = IRcommand.getFreshLabel(" class: " + SYMBOL_TABLE.getInstance().get_current_class().name + "::" + this.funcName + "()_start");
            label_func_end = IRcommand.getFreshLabel(SYMBOL_TABLE.getInstance().get_current_class().name + "_" + this.funcName + "_end");
        }

        // insert the method to the CLASSES_MAP
        if(SYMBOL_TABLE.getInstance().get_inside_class()){
            String clssName = SYMBOL_TABLE.getInstance().get_current_class().name;
            CLASSES_MAP.getInstance().insertMethod(clssName, this.funcName);
        }

        // Add function entry label as IR command
        IR.getInstance().Add_IRcommand(new IRcommand_DecFunction(label_func_start, 0, this.funcName, IR.getInstance().currLine));

        // set the current function to the function we are in and set inside function to true
        SYMBOL_TABLE.getInstance().set_inside_function(true);
        SYMBOL_TABLE.getInstance().set_current_function(((TYPE_FUNCTION)SYMBOL_TABLE.getInstance().find(this.funcName)));

        // this is in a case that the function is a class method so we couldnt find it in the symbol table
        if(((TYPE_FUNCTION)SYMBOL_TABLE.getInstance().find(this.funcName)) == null){
            TYPE_CLASS currCls = SYMBOL_TABLE.getInstance().get_current_class();
            TYPE_FUNCTION currFunc = currCls.findFunction(this.funcName);
            SYMBOL_TABLE.getInstance().set_current_function(currFunc);
        }

        // Generate IR code for the function body
        if (this.body != null) {
            body.IRme();
        }

        // Update the number of locals in the function decleration IR command
        ((IRcommand_DecFunction)IR.getInstance().findLastDecFunctionCommand()).updateNumOfLocals(OFFSET_TABLE.getInstance().getNumOfLocals());

        // Add function exit label IR command, this will also invoke the epilogue in the MIPS code
        IR.getInstance().Add_IRcommand(new IRcommand_EndFunction(label_func_end, funcName, IR.getInstance().currLine));

        // end the function scope in the symbol table and set inside function to false
        SYMBOL_TABLE.getInstance().set_current_function(null);
        SYMBOL_TABLE.getInstance().set_inside_function(false);

        // end the function scope in the offset table
        OFFSET_TABLE.getInstance().endFunctionParse();

        return null;
    }
}
