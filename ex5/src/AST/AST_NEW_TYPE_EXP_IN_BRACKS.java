package AST;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_NEW_TYPE_EXP_IN_BRACKS extends AST_NEW_EXP
{
    
    public AST_TYPE typeOfElements;
    public AST_EXP arraySize;
    public int line;
    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_NEW_TYPE_EXP_IN_BRACKS(AST_TYPE typeOfElements, AST_EXP arraySize, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.println("====================== newExp -> NEW type LBRACK exp RBRACK");

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.typeOfElements = typeOfElements;
        this.arraySize = arraySize;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = NEW TYPE EXP IN BRACKS */
        /*********************************/
        System.out.println("AST NODE NEW TYPE EXP IN BRACKS");

        /******************************************/
        /* RECURSIVELY PRINT t and e ... */
        /******************************************/
        if (typeOfElements != null) typeOfElements.PrintMe();
        if (arraySize != null) arraySize.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "NEW TYPE\nEXP IN BRACKS");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (typeOfElements != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, typeOfElements.SerialNumber);
        if (arraySize != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, arraySize.SerialNumber);
    }

    public TYPE SemantMe()
    {
        // semant the type of the elements in the array
        TYPE type = typeOfElements.SemantMe();

        // check if it exists (the type)
        if(type == null)
        {
            System.out.format(">> ERROR(%d) non existing type\n",line);
            printError(line);
        }

        // check if the type of the elements is void
        if(type instanceof TYPE_VOID)
        {
            System.out.format(">> ERROR(%d) variable cannot be of void type\n",line);
            printError(line);
        }
        
        // semant the size of the array
        TYPE expType = arraySize.SemantMe();

        // check if it is of TYPE_INT
        if(!(expType instanceof TYPE_INT))
        {
            System.out.format(">> ERROR(%d) array size must be an integer\n",line);
            printError(line);
        }

        else
        {
            // check if the saize of the array is a positive integer
            if((arraySize instanceof AST_EXP_INT) && ((AST_EXP_INT)arraySize).i <= 0)
            {
                System.out.format(">> ERROR(%d) array size must be a positive integer\n",line);
                printError(line);
            }
        }

        // return the type of elements
        return type;
    }

    public TEMP IRme()
	{
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
        // IRme the size of the array
        TEMP size = arraySize.IRme();
        IR.getInstance().Add_IRcommand(new IRcommand_new_Array_Alloc(dst, size, IR.getInstance().currLine));
        return dst;
	}
}
