package AST;
import TYPES.*;

public abstract class AST_VAR_DEC extends AST_Node
{
   public TYPE SemantMe() {
        return null;
    }

    public TEMP IRme() {
		IR.getInstance().Add_IRcommand(new IRcommand_Allocate(name));
		
		if (initialValue != null)
		{
			IR.getInstance().Add_IRcommand(new IRcommand_Store(name,initialValue.IRme()));
		}
		return null;
	}

}
