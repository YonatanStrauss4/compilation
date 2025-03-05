package AST;
import TYPES.*;
import TEMP.*;
import IR.*;


public class AST_EXP_VAR extends AST_EXP {

	public AST_VAR var;

	// CONSTRUCTOR(S)
	public AST_EXP_VAR(AST_VAR var) {
		// SET A UNIQUE SERIAL NUMBER
		SerialNumber = AST_Node_Serial_Number.getFresh();

		// PRINT CORRESPONDING DERIVATION RULE
		System.out.print("====================== exp -> var\n");

		// COPY INPUT DATA NENBERS ...
		this.var = var;
	}
	
	public void PrintMe() {
		/************************************/
		/* AST NODE TYPE = EXP VAR */
		/************************************/
		System.out.print("AST NODE EXP VAR\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		if (var != null) var.PrintMe();
		
		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nVAR");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
			
	}

	public TYPE SemantMe() {
		// semant the variable
		TYPE type = var.SemantMe();
		return type;
	}

	public TEMP IRme() {
		TEMP vari = var.IRme();
		return vari;
	}
}
