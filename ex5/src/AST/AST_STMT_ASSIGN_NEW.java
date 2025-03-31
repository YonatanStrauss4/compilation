package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import MIPS.*;
import SYMBOL_TABLE.*;

public class AST_STMT_ASSIGN_NEW extends AST_STMT
{

    public AST_VAR var;
    public AST_NEW_EXP newExp;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEW_EXP newExp, int line) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== stmt -> var ASSIGN newExp SEMICOLON\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.var = var;
        this.newExp = newExp;
        this.line = line;
    }

    /***************************************************/
    /* The printing message for an assignment statement with new expression AST node */
    /***************************************************/
    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = STMT ASSIGN NEW */
        /*********************************/
        System.out.println("AST NODE STMT ASSIGN NEW");

        /******************************************/
        /* RECURSIVELY PRINT v and new_exp ... */
        /******************************************/
        if (var != null) var.PrintMe();
        if (newExp != null) newExp.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nASSIGN NEW");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
        if (newExp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, newExp.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the variavble and the NEW expression
        TYPE varType = var.SemantMe();
        TYPE newType = newExp.SemantMe();

        TYPE elemType = null;

        if (varType instanceof TYPE_ARRAY){
            elemType = ((TYPE_ARRAY)varType).type;
        }
        else {
            elemType = varType;
        }

        // check if variable types are equal
        if(!elemType.equals(newType)){
            // check if the type of the variable is TYPE_CLASS
            if(!(varType instanceof TYPE_CLASS)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
            }

            // variable type is a class but not equal to newExp type, so we check for inheritance
            if(!((TYPE_CLASS)newType).checkIfInherit((TYPE_CLASS)varType)){
                System.out.format(">> ERROR(%d) type missmatch, cannot assign %s to %s\n",line, newType.name, varType.name);
                printError(line);
                }
            }

        // check if we assign a non array type to an array
        if(varType instanceof TYPE_ARRAY && !(newExp instanceof AST_NEW_TYPE_EXP_IN_BRACKS)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign non array to array\n",line);
            printError(line);
        }

        // check if we assign assign array type to a non array type
        if(!(varType instanceof TYPE_ARRAY) && !(newExp instanceof AST_NEW_TYPE)){
            System.out.format(">> ERROR(%d) type missmatch, cannot assign array to non array\n",line);
            printError(line);
        }

        return null;
    }

    public TEMP IRme(){
		// if the variable is a simple variable
		if(var instanceof AST_VAR_SIMPLE){
			String varName = ((AST_VAR_SIMPLE)var).varName;

			// get the offset and IRme the expression
            int offset = 0;
            if(SYMBOL_TABLE.getInstance().get_inside_class()){
                offset = OFFSET_TABLE.getInstance().findVariableOffset(varName, 1);
            }
            else{
                offset = OFFSET_TABLE.getInstance().findVariableOffset(varName, 0);
            }	

			TEMP src = newExp.IRme();	


			if (offset == -1){
				// we need now to check if maybe we are in a method and the variable is a class instance
				TYPE_CLASS currClass = SYMBOL_TABLE.getInstance().get_current_class();

				// we are in a method
				if(currClass != null){
					String className = currClass.name;
					int fieldOffset = CLASSES_MAP.getInstance().getFieldOffset(className, varName);

					// the variable is a field of the class or the classes it extends
					if(fieldOffset != -1){
						IR.getInstance().Add_IRcommand(new IRcommand_Store_Field(varName, src, fieldOffset, IR.getInstance().currLine));
					}
                    // the variable is a global variable
                    else{
                        IR.getInstance().Add_IRcommand(new IRcommand_Store_Global(varName, src, IR.getInstance().currLine));
                    }
				}

				// the variable is a global variable
				else{
					IR.getInstance().Add_IRcommand(new IRcommand_Store_Global(varName, src, IR.getInstance().currLine));
				}

			}
		
			// the variable is a function parameter
			else if(offset > 0){
				IR.getInstance().Add_IRcommand(new IRcommand_Store_param(varName, src, offset, IR.getInstance().currLine));
			}

			// the variable is a local variable
			else{
				IR.getInstance().Add_IRcommand(new IRcommand_Store_Local(varName, src, offset, IR.getInstance().currLine));
			}
				// add IR command to store the expression in the variable
		}

		// if the variable is a subscript variable
		if(var instanceof AST_VAR_SUBSCRIPT){
			// IRme the relevant variables
			TEMP index = ((AST_VAR_SUBSCRIPT)var).idxValue.IRme();
			TEMP newVal = newExp.IRme();
			TEMP arr = ((AST_VAR_SUBSCRIPT)var).arrayName.IRme();

			// add IR command to set the array
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Array(arr, index, newVal, IR.getInstance().currLine));
		}

		// if the variable is a field variable
		if(var instanceof AST_VAR_FIELD){
			// get the class instance, the data member name and IRme the expression and the variable
			String variableDataMemberName = ((AST_VAR_FIELD)var).variableDataMemberName;
			String classInstanceName = var.varClassName;
			TYPE clsInstance = SYMBOL_TABLE.getInstance().find(classInstanceName);
			TEMP classInstanceTemp = ((AST_VAR_FIELD)var).var.IRme();
			TEMP src = newExp.IRme();

			// get the offset of the variable
			int offset = CLASSES_MAP.getInstance().getFieldOffset(clsInstance.name, variableDataMemberName);

			// add IR command to set the field
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Field(classInstanceTemp, src, variableDataMemberName, offset, IR.getInstance().currLine));
			
		}
		return null;
	}
}
