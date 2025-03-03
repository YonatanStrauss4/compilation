package AST;
import TYPES.*;
import TEMP.*;
import IR.*;

public abstract class AST_VAR extends AST_Node
{
   public TYPE SemantMe() {
        return null;
    }

    public TEMP IRme() {
        return null;
    }
}
