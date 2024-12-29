package AST;
import TYPES.*;

public class AST_TYPE_STRING extends AST_TYPE
{
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_TYPE_STRING()
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== type -> TYPE_STRING\n");

    }


    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = TYPE STRING */
        /*********************************/
        System.out.println("AST NODE TYPE STRING");

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "TYPE\nSTRING");

    }

    public TYPE SemantMe()
    {
        // return TYPE_STRING
        return TYPE_STRING.getInstance();
    }
}
