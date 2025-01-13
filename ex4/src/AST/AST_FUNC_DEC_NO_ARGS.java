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
        // [1] Allocate fresh labels for function entry and exit
        String label_func_start = IRcommand.getFreshLabel(this.funcName + "_start");
        String label_func_end = IRcommand.getFreshLabel(this.funcName + "_end");

        // [2] Add function entry label
        IR.getInstance().Add_IRcommand(new IRcommand_Label(label_func_start));

        // [3] Generate IR code for the function body
        if (this.body != null) {
            body.IRme();
        }

        // [4] Add function exit label
        IR.getInstance().Add_IRcommand(new IRcommand_Label(label_func_end));

        return null;
    }
}
