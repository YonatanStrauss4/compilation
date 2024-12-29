package AST;
import TYPES.*;

public class AST_PROGRAM_REC extends AST_PROGRAM
{

	public AST_DEC dec;
	public AST_PROGRAM decList;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_PROGRAM_REC(AST_DEC dec ,AST_PROGRAM decList)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.dec = dec;
		this.decList = decList;
	}

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		System.out.print("AST NODE DEC LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (dec != null) dec.PrintMe();
		if (decList != null) decList.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nLIST\n");
				
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (dec != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, dec.SerialNumber);
		if (decList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, decList.SerialNumber);
	}

	public TYPE SemantMe()
	{		
		// recursively semant the declarations of the program
		if (dec != null) 
		{
			dec.SemantMe();
		}
		if (decList != null)
		{
			decList.SemantMe();
		}
		
		return null;	
	}
}
