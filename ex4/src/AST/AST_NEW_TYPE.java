package AST;
import TYPES.*;
import TEMP.*;

public class AST_NEW_TYPE extends AST_NEW_EXP
{

    public AST_TYPE type;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_NEW_TYPE(AST_TYPE type)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== newExp-> NEW type\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.type = type;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = NEW TYPE */
        /*********************************/
        System.out.println("AST NODE NEW TYPE");

        /******************************************/
        /* RECURSIVELY PRINT t ... */
        /******************************************/
        if (type != null) type.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "NEW TYPE\n");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (type != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, type.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the type
        return type.SemantMe();
    }
}
