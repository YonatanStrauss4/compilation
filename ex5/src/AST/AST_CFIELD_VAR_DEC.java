package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_CFIELD_VAR_DEC extends AST_CFIELD
{
	
	public AST_VAR_DEC varDec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_VAR_DEC(AST_VAR_DEC varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== cField -> varDec\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varDec = varDec;
	}


	 public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = CLASS FIELD VARIABLE DECLARATION */
        /*********************************/
        System.out.println("AST NODE CFIELD VAR DEC");

        /******************************************/
        /* RECURSIVELY PRINT var_dec ... */
        /******************************************/
        if (varDec != null) varDec.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "CLASS FIELD\nVARIABLE DECLARATION");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (varDec != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, varDec.SerialNumber);
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

	public TEMP IRme()
	{
		// IRme the variable declaration
		if(varDec != null) 
		{
			return varDec.IRme();
		}

		return null;
	}
}
