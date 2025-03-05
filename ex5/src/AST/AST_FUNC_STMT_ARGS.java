package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import java.util.*;

public class AST_FUNC_STMT_ARGS extends AST_FUNC_STMT
{

    public String funcName;
    public int line;
    public AST_EXP_ARGUMENTS funcArgs;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNC_STMT_ARGS(String funcName, AST_EXP_ARGUMENTS funcArgs, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== funcStmt -> var DOT ID( %s ) LPAREN RPAREN SEMICOLON\n" , funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.funcName = funcName;
        this.funcArgs = funcArgs;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC STMT ARGS */
        /*********************************/
        System.out.println("AST NODE FUNC STMT ARGS");

        /******************************************/
        /* PRINT name */
        /******************************************/
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
            "FUNC STMT ARGS\nFunction name: " + funcName);

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (funcArgs != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, funcArgs.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // get the current class
        TYPE_CLASS currClass = SYMBOL_TABLE.getInstance().get_current_class();
        TYPE funcFindType = null;

        funcFindType = SYMBOL_TABLE.getInstance().findInClass(funcName);

		if (funcFindType == null){
			// we want to check in inheritance tree
			if (currClass != null && ((TYPE_CLASS)currClass).father != null){
				funcFindType = ((TYPE_CLASS)currClass).findFunctionInInheritanceTree(funcName);
				if (funcFindType == null){
					// only left to check if it is in global scope
					funcFindType = SYMBOL_TABLE.getInstance().find(funcName);
					if (funcFindType == null){
						// we didnt find it
						System.out.format(">> ERROR(%d) function %s has not been declared\n", this.line, funcName);
						printError(this.line);
					}
				}
			}
			else {
				funcFindType = SYMBOL_TABLE.getInstance().find(funcName);
				if (funcFindType == null){
					// we didnt find it
					System.out.format(">> ERROR(%d) function %s has not been declared\n", this.line, funcName);
					printError(this.line);
				}
			}
		}


        // function has been declared, we need to check if the decleration and the call have the same arguments and types
        TYPE_LIST args = null;
        if(funcArgs != null){
            args = funcArgs.SemantMe();  // semant the arguments of the function call
        }

        if(!(args.checkArgumentsList(((TYPE_FUNCTION)funcFindType).params))){
            System.out.format(">> ERROR(%d) mismatch in types of arguments in function call (\n", this.line, funcName);
            printError(this.line);
        }

        // return the return type of the function
        return ((TYPE_FUNCTION)funcFindType).returnType;
    }

    public TEMP IRme() {

        // recursively IRme the arguments
        List<TEMP> args = null;
        if (funcArgs != null) {
            args = funcArgs.IRme(new ArrayList<>());
        }

        // check if function is a print int function, if so, handle it correctly
        if(funcName.equals("PrintInt"))
        {
            IR.getInstance().Add_IRcommand(new IRcommand_PrintInt(args.get(0), IR.getInstance().currLine));
            return null;
        }

        // check if function is a print string function, if so, handle it correctly
        else if(funcName.equals("PrintString"))
        {
            IR.getInstance().Add_IRcommand(new IRcommand_PrintString(args.get(0), IR.getInstance().currLine));
            return null;
        }

        // else, treat it as a function with arguments
        else
        {
            IR.getInstance().Add_IRcommand(new IRcommand_Call_Function_Args_Void(funcName, args, IR.getInstance().currLine));
        }

        return null;
    }
}
