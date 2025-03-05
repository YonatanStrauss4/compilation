package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import SYMBOL_TABLE.*;

public class AST_EXP_STRING extends AST_EXP
{
	
	public String value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String value)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> STRING( %s )\n", value);
		this.value = value;
	}

	/******************************************************/
	/* The printing message for a STRING EXP AST node */
	/******************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST STRING EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",value);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING\n%s",value.replace('"','\'')));
	}

	public TYPE SemantMe()
	{
		// return TYPE_STRING
		return TYPE_STRING.getInstance();
	}

	public TEMP IRme()
	{
		// deleting the quotes from the string
		String strValue = value.substring(1, value.length() - 1);

		// if the string is not in the map, add it
		if(!MAP_OF_STRINGS.getInstance().containsKey(value)){

			// add the string to the .data section
			IR.getInstance().Add_IRcommand(new IRcommand_Const_String(value, "str_" + strValue, IR.getInstance().currLine));

			// add the string to the map
    		MAP_OF_STRINGS.getInstance().put(value, "str_" + strValue);
		}

		// if we are not inside a class or a function, return null, we already allocated the string
		if(!SYMBOL_TABLE.getInstance().get_inside_class() && !SYMBOL_TABLE.getInstance().get_inside_function()){
			return null;
		}

		// we are not in the global scope
		else {

			// get the first entry in the offset table
			OFFSET_TABLE_ENTRY entry = OFFSET_TABLE.getInstance().offsetTable.get(0);

			// we want to ensure that this string is not a data member of a class before loading it
			if (OFFSET_TABLE.getInstance().offsetTable.size() != 1 || !entry.type.equals("CLASS")) {
				
				// load the string from the map
				TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
				IR.getInstance().Add_IRcommand(new IRcommand_Load_Const_String(t, MAP_OF_STRINGS.getInstance().get(value), IR.getInstance().currLine));
				return t;
			}

			return null;
		}
	}
}
