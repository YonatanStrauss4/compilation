package AST;
import TYPES.*;

public class AST_DEC_VAR_DEC extends AST_DEC
{

	public AST_VAR_DEC varDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VAR_DEC(AST_VAR_DEC varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		System.out.println("====================== dec -> varDec");

		this.varDec = varDec;
	}

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe() {
        // AST NODE TYPE = VAR DECLARATION
        System.out.println("AST NODE DECLARATION VAR DEC");

        /******************************************/
        /* RECURSIVELY PRINT varDec ... */
        /******************************************/
        if (varDec != null) varDec.PrintMe();

        // PRINT Node to AST GRAPHVIZ DOT file
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "DECLARATION\nVAR DEC");

        // PRINT Edges to AST GRAPHVIZ DOT file
        if (varDec != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, varDec.SerialNumber);
    }

	public TYPE SemantMe(){
		// semant the variable declaration
		if(varDec != null) 
			return varDec.SemantMe();
		return null;
	}
	
}
