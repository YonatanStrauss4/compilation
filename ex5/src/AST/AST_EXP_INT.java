package AST;
import TYPES.*;
import TEMP.*;
import IR.*;


public class AST_EXP_INT extends AST_EXP
{

	public int i;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_INT(int i)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> INT( %d )\n", i);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.i = i;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE INT( %d )\n",i);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("INT(%d)",i));
	}

	public TYPE SemantMe()
	{
		// return TYPE_INT
		return TYPE_INT.getInstance();
	}

	public TEMP IRme() {
		// return a new TEMP with the value of i
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		// Add IR command to load the integer value into the new TEMP
		IR.getInstance().Add_IRcommand(new IRcommandConstInt(t, this.i,IR.getInstance().currLine));
		return t;
	}

}
