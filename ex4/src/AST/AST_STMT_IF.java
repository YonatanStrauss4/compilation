package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_IF extends AST_STMT
{
	
	public AST_EXP cond;
	public AST_STMT_LIST body;
	public int line;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_IF(AST_EXP cond,AST_STMT_LIST body, int line)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.cond = cond;
		this.body = body;
		this.line = line;
	}

	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT IF\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (cond != null) cond.PrintMe();
		if (body != null) body.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"IF (left)\nTHEN right");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
		if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}

	public TYPE SemantMe()
	{

		// check if missing condition
        if(cond == null){
            System.out.format(">> ERROR [%d] missing condition for while loop\n", line);
            printError(line);
        }
		
		// semant the condition and checck if it is of TYPE_INT
		TYPE conditionType = cond.SemantMe();
		if (!(conditionType instanceof TYPE_INT))
		{
			System.out.format(">> ERROR [%d] condition of if statement is not integral\n", this.line);
			printError(line);
		}

		// begin if scope
		SYMBOL_TABLE.getInstance().beginScope();
		SYMBOL_TABLE.getInstance().updateCurrentScopeLevelUp();

		// semant if body
		body.SemantMe();

		// end if scope
		SYMBOL_TABLE.getInstance().updateCurrentScopeLevelDown();
		SYMBOL_TABLE.getInstance().endScope();

		return null;		
	}

    public TEMP IRme() {

		// [1] Allocate 2 fresh labels
		String label_start = IRcommand.getFreshLabel("if_start");
		String label_end = IRcommand.getFreshLabel("if_end");

		// [2] entry label for the if
        IRcommand lbl_strt_cmd = new IRcommand_Label(label_start,IR.getInstance().currLine,true);
		IR.
		getInstance().
		Add_IRcommand(lbl_strt_cmd);

		// [3] cond.IRme();
		TEMP cond_temp = cond.IRme();

		// [4] Jump conditionally to the if end
		IRcommand jmp_if_eq_zero = new IRcommand_Jump_If_Eq_To_Zero(cond_temp, label_end,IR.getInstance().currLine);
		IR.
		getInstance().
		Add_IRcommand(jmp_if_eq_zero);		

		// [5] body.IRme()
		body.IRme();

		// [6] If end label
		IRcommand lbl_end_cmd = new IRcommand_Label(label_end,IR.getInstance().currLine,false);
		IR.
		getInstance().
		Add_IRcommand(lbl_end_cmd);

		// [7] update CFG
		IR.getInstance().controlGraph.update_CFG(jmp_if_eq_zero,lbl_end_cmd);

		// [8] return null
		return null;
	}
}
