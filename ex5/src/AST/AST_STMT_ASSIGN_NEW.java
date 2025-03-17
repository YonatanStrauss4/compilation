package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import MIPS.*;

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

    public TEMP IRme()
    {
        // if the variable is a simple variable
		if(var instanceof AST_VAR_SIMPLE){
            // IRme the new expression
			TEMP src = newExp.IRme();	

            // get the offset of the variable
            String varName = ((AST_VAR_SIMPLE)var).varName;
            int offset = OFFSET_TABLE.getInstance().findVariableOffset(varName);

            // IRme the variable
            TEMP t = var.IRme();

            // if the expression is a class instance
            if(newExp instanceof AST_NEW_TYPE)
            {
                IR.getInstance().Add_IRcommand(new IRcommand_new_Class_Alloc(t, ((AST_TYPE_ID)(((AST_NEW_TYPE)newExp).type)).typeName, IR.getInstance().currLine));
            }

            // if the expression is a new array
            else
            {
                IR.getInstance().Add_IRcommand(new IRcommand_new_Array_Alloc(t, src, IR.getInstance().currLine));
            }
            // store the new expression in the variable temp
            IR.getInstance().Add_IRcommand(new IRcommand_Store_Local(varName, t, offset, IR.getInstance().currLine));  //need to add cases
		}

		// if the variable is a subscript variable
		if(var instanceof AST_VAR_SUBSCRIPT){

            // IRme the index and the new expression and get the array name and index
			TEMP index = ((AST_VAR_SUBSCRIPT)var).idxValue.IRme();
			TEMP newVal = newExp.IRme();
			TEMP arr = ((AST_VAR_SUBSCRIPT)var).arrayName.IRme();
            TEMP src = newExp.IRme();

            // if the expression is a class instance
            if(newExp instanceof AST_NEW_TYPE)
            {
                IR.getInstance().Add_IRcommand(new IRcommand_new_Class_Alloc(arr, ((AST_TYPE_ID)(((AST_NEW_TYPE)newExp).type)).typeName, IR.getInstance().currLine));
            }

            // if the expression is a new array
            else
            {
                IR.getInstance().Add_IRcommand(new IRcommand_new_Array_Alloc(arr, src, IR.getInstance().currLine));
            }

            // set the array with the new expression
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Array(arr, index, newVal, IR.getInstance().currLine));
		}

		// if the variable is a field variable
		if(var instanceof AST_VAR_FIELD){
            // get the class instance name and the variable data member name
			String variableDataMemberName = ((AST_VAR_FIELD)var).variableDataMemberName;
			String classInstanceName = this.var.varClassName;

            // get the class instance and IRme the new expression and the variable, and get the offset of the variable
			TEMP src = newExp.IRme();
		    TEMP newTemp = TEMP_FACTORY.getInstance().getFreshTEMP(); 
			int offset = CLASSES_MAP.getInstance().getFieldOffset(classInstanceName, variableDataMemberName);

            // if the new expression is a class instance
            if(newExp instanceof AST_NEW_TYPE){
                // update the class entry in the offset table to be the runtime class name
                OFFSET_TABLE_ENTRY entry = OFFSET_TABLE.getInstance().findClassInstance(classInstanceName);
                entry.className = ((AST_TYPE_ID)(((AST_NEW_TYPE)newExp).type)).typeName;
                entry.isClassInstance = true;

                // allocate a new class instance
                IR.getInstance().Add_IRcommand(new IRcommand_new_Class_Alloc(newTemp, entry.className, IR.getInstance().currLine));
            }

            // if the new expression is a new array
            else{
                // allocate a new array
                IR.getInstance().Add_IRcommand(new IRcommand_new_Array_Alloc(newTemp, src, IR.getInstance().currLine));
            }

            TEMP classInstanceTemp = ((AST_VAR_FIELD)var).IRmeHelper("L");
            // set the field with the new expression
			IR.getInstance().Add_IRcommand(new IRcommand_Set_Field(classInstanceTemp, newTemp, variableDataMemberName, offset, IR.getInstance().currLine));
			
		}
		return null; 
    }
}
