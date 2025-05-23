package AST;
import TYPES.*;
import IR.*;
import TEMP.*;

public class AST_EXP_NIL extends AST_EXP
{
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NIL()
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> NIL\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
	}

	public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = EXP NIL */
        /*********************************/
        System.out.println("AST NODE EXP NIL");

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "EXP\nNIL");
	}

	public TYPE SemantMe()
	{
		// return TYPE_NIL
		varType = TYPE_NIL.getInstance().name;
		return TYPE_NIL.getInstance();
	}

	public TEMP IRme()
	{
		// get fresh TEMP
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		// add IR command for nil
		IR.getInstance().Add_IRcommand(new IRcommand_Nil(t, IR.getInstance().currLine));
		return t;
	}
}
