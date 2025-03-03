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
		String strValue = value.substring(1, value.length() - 1);
		if(!MAP_OF_STRINGS.getInstance().containsKey(value)){
			IR.getInstance().Add_IRcommand(new IRcommand_Const_String(value, "str_" + strValue, IR.getInstance().currLine));
			System.out.println("Adding string to map: " + value + " -> str_" + strValue);
    		MAP_OF_STRINGS.getInstance().put(value, "str_" + strValue);
		}
		if(!SYMBOL_TABLE.getInstance().get_inside_class() && !SYMBOL_TABLE.getInstance().get_inside_function()){
			return null;
		}
		else{
			TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Const_String(t, MAP_OF_STRINGS.getInstance().get(value), IR.getInstance().currLine));
			return t;
		}
	}
}
