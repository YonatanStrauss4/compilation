package AST;
import TYPES.*;
import SYMBOL_TABLE.*;  
import TEMP.*;
import IR.*;
import java.util.*;

public class AST_FUNC_STMT_VAR_DOT_ARGS extends AST_FUNC_STMT
{

    public AST_VAR var;
    public String funcName;
    public AST_EXP_ARGUMENTS funcArgs;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_STMT_VAR_DOT_ARGS(AST_VAR var, String funcName, AST_EXP_ARGUMENTS funcArgs, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== funcStmt-> var DOT ID( %s ) LPAREN expArguments RPAREN SEMICOLON\n" + funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.var = var;
        this.funcName = funcName;
        this.funcArgs = funcArgs;
        this.line = line;
    }


    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC STMT VAR DOT ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC STMT VAR DOT ARGS");

        /******************************************/
        /* RECURSIVELY PRINT v and name */
        /******************************************/
        if (var != null) var.PrintMe();
        System.out.println("Function name: " + funcName);

        /******************************************/
        /* RECURSIVELY PRINT eA */
        /******************************************/
        if (funcArgs != null) funcArgs.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "FUNC STMT VAR DOT ARGS\nFunction name: " + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
        if (funcArgs != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, funcArgs.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the variable to get his type
        TYPE varType = var.SemantMe();

        // check if the variable is TYPE_CLASS
		if(!(varType instanceof TYPE_CLASS)){
			System.out.format(">> ERROR(%d) variable is not an instance of a class\n", this.line);
				printError(this.line);
		}
        
        varType = (TYPE_CLASS)varType;
        if (SYMBOL_TABLE.getInstance().get_inside_class()){
            TYPE funcSearch = SYMBOL_TABLE.getInstance().currentClassFunctionMembers.search(funcName);
            if (funcSearch == null && ((TYPE_CLASS)varType).father != null){
                funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
                if (funcSearch == null) {
                    System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends\n", this.line, funcName);
                    printError(this.line);
                }
            }
        }
       
        TYPE funcSearch = ((TYPE_CLASS)varType).findFunctionInInheritanceTree(funcName);
        if (funcSearch == null){
            System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends\n", this.line, funcName);
            printError(this.line);
        }
        // function has been declared, we need to check if the decleration and the call have the same arguments and types
        TYPE_LIST args = null;
        if(funcArgs != null){
            args = funcArgs.SemantMe();  // semant the arguments of the function call
        }
        
        if(!(args.checkArgumentsList(((TYPE_FUNCTION)funcSearch).params))){
            System.out.format(">> ERROR(%d) mismatch in types of arguments in function call (\n", this.line, funcName);
            printError(this.line);
        }

        // return the return type of the function
        return ((TYPE_FUNCTION)funcSearch).returnType;
    }

    public TEMP IRme()
    {
        TEMP t = var.IRme();
        List<TEMP> args = funcArgs.IRme(new ArrayList<>());
        IR.getInstance().Add_IRcommand(new IRcommand_Virtual_Call_Args_Void(t, args, funcName, IR.getInstance().currLine));
        return null;
    }
}
