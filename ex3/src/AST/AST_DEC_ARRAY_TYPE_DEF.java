package AST;
import TYPES.*;

public class AST_DEC_ARRAY_TYPE_DEF extends AST_DEC
{

    public AST_ARRAY_TYPE_DEF array_type_def;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_DEC_ARRAY_TYPE_DEF(AST_ARRAY_TYPE_DEF array_type_def)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== dec -> arrayTypeDef\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.array_type_def = array_type_def;

    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = ARRAY TYPEDEF DEC */
        /*********************************/
        System.out.println("AST NODE ARRAY TYPE DEF DEC");

        /******************************************/
        /* RECURSIVELY PRINT array_type_def ... */
        /******************************************/
        if (array_type_def != null) array_type_def.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "DEC\nARRAY TYPE DEF");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (array_type_def != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, array_type_def.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the array decleration
        if(array_type_def != null)
        {
            return array_type_def.SemantMe();
        }
        return null;
    }

}
