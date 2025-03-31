package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import IR.*;
import TEMP.*;


public class AST_VAR_SIMPLE extends AST_VAR
{

	public String varName;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String varName, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",varName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varName = varName;
		this.line = line;
	}


	public void PrintMe() {
		// AST NODE TYPE = AST VAR SIMPLE
		System.out.format("AST NODE VAR SIMPLE( %s )\n",varName);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nSIMPLE\n(%s)",varName));
	}

	public TYPE SemantMe()
    {
        TYPE varFindType = null;
		//we now check to see if the variable has already been declared in the following order:
		//first we check from the inner scope
		varFindType = SYMBOL_TABLE.getInstance().findInClass(varName);
		if (varFindType != null && varFindType instanceof TYPE_CLASS){
			TYPE currClass = SYMBOL_TABLE.getInstance().get_current_class();
			if(!(currClass != null && ((TYPE_CLASS)currClass).name.equals(((TYPE_CLASS)varFindType).name))){
				varFindType = SYMBOL_TABLE.getInstance().findClassInSymbolTable(((TYPE_CLASS)varFindType).name);
			}
		}

		if (varFindType == null){
			// we want to check in inheritance tree
			TYPE currClass = SYMBOL_TABLE.getInstance().get_current_class();
			if (currClass != null && ((TYPE_CLASS)currClass).father != null){
				varFindType = ((TYPE_CLASS)currClass).findVariableInInheritanceTree(varName);
				if (varFindType != null && varFindType instanceof TYPE_CLASS){
					if(!(currClass != null && ((TYPE_CLASS)currClass).name.equals(((TYPE_CLASS)varFindType).name))){
						varFindType = SYMBOL_TABLE.getInstance().findClassInSymbolTable(((TYPE_CLASS)varFindType).name);
					}
				}
				if (varFindType == null && varFindType instanceof TYPE_CLASS){
					// only left to check if it is in global scope
					varFindType = SYMBOL_TABLE.getInstance().find(varName);
					if (varFindType != null && varFindType instanceof TYPE_CLASS){
						if(!(currClass != null && ((TYPE_CLASS)currClass).name.equals(((TYPE_CLASS)varFindType).name))){
							varFindType = SYMBOL_TABLE.getInstance().findClassInSymbolTable(((TYPE_CLASS)varFindType).name);
						}
					}
					if (varFindType == null){
						// we didnt find it
						System.out.format(">> ERROR(%d) variable %s has not been declared\n", this.line, varName);
						printError(this.line);
					}
				}
			}
			else {
				varFindType = SYMBOL_TABLE.getInstance().find(varName);
				if (varFindType != null && varFindType instanceof TYPE_CLASS){
					if(!(currClass != null && ((TYPE_CLASS)currClass).name.equals(((TYPE_CLASS)varFindType).name))){
						varFindType = SYMBOL_TABLE.getInstance().findClassInSymbolTable(((TYPE_CLASS)varFindType).name);
					}
				}
				if (varFindType == null){
					// we didnt find it
					System.out.format(">> ERROR(%d) variable %s has not been declared\n", this.line, varName);
					printError(this.line);
				}
			}
		}
			
		if(varFindType instanceof TYPE_CLASS){
			return (TYPE_CLASS)varFindType;
		}
		return varFindType;
	}

	public TEMP IRme() {
		// get fresh TEMP
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP(); 

		// get the offset of the variable
		int offset = 0;
		if(SYMBOL_TABLE.getInstance().get_inside_class()){
			offset = OFFSET_TABLE.getInstance().findVariableOffset(varName, 1);
		}
		else{
			offset = OFFSET_TABLE.getInstance().findVariableOffset(varName, 0);
		}
		
		// the variable is not a local variable
		if (offset == -1){
			// we need now to check if maybe we are in a method and the variable is a class instance
			TYPE_CLASS currClass = SYMBOL_TABLE.getInstance().get_current_class();

			// we are in a method
			if(currClass != null){
				String className = currClass.name;
				int fieldOffset = CLASSES_MAP.getInstance().getFieldOffset(className, varName);


				// the variable is a field of the class or the classes it extends
				if(fieldOffset != -1){
					IR.getInstance().Add_IRcommand(new IRcommand_Load_Field(t, varName, fieldOffset, IR.getInstance().currLine));
					return t;
				}
				// the variable is a global variable
				else{
					String type = OFFSET_TABLE.getInstance().getGlobalType(varName);
					if(type.equals("STRING")){
						IR.getInstance().Add_IRcommand(new IRcommand_Load_Global_Var(t, varName, true, IR.getInstance().currLine));
					}
					else{
						IR.getInstance().Add_IRcommand(new IRcommand_Load_Global_Var(t, varName, false, IR.getInstance().currLine));
					}
				}
			}

			// the variable is a global variable
			else{
				String type = OFFSET_TABLE.getInstance().getGlobalType(varName);
				if(type.equals("STRING")){
					IR.getInstance().Add_IRcommand(new IRcommand_Load_Global_Var(t, varName, true, IR.getInstance().currLine));
				}
				else{
					IR.getInstance().Add_IRcommand(new IRcommand_Load_Global_Var(t, varName, false, IR.getInstance().currLine));
				}
			}

		}
		
		// the variable is a function parameter
		else if(offset > 0){
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Param(t, offset, varName,  IR.getInstance().currLine));
		}

		// the variable is a local variable
		else{
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Local_Var(t, varName, offset, IR.getInstance().currLine));
		}
		
		return t;
	}
}