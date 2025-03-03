package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;


public class AST_TYPE_ID extends AST_TYPE
{

    public String typeName;
    public int line;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_TYPE_ID(String typeName, int line)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== type -> ID( %s )\n", typeName);

        /*******************************/
        /* COPY INPUT DATA MEMBERS ... */
        /*******************************/
        this.typeName = typeName;
        this.line = line;
    }

    public void PrintMe()
    {
        /*********************************/
        /* AST NODE TYPE = TYPE ID */
        /*********************************/
        System.out.println("AST NODE TYPE ID");

        /******************************************/
        /* PRINT type name */
        /******************************************/
        System.out.format("TYPE NAME( %s )\n", typeName);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("TYPE\nID\n...->%s", typeName));
    }

    public TYPE SemantMe()
    {
        // search if the type is a class or array that has been defiend before

        TYPE t = SYMBOL_TABLE.getInstance().find(typeName);

        //check also if maybe we are currently in the class that we are using as type for the variable
        TYPE currClass = SYMBOL_TABLE.getInstance().get_current_class();
        if ((t == null || !((t != null && !(t instanceof TYPE_CLASS)) || (t != null && !(t instanceof TYPE_ARRAY)))) && currClass != null && !currClass.name.equals(typeName))
        {
            System.out.format(">> ERROR(%d) type not defined: %s\n",line,typeName);
            printError(line);
        }
        
        if (t == null){
            return currClass;
        }
        return t;
    }

    public String getTypeName(){
        return typeName;
    }
}
