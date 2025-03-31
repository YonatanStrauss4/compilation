package AST;
import TYPES.*;
import IR.*;
import TEMP.*;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;
	public int line;
	public boolean isString;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right,int OP, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
		this.line = line;
		this.isString = false;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP="";
		
		/*********************************/
		/* CONVERT OP to a printable sOP */
		/*********************************/
		if (OP == 0) {sOP = "+";}
		if (OP == 1) {sOP = "-";}
		if (OP == 2) {sOP = "*";}	
		if (OP == 3) {sOP = "/";}
		if (OP == 4) {sOP = "<";}
		if (OP == 5) {sOP = ">";}
		if (OP == 6) {sOP = "=";}
		
		/*************************************/
		/* AST NODE TYPE = AST BINOP EXP */
		/*************************************/
		System.out.print("AST NODE EXP BINOP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)",sOP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		// semant the two sides of the binop
		if (left  != null) t1 = left.SemantMe();
		if (right != null) t2 = right.SemantMe();
		
		// checking conditions for -,*,/,<,>
		if(1 <= this.OP && this.OP <= 5){
			// check if dividing by zero
			if(this.OP == 3){
				if(t2 instanceof TYPE_INT && right instanceof AST_EXP_INT && ((AST_EXP_INT)right).i == 0){
					System.out.format(">> ERROR(%d) division by zero\n", this.line);
					printError(this.line);
				}
			}

			// check if both sides are integers
			if(!(t1 instanceof TYPE_INT && t2 instanceof TYPE_INT)){
				System.out.format(">> ERROR(%d) /,<,>,-,* operations must be between two integers\n", this.line);
				printError(this.line);
			}
			varType = "int";
			return TYPE_INT.getInstance();
		}
		
		// + operation
		if(this.OP == 0){
			// check if + operation is between two strings or integers
			if(!(t1 instanceof TYPE_INT && t2 instanceof TYPE_INT) && !(t1 instanceof TYPE_STRING && t2 instanceof TYPE_STRING)){
				System.out.format(">> ERROR(%d) + operation must be between two integers or two strings\n", this.line);
				printError(this.line);
			}

			// if + integers, return TYPE_INT
			if(t1 instanceof TYPE_INT){
				varType = "int";
				return TYPE_INT.getInstance();
			}
			this.isString = true;
			//return TYPE_STRING
			varType = "string";
			return TYPE_STRING.getInstance();
		}
		
		// equal operation
		if(this.OP == 6){
			// check if trying to compare a null 
			if(t1 == null || t2 == null){
				System.out.format(">> ERROR(%d) = cannot compare null types\n", this.line);
				printError(this.line);
			}

			
			// both are class instances
			if(t1.isClass() && t2.isClass()){
				// check if the same class
				if(!((TYPE_CLASS)t1).name.equals(((TYPE_CLASS)t2).name)){
					// not the same class, so check if one class inherits the other (check both ways)
					if(!(((TYPE_CLASS)t1).checkIfInherit((TYPE_CLASS)t2) || ((TYPE_CLASS)t2).checkIfInherit((TYPE_CLASS)t1))){
						System.out.format(">> ERROR(%d) = Comparison between two different class that doesnt inherits eachother)\n", this.line);
						printError(this.line);
					}
				}
			}
			
			// both are array instances
			if(t1.isArray() && t2.isArray()){
				if(!((TYPE_ARRAY)t1).type.equals(((TYPE_ARRAY)t2).type)){
					System.out.format(">> ERROR(%d) = Comparison between two different types of arrays\n", this.line);
					printError(this.line);
				}
			}

			// diffrent types
			if(!(t1.equals(t2))){
				// check if one of them is nil
				if(!(t1 instanceof TYPE_NIL || t2 instanceof TYPE_NIL)){
					// check if the comparison is between a nil and a string
					if(!((t1 instanceof TYPE_STRING && t2 instanceof TYPE_NIL) || (t1 instanceof TYPE_NIL && t2 instanceof TYPE_STRING))){
						System.out.format(">> ERROR(%d) = Comparison between two different types\n", this.line);
						printError(this.line);
					}
				}
			}
			
			// binop returns int
			varType = "int";
			return TYPE_INT.getInstance();
		}

		// binop returns int
		varType = "int";
		return TYPE_INT.getInstance();
	}

	public TEMP IRme()
	{
		// IR the two sides of the binop
		TEMP t1 = null;
		TEMP t2 = null;

		if (left  != null) 
		{	
			t1 = left.IRme();
		}
		if (right != null) 
		{
			t2 = right.IRme();
		}

		// get a fresh temp
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

		
		// add the IR command for the binop
		// 0 - add
		if (OP == 0) {
			// if we are adding strings
			if (this.isString) 
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Strings(dst,t1,t2,IR.getInstance().currLine));
			}

			// if we are adding integers
			else 
			{	
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Integers(dst,t1,t2,IR.getInstance().currLine));
			}
		}

		// 1 - sub
		if (OP == 1) 
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Sub_Integers(dst,t1,t2,IR.getInstance().currLine));
		}

		// 2 - mul
		if (OP == 2) 
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Mul_Integers(dst,t1,t2,IR.getInstance().currLine));
		}

		// 3 - div
		if (OP == 3) 
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Div_Integers(dst,t1,t2,IR.getInstance().currLine));
		}

		// 4 - <
		if (OP == 4) 
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2,IR.getInstance().currLine));
		}

		// 5 - >
		if (OP == 5) 
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t2,t1,IR.getInstance().currLine));
		}

		// 6 - ==
		if (OP == 6) 
		{
			if(left.varType.equals("string") && right.varType.equals("string"))
			{
								System.out.println("2222222222");	

				IR.getInstance().Add_IRcommand(new IRcommand_binop_EQ_Strings(dst,t1,t2,IR.getInstance().currLine));
			}
			else if(left.varType.equals("int") && right.varType.equals("int"))
			{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_EQ_Integers(dst,t1,t2,IR.getInstance().currLine));
			}
			else{
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_EQ_Addresses(dst,t1,t2,IR.getInstance().currLine));
			}
	 	} 	 
		return dst;
	}
}
