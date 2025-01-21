package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_RETURN_EXP extends AST_STMT
{

    public AST_EXP exp;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_STMT_RETURN_EXP(AST_EXP exp, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== stmt -> RETURN exp SEMICOLON\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.exp = exp;
        this.line = line;
    }

    /***************************************************/
    /* The printing message for a return statement with expression AST node */
    /***************************************************/
    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = STMT RETURN EXP */
        /*********************************/
        System.out.println("AST NODE STMT RETURN EXP");

        /******************************************/
        /* RECURSIVELY PRINT exp ... */
        /******************************************/
        if (exp != null) exp.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nRETURN EXP");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // check if return statment is inside a function
        if(!(SYMBOL_TABLE.getInstance().get_inside_function()))
        {
            System.out.format(">> ERROR(%d) return statement outside of function\n",line);
            printError(line);
        }


        // check if current function need to return nothing, then it is not possible that the return will have an expression
        if (SYMBOL_TABLE.getInstance().get_current_function().returnType instanceof TYPE_VOID)
        {
            System.out.format(">> ERROR(%d) return statement type mismatch\n",line);
            printError(line);
        }

        // check if the return type is ok
        TYPE returnExp = exp.SemantMe();
        TYPE returnExpCurrFunc = SYMBOL_TABLE.getInstance().get_current_function().returnType;

        // check if the return type of the function and the return type itself is both TYPE_CLASS
        if(returnExp instanceof TYPE_CLASS && returnExpCurrFunc instanceof TYPE_CLASS){
            // check if they are equal or if theres inherit relationship between them
            if (!returnExp.equals(returnExpCurrFunc) && !((TYPE_CLASS)returnExpCurrFunc).checkIfInherit(((TYPE_CLASS)returnExp))) {
                System.out.format(">> ERROR(%d) return statement type mismatch\n",line);
                printError(line);
            }
        }
        
        else{
            // check if it satisfies the conditions of nil
            if (!(returnExp.equals(returnExpCurrFunc))){
                if(!((returnExpCurrFunc instanceof TYPE_ARRAY || returnExpCurrFunc instanceof TYPE_CLASS || returnExpCurrFunc instanceof TYPE_NIL) && returnExp instanceof TYPE_NIL)){
                    System.out.format(">> ERROR(%d) return statement type mismatch\n",line);
                    printError(line);
                }
            }
        }

        // Return the type of the return expression
        return returnExp;
    }

    public TEMP IRme() {
        // IRme the return expression
        TEMP t = exp.IRme();
        // add the return command to the IR
        IR.getInstance().Add_IRcommand(new IRcommand_Return(t,IR.getInstance().currLine));
        return null;
    }
    
}

