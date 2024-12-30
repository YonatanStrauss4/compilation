package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_FUNC_DEC_ARGS extends AST_FUNC_DEC
{

    public AST_TYPE t;
    public String funcName;
    public AST_FUNC_DEC_ARGS_LIST args;
    public AST_STMT_LIST body;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_DEC_ARGS(AST_TYPE t, String funcName, AST_FUNC_DEC_ARGS_LIST args, AST_STMT_LIST body, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== funcDec -> type ID( %s ) LPAREN funcArgs RPAREN LBRACE stmtList RBRACE\n", funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.t = t;
        this.funcName = funcName;
        this.args = args;
        this.body = body;
        this.line = line;   
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC DEC ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC DEC ARGS");

        /******************************************/
        /* RECURSIVELY PRINT t, args, and body ... */
        /******************************************/
        if (t != null) t.PrintMe();
        System.out.format("Function name( %s )\n", funcName);
        if (args != null) args.PrintMe();
        if (body != null) body.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNC DEC\nARGS\nFunction name:" + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
        if (args != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, args.SerialNumber);
        if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, body.SerialNumber);
    }

    public TYPE SemantMe()
    {

        // get return type and create function type 
        TYPE returnType = t.SemantMe();
        TYPE_FUNCTION currFunc = new TYPE_FUNCTION(returnType, funcName, args.SemantMe());

        // check if function name is already declared in scope or is a reserved word       
        if (SYMBOL_TABLE.getInstance().findInScope(funcName) != null || isReservedWord(funcName)){
                System.out.format(">> ERROR(%d) the name %s already been used in scope or is a reserved word\n", this.line, funcName);
                printError(this.line);
        }


        // check for overloading
        TYPE_CLASS currCls = SYMBOL_TABLE.getInstance().get_current_class();
        if(currCls != null){
            if(!checkIfOverloadingIsCorrect(currCls, currFunc)){
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
        SYMBOL_TABLE.getInstance().enter(funcName, currFunc);
        SYMBOL_TABLE.getInstance().set_current_function(currFunc);
        SYMBOL_TABLE.getInstance().set_inside_function(true);

        // semant me of function body
        body.SemantMe();

        // end scope of function in symbol table and set inside function and current function
        SYMBOL_TABLE.getInstance().set_inside_function(false);
        SYMBOL_TABLE.getInstance().set_current_function(null);
        SYMBOL_TABLE.getInstance().endScope();

        // enter function into symbol table (as TYPE_FUNCTION)
        SYMBOL_TABLE.getInstance().enter(funcName, currFunc);

        // return the function as a class member
        return new TYPE_CLASS_VAR_DEC(currFunc, funcName);
    }


    // function to check if overloading is correct
    public static boolean checkIfOverloadingIsCorrect(TYPE_CLASS curr_cls, TYPE_FUNCTION curr_func) {
        TYPE_CLASS father_cls = curr_cls.father;
        while (father_cls != null){
            TYPE_FUNCTION func_in_father = father_cls.findFunction(curr_func.name);
            TYPE_CLASS_VAR_DEC var_in_father = father_cls.findVariable(curr_func.name);
            if (var_in_father != null){
                return false;
            }
            if (func_in_father != null){
                if (!func_in_father.isSameSignature(curr_func, func_in_father)){
                    return false;
                }
            }
            father_cls = father_cls.father;
        }
        return true;
    }    

    
}

