package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_CFIELD_REC extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELD cF;
	public AST_CFIELD_REC cFR;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_REC(AST_CFIELD cF, AST_CFIELD_REC cFR)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (cFR != null) System.out.print("====================== cFieldRec -> cField cFieldRec\n");
		if (cFR == null) System.out.print("====================== cFieldRec -> cField\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.cF = cF;
		this.cFR = cFR;
	}

	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST CFIELD REC */
		/**************************************/
		System.out.print("AST NODE cField rec\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (cF != null) cF.PrintMe();
		if (cFR != null) cFR.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"cField\nrec\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cF != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cF.SerialNumber);
		if (cFR != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cFR.SerialNumber);
	}

	public TYPE_LIST SemantMe()
	{
		// semant recursively
		TYPE t = cF.SemantMe();

		if(cFR == null)
		{
			return new TYPE_LIST(t,null);
		}
		else
		{
			return new TYPE_LIST(t,cFR.SemantMe());
		}
	}

	public TEMP IRme()
	{
		// First, generate IR for the current class field `cF`
		TEMP t = cF.IRme();

		// If `cFR` is not null, generate IR for the rest of the class fields
		if (cFR != null)
		{
			cFR.IRme(); // Recursively call IRme on the next part of the list
		}

		return t;  // Return the TEMP generated for `cF`, or null if no TEMP was generated
	}


	
}
