package AST;
import TYPES.*;
import TEMP.*;

public class AST_TYPE_VOID extends AST_TYPE
{
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_TYPE_VOID()
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== type -> TYPE_VOID\n");

    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = TYPE VOID */
        /*********************************/
        System.out.println("AST NODE TYPE VOID");

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "TYPE\nVOID");
    }

    public TYPE SemantMe()
    {
        // return TYPE_VOID
        return TYPE_VOID.getInstance();
    }
}
