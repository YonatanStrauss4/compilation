package AST;

public class AST_CLASS_DEC_EXTENDS extends AST_CLASS_DEC
{
    public String name1;
    public String name2;
    public AST_CFIELD_REC cFR ;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_CLASS_DEC_EXTENDS(String name1, String name2, AST_CFIELD_REC cFR)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== classDec -> CLASS ID( %s ) EXTENDS ID( %s ) LBRACE cFieldRec RBRACE\n", name1, name2);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.name1 = name1;
        this.name2 = name2;
        this.cFR = cFR;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = CLASS DEC EXTENDS */
        /*********************************/
        System.out.println("AST NODE CLASS DECLARATION EXTENDS");

        /******************************************/
        /* PRINT class name, superclass, and body ... */
        /******************************************/
        System.out.format("CLASS NAME( %s )\n", name1);
        System.out.format("SUPERCLASS( %s )\n", name2);
        if (cFR != null) cFR.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("CLASS DEC\nEXTENDS\n...->%s", name1));

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (cFR != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, cFR.SerialNumber);
    }
}
