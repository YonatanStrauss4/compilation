package AST;
import TYPES.*;

public class AST_DEC_FUNC_DEC extends AST_DEC
{
    
    public AST_FUNC_DEC func_dec;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_DEC_FUNC_DEC(AST_FUNC_DEC func_dec)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== dec -> funcDec\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.func_dec = func_dec;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = FUNC DEC*/
        /*********************************/
        System.out.println("AST NODE FUNC DEC");

        /******************************************/
        /* RECURSIVELY PRINT func_dec ... */
        /******************************************/
        if (func_dec != null) func_dec.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "DEC\nFUNC DEC");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (func_dec != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, func_dec.SerialNumber);
    }

	public TYPE SemantMe()
	{
        // semant the function decleration
		if (func_dec != null) 
        {
			return func_dec.SemantMe();
		}
		return null;
	}
}
