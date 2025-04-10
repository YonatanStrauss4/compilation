package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_STMT_WHILE extends AST_STMT
{

    public AST_EXP cond;
    public AST_STMT_LIST body;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_STMT_WHILE(AST_EXP cond, AST_STMT_LIST body, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== stmt -> WHILE LPAREN exp RPAREN LBRACE stmtList RBRACE\n");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.cond = cond;
        this.body = body;
        this.line = line;
    }

    /***************************************************/
    /* The printing message for a while loop statement AST node */
    /***************************************************/
    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = STMT WHILE */
        /*********************************/
        System.out.println("AST NODE STMT WHILE");

        /******************************************/
        /* RECURSIVELY PRINT e and body ... */
        /******************************************/
        if (cond != null) cond.PrintMe();
        if (body != null) body.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT\nWHILE");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (cond != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, cond.SerialNumber);
        if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, body.SerialNumber);
    }

    public TYPE SemantMe()
    {

        // check if missing condition
        if(cond == null){
            System.out.format(">> ERROR [%d] missing condition for while loop\n", line);
            printError(line);
        }

        // semant the condition and check if it is of TYPE_INT
        TYPE conditionType = cond.SemantMe();
        if (!(conditionType instanceof TYPE_INT))
        {
            System.out.format(">> ERROR [%d] condition inside WHILE is not of type INT\n", line);
            printError(line);
        }
        
        // begin while scope
        SYMBOL_TABLE.getInstance().beginScope();
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelUp();

        // semant while body
        body.SemantMe();

        // end while scope
        SYMBOL_TABLE.getInstance().updateCurrentScopeLevelDown();
        SYMBOL_TABLE.getInstance().endScope();

        return null;
    }
}
