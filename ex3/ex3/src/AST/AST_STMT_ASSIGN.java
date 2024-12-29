package AST;
import TYPES.*;

public class AST_STMT_ASSIGN extends AST_STMT
{

	public AST_VAR var;
	public AST_EXP newExp;
	public int line;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP newExp, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt-> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.newExp = newExp;
		this.line = line;
	}

	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST STMT ASSIGN */
		/********************************************/
		System.out.print("AST NODE STMT ASSIGN\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.PrintMe();
		if (newExp != null) newExp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nASSIGN\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,newExp.SerialNumber);
	}

	public TYPE SemantMe()
	{
		// semant the variavble and the NEW expression
		TYPE varType = var.SemantMe();
		TYPE expType = newExp.SemantMe();

		
		// we check: if the type of the variable is: class/array/nil and the expression type is nil, and the types are equal
		if(!(varType instanceof TYPE_ARRAY || varType instanceof TYPE_CLASS || varType instanceof TYPE_NIL && expType instanceof TYPE_NIL) && !varType.equals(expType)){
			if(varType instanceof TYPE_CLASS && expType instanceof TYPE_CLASS){
				if(!(((TYPE_CLASS)expType).checkIfInherit((TYPE_CLASS)varType))){
					System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
				}
			}

		}

		return null;
	}
}
