package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import java.util.List;

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

        

        TYPE funcFind = SYMBOL_TABLE.getInstance().findInScope(funcName);
        
        SYMBOL_TABLE.getInstance().beginScope();

        TYPE_FUNCTION currFunc = new TYPE_FUNCTION(returnType, funcName, args.SemantMe());
        
        // check if function name is already declared in scope or is a reserved word       
        if (funcFind != null || isReservedWord(funcName)){
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

    public TEMP IRme() {

        // Allocate fresh labels for function entry and exit
        String label_func_end = IRcommand.getFreshLabel(this.funcName + "_end");

        // Initialize the function start label with the function name
        StringBuilder labelBuilder = new StringBuilder(this.funcName + "(");

        // Get the list of argument names
        List<String> argNames = this.args.getArgumentsAsList();
        for (int i = 0; i < argNames.size(); i++) {
            labelBuilder.append(argNames.get(i));
            if (i < argNames.size() - 1) {
            labelBuilder.append(", ");
            }
        }
        labelBuilder.append(")_start");

        // Add the function to the class in the CLASSES_MAP, if its a method
        if(SYMBOL_TABLE.getInstance().get_inside_class()){
            String clssName = SYMBOL_TABLE.getInstance().get_current_class().name;

            CLASSES_MAP.getInstance().insertMethod(clssName, this.funcName);
        }

        // Enter the scope function to the OFFSET_TABLE
        OFFSET_TABLE.getInstance().enterFunction(this.funcName, argNames);

        // build the label for the IR command
        String label_func_start = IRcommand.getFreshLabel(labelBuilder.toString());

        // Add function entry label IR command
        if(SYMBOL_TABLE.getInstance().get_inside_class()){
            String clsName = SYMBOL_TABLE.getInstance().get_current_class().name;
            IR.getInstance().Add_IRcommand(new IRcommand_DecFunction(label_func_start, 0, this.funcName, true, clsName, IR.getInstance().currLine));
        }
        else{
            IR.getInstance().Add_IRcommand(new IRcommand_DecFunction(label_func_start, 0, this.funcName, false, null, IR.getInstance().currLine));
        }

        // set the current function in the symbol table and set inside function to true
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
        if(SYMBOL_TABLE.getInstance().get_inside_class()){
            String clsName = SYMBOL_TABLE.getInstance().get_current_class().name;
            IR.getInstance().Add_IRcommand(new IRcommand_EndFunction(label_func_end, funcName, true, clsName, IR.getInstance().currLine));
        }
        else{
            IR.getInstance().Add_IRcommand(new IRcommand_EndFunction(label_func_end, funcName, false, null, IR.getInstance().currLine));
        }
    
        // end the function scope in the symbol table and set inside function to false
        SYMBOL_TABLE.getInstance().set_current_function(null);
        SYMBOL_TABLE.getInstance().set_inside_function(false);

        // end the function scope in the offset table
        OFFSET_TABLE.getInstance().endFunctionParse();

        return null;
    }
    
}

