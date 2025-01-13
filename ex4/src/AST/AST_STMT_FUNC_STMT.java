package AST;
import TYPES.*;
import TEMP.*;

public class AST_STMT_FUNC_STMT extends AST_STMT
{

    public AST_FUNC_STMT funcStmt;

    // CONSTRUCTOR(S)
    public AST_STMT_FUNC_STMT(AST_FUNC_STMT funcStmt) {
        // SET A UNIQUE SERIAL NUMBER
        SerialNumber = AST_Node_Serial_Number.getFresh();

        // PRINT CORRESPONDING DERIVATION RULE
        System.out.println("====================== stmt-> funcStmt");

        // COPY INPUT DATA MEMBERS ...
        this.funcStmt = funcStmt;
    }

    public void PrintMe() {
        // AST NODE TYPE = STMT FUNC STMT
        System.out.println("AST NODE STMT FUNC STMT");

        // RECURSIVELY PRINT  ...
        if (funcStmt != null) funcStmt.PrintMe();

        // PRINT Node to AST GRAPHVIZ DOT file
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nFUNC STMT");

        // PRINT Edges to AST GRAPHVIZ DOT file
        if (funcStmt != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, funcStmt.SerialNumber);
    }

    public TYPE SemantMe() {
        // semant the function statement
        if (funcStmt != null)
        { 
            funcStmt.SemantMe();
        }
        return null;
    }

    public TEMP IRme() {		
		return null;
	}

}
