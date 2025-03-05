package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import SYMBOL_TABLE.*;


public class AST_STMT_ASSIGN extends AST_STMT
{

	public AST_VAR var;
	public AST_EXP exp;
	public int line;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP exp, int line)
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
		this.exp = exp;
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
		if (exp != null) exp.PrintMe();

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
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	public TYPE SemantMe()
	{
		// semant the variavble and the NEW expression
		TYPE varType = var.SemantMe();
		TYPE expType = exp.SemantMe();

		if (varType instanceof TYPE_CLASS){
			if (expType instanceof TYPE_CLASS){
				if(!(((TYPE_CLASS)expType).checkIfInherit((TYPE_CLASS)varType))){
					System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
					printError(line);
				}
			}
			else{
				if (!(expType instanceof TYPE_NIL)){
					System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
					printError(line);
				}
			}
		}
		else if (varType instanceof TYPE_ARRAY){
			if (expType instanceof TYPE_ARRAY){
				if(!((((TYPE_ARRAY)expType).name).equals(((TYPE_ARRAY)varType).name))){
					System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
					printError(line);
				}
			}
			else{
				if (!(expType instanceof TYPE_NIL)){
					System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
					printError(line);
				}
			}
		}
		else if (!(varType.equals(expType))){
			System.out.format(">> ERROR [%d] Type mismatch in assignment: %s cannot be assigned to %s\n", line, expType.name, varType.name);
			printError(line);	
		}

		return null;
	}

	public TEMP IRme(){
		// if the variable is a simple variable
		if(var instanceof AST_VAR_SIMPLE){

			// get the offset and IRme the expression
			int offset = OFFSET_TABLE.getInstance().findVariableOffset(((AST_VAR_SIMPLE)var).varName);	
			TEMP src = exp.IRme();	

			// add IR command to store the expression in the variable
			IR.getInstance().Add_IRcommand(new IRcommand_Store(((AST_VAR_SIMPLE)var).varName, src, offset, IR.getInstance().currLine));
		}

		// if the variable is a subscript variable
		if(var instanceof AST_VAR_SUBSCRIPT){

			// IRme the relevant variables
			TEMP index = ((AST_VAR_SUBSCRIPT)var).idxValue.IRme();
			TEMP newVal = exp.IRme();
			TEMP arr = ((AST_VAR_SUBSCRIPT)var).arrayName.IRme();

			// add IR command to set the array
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Array(arr, index, newVal, IR.getInstance().currLine));
		}

		// if the variable is a field variable
		if(var instanceof AST_VAR_FIELD){

			// get the class instance, the data member name and IRme the expression and the variable
			String variableDataMemberName = ((AST_VAR_FIELD)var).variableDataMemberName;
			String classInstanceName = ((AST_VAR_SIMPLE)(((AST_VAR_FIELD)var).var)).varName;
			OFFSET_TABLE_ENTRY clsInstance = OFFSET_TABLE.getInstance().findClassInstance(classInstanceName);
			TEMP classInstanceTemp = ((AST_VAR_FIELD)var).IRmeHelper("L");
			TEMP src = exp.IRme();

			// get the offset of the variable
			int offset = CLASSES_MAP.getInstance().getFieldOffset(clsInstance.className, variableDataMemberName);

			// add IR command to set the field
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Field(classInstanceTemp, src, variableDataMemberName, offset, IR.getInstance().currLine));
			
		}
		return null;
	}

}
