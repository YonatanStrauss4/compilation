package AST;
import SYMBOL_TABLE.*;
import TYPES.*;
import TEMP.*;
import IR.*;


public class AST_FUNCTION_NO_ARGS extends AST_FUNCTION
{

    public String funcName;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_FUNCTION_NO_ARGS(String funcName, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== function -> ID( %s ) LPAREN RPAREN\n", funcName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.funcName = funcName;
        this.line = line;
    }

    public void PrintMe() 
    {
        /*********************************/
        /* AST NODE TYPE = FUNCTION NO ARGS */
        /*********************************/
        System.out.println("AST NODE FUNCTION NO ARGS");

        /******************************************/
        /* PRINT name */
        /******************************************/
        System.out.println("Function name: " + funcName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "FUNCTION NO ARGS\nFunction name: " + funcName);
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


        // return the return type of the function
        return ((TYPE_FUNCTION)funcFindType).returnType;
    }

    public TEMP IRme()
    {

        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRcommand(new IRcommand_Call_Function_No_Args_Not_Void(dst, funcName, IR.getInstance().currLine));
        if(SYMBOL_TABLE.getInstance().get_current_class() != null) {
            // if we are in a class, we need to add the this pointer to the function call
            String lowestClassName = SYMBOL_TABLE.getInstance().findLowestClassWithMethod(SYMBOL_TABLE.getInstance().get_current_class(), funcName);
            int offset = CLASSES_MAP.getInstance().getMethodOffset(lowestClassName, funcName); 
            IR.getInstance().Add_IRcommand(new IRcommand_Joker_Call_Function_No_Args_Not_Void(dst, offset, lowestClassName, IR.getInstance().currLine));
        }
        else{
            // call the function
        IR.getInstance().Add_IRcommand(new IRcommand_Call_Function_No_Args_Not_Void(dst, funcName, IR.getInstance().currLine));
        }
        return dst;
    }   
}
 