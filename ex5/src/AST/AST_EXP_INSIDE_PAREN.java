package AST;
import TYPES.*;
import IR.*;
import TEMP.*;


public class AST_EXP_INSIDE_PAREN extends AST_EXP
{

    public AST_EXP expInParen;

    // CONSTRUCTOR(S)
    public AST_EXP_INSIDE_PAREN(AST_EXP expInParen) {
        // SET A UNIQUE SERIAL NUMBER
        SerialNumber = AST_Node_Serial_Number.getFresh();

        // PRINT CORRESPONDING DERIVATION RULE
        System.out.println("====================== exp -> LPAREN exp RPAREN\n");

        // COPY INPUT DATA MEMBERS ...
        this.expInParen = expInParen;
    }

    public void PrintMe() {
        // AST NODE TYPE = EXP INSIDE PAREN
        System.out.println("AST NODE EXP INSIDE PAREN");

        /* RECURSIVELY PRINT e ... */
        if (expInParen != null) expInParen.PrintMe();

        /* PRINT Node to AST GRAPHVIZ DOT file */
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "EXP\nINSIDE PAREN");

        /* PRINT Edges to AST GRAPHVIZ DOT file */
        if (expInParen != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, expInParen.SerialNumber);
    }

    public TYPE SemantMe() {
        // semant the expression inside the parenthesis
        return expInParen.SemantMe();
    }

    public TEMP IRme() {
        return expInParen.IRme();
    }   
}
