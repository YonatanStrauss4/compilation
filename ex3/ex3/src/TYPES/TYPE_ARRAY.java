package TYPES;

public class TYPE_ARRAY extends TYPE
{

    /****************/
    /* DATA MEMBERS */
    /****************/
    public TYPE type;
    public String name;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public TYPE_ARRAY(TYPE type, String name)
    {
        this.type = type;
        this.name = name;
    }
}