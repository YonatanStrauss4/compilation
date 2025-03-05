package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_FIELD extends AST_VAR
{

	public AST_VAR var;
	public String variableDataMemberName;
	public String side = "R";
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var, String variableDataMemberName, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",variableDataMemberName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.variableDataMemberName = variableDataMemberName;
		this.line = line;
	}

	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST VAR FIELD */
		/*********************************/
		System.out.print("AST NODE VAR FIELD\n");

		/**********************************************/
		/* RECURSIVELY PRINT v, then name ... */
		/**********************************************/
		if (var != null) var.PrintMe();
		System.out.format("FIELD NAME( %s )\n",variableDataMemberName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nFIELD\n...->%s",variableDataMemberName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}

	public TYPE SemantMe()
	{
		// semant the variable
		TYPE type = var.SemantMe();
		
		// check if the variable is TYPE_CLASS
		if(!(type instanceof TYPE_CLASS)){
			System.out.format(">> ERROR(%d) variable is not an instance of a class\n", this.line);
			printError(this.line);
		}

		// check if the variable is indeed a data member of the class or the classes it extends, if we are in the class currently
		type = (TYPE_CLASS)type;

		TYPE varSearch = null;
		if(SYMBOL_TABLE.getInstance().get_current_class() != null && SYMBOL_TABLE.getInstance().get_current_class().name.equals(type.name)){
			varSearch = SYMBOL_TABLE.getInstance().currentClassVariableMembers.search(variableDataMemberName);
			if(varSearch == null){
				varSearch = ((TYPE_CLASS)type).findVariableInInheritanceTree(variableDataMemberName);
				if(varSearch == null){
					System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends\n", this.line, variableDataMemberName);
					printError(this.line);
				}
			}
		}
		
		//else, we just look at the class and its inheritance tree
		else{
			varSearch = ((TYPE_CLASS)type).findVariableInInheritanceTree(variableDataMemberName);
			if(varSearch == null){
				System.out.format(">> ERROR(%d) %s is not a data memeber of the class or the classes it extends\n", this.line, variableDataMemberName);
				printError(this.line);
			}
		}
	
		// return the variable data member type we found (if we found it)
		return varSearch;
	}

	public TEMP IRme()
	{
		// IRme the variable
		TEMP t = var.IRme();

		// get a fresh TEMP
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// get the class instance of the variable
		OFFSET_TABLE_ENTRY clsInstance = OFFSET_TABLE.getInstance().findClassInstance(((AST_VAR_SIMPLE)var).varName);

		//get the offset of the field
		int offset = CLASSES_MAP.getInstance().getFieldOffset(clsInstance.className, variableDataMemberName);

		// check if the field is a class instance
		OFFSET_TABLE_ENTRY checkIfFieldIsClassInstance = OFFSET_TABLE.getInstance().findClassInstance(variableDataMemberName);

		// if the field is a class instance, we need to return t (recursive)
		if(checkIfFieldIsClassInstance == null && side.equals("L"))
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Access_Field(dst, t, variableDataMemberName, offset, IR.getInstance().currLine));
			return t;
		}

		// if the field is not a class instance, we need to return dst (no recursion)
		else
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Access_Field(dst, t, variableDataMemberName, offset, IR.getInstance().currLine));
			return dst;
		}
	}

	// IRme helper to tell that we are in the left side of the assignmnet
	public TEMP IRmeHelper(String side){
		this.side = side;
		return IRme();
	}
}
