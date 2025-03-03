package AST;
import TYPES.*;
import TEMP.*;

public class AST_STMT_ASSIGN_NEW extends AST_STMT
{

    public AST_VAR var;
    public AST_NEW_EXP newExp;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEW_EXP newExp, int line) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== stmt -> var ASSIGN newExp SEMICOLON\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.var = var;
        this.newExp = newExp;
        this.line = line;
    }

    /***************************************************/
    /* The printing message for an assignment statement with new expression AST node */
    /***************************************************/
    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = STMT ASSIGN NEW */
        /*********************************/
        System.out.println("AST NODE STMT ASSIGN NEW");

        /******************************************/
        /* RECURSIVELY PRINT v and new_exp ... */
        /******************************************/
        if (var != null) var.PrintMe();
        if (newExp != null) newExp.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nASSIGN NEW");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
        if (newExp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, newExp.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the variavble and the NEW expression
        TYPE varType = var.SemantMe();
        TYPE newType = newExp.SemantMe();

        TYPE elemType = null;

        if (varType instanceof TYPE_ARRAY){
            elemType = ((TYPE_ARRAY)varType).type;
        }
        else {
            elemType = varType;
        }

        // check if variable types are equal
        if(!elemType.equals(newType)){
            // check if the type of the variable is TYPE_CLASS
            if(!(varType instanceof TYPE_CLASS)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
            }

            // variable type is a class but not equal to newExp type, so we check for inheritance
            if(!((TYPE_CLASS)newType).checkIfInherit((TYPE_CLASS)varType)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
                }
            }

        // check if we assign a non array type to an array
        if(varType instanceof TYPE_ARRAY && !(newExp instanceof AST_NEW_TYPE_EXP_IN_BRACKS)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign non array to array\n",line);
            printError(line);
        }

        // check if we assign assign array type to a non array type
        if(!(varType instanceof TYPE_ARRAY) && !(newExp instanceof AST_NEW_TYPE)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign array to non array\n",line);
            printError(line);
        }

        return null;
    }
}
