package AST;
import TYPES.*;
import TEMP.*;


public class AST_EXP_ARGUMENTS extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP argument;
	public AST_EXP_ARGUMENTS argumentList;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_ARGUMENTS(AST_EXP argument, AST_EXP_ARGUMENTS argumentList)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (argumentList != null) System.out.print("====================== expArguments -> exp expArguments\n");
		if (argumentList == null) System.out.print("====================== expArguments -> exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.argument = argument;
		this.argumentList = argumentList;
	}

	/******************************************************/
	/* The printing message for an expression arguments AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST EXP ARGUMENTS */
		/**************************************/
		System.out.print("AST NODE EXP ARGUMENTS\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (argument != null) argument.PrintMe();
		if (argumentList != null) argumentList.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nARGUMENTS\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (argument != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, argument.SerialNumber);
		if (argumentList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, argumentList.SerialNumber);
	}

	public TYPE_LIST SemantMe()
	{
		// semant the params list recursively
		TYPE type  = argument.SemantMe();

		if(argumentList == null)
		{
			return new TYPE_LIST(type,null);
		}
		else
		{
			return new TYPE_LIST(type, argumentList.SemantMe());
		}

	}
}
