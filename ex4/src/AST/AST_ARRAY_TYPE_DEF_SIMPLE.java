package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;

public class AST_ARRAY_TYPE_DEF_SIMPLE extends AST_ARRAY_TYPE_DEF {

	public String name;
  	public AST_TYPE t;
	public int line;
	
	public AST_ARRAY_TYPE_DEF_SIMPLE(String name, AST_TYPE t, int line) {
		/* SET A UNIQUE SERIAL NUMBER */
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/* PRINT CORRESPONDING DERIVATION RULE */
		System.out.format("====================== arrayTypedef -> ARRAY ID( %s ) EQ type LBRACK RBRACK SEMICOLON\n", name);

		/* COPY INPUT DATA MEMBERS ... */
		this.name = name;
    	this.t = t;
		this.line = line;
	}

	/* The printing message for an ARRAY TYPEDEF AST node */
	public void PrintMe() {
        /* AST NODE TYPE = ARRAY TYPEDEF */
        System.out.println("AST NODE ARRAY TYPEDEF");

        /******************************************/
        /* PRINT name and type */
        /******************************************/
        System.out.format("ARRAY NAME( %s )\n", name);
        System.out.print("TYPE:\n");
        if (t != null) t.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("ARRAY\nTYPEDEF\n...->%s", name));

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, t.SerialNumber);
    }

	public TYPE SemantMe()
	{
		
		TYPE type = t.SemantMe();

		// check if array has type
		if(type == null){
			System.out.format(">> ERROR(%d) no type declared\n",line);
			printError(line);
		}

		// check if array has been declared with type void
		if(type instanceof TYPE_VOID || type instanceof TYPE_NIL){
			System.out.format(">> ERROR(%d) array type cannot be of void or nil type\n",line);
			printError(line);
		}


		// check if array has been defined in global scope
		if(SYMBOL_TABLE.getInstance().getCurrentScopeLevel() != 0){
			System.out.format(">> ERROR(%d) array type definition must be in global scope\n",line);
			printError(line);
		}

		// check if function name is already declared in scope or is a reserved word
		if (SYMBOL_TABLE.getInstance().find(name) != null || isReservedWord(name)){
			System.out.format(">> ERROR(%d) the name %s already been used in scope or is a reserved word\n", this.line, name);
			printError(this.line);
		}		

		// create an array instance
		TYPE_ARRAY array = new TYPE_ARRAY(type, name);
		
		// enter the array to the symbol table
		SYMBOL_TABLE.getInstance().enter(name, array, false, false);

		//return the array
		return array;
	}
}
