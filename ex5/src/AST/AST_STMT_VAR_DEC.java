package AST;
import TYPES.*;
import TEMP.*;


public class AST_STMT_VAR_DEC extends AST_STMT
{

	public AST_VAR_DEC varDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_VAR_DEC(AST_VAR_DEC varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.varDec = varDec;
	}
	
	public void PrintMe()
	{
		varDec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT\nVAR\nDEC"));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);		
	}

	public TYPE SemantMe()
	{
		// semant the variable declaration
		if(varDec != null) 
		{
			return varDec.SemantMe();
		}

		return null;
	}

	public TEMP IRme() {
		// IRme the variable declaration
		if(varDec != null) 
		{
			return varDec.IRme();
		}

		return null;
	}
}
