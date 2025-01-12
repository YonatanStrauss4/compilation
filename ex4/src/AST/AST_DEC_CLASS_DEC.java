package AST;
import TYPES.*;

public class AST_DEC_CLASS_DEC extends AST_DEC
{

	public AST_CLASS_DEC classDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASS_DEC(AST_CLASS_DEC classDec)
	{
	
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/* print derivation rule */
		System.out.format("====================== dec -> classDec\n");

		/***************************************/
		this.classDec = classDec;

	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("AST NODE DEC CLASS DEC\n");
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nCLASS DEC\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (classDec != null)
		{
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,classDec.SerialNumber);
		} 
	}

	public TYPE SemantMe()
	{	
		// semant the class declaration
		if (classDec != null)
		{
			return classDec.SemantMe();
		}
		return null;
	}
}
