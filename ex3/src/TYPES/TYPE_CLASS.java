package TYPES;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_LIST data_members;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_LIST data_members)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
	}

	public TYPE_FUNCTION findFunction(String name){
		TYPE_CLASS current = this;
		if(current.data_members == null){
			return null;
		}
		TYPE func = current.data_members.search(name);
		if(func != null && ((TYPE_CLASS_FUNC_DEC)func).type instanceof TYPE_FUNCTION){
			return (TYPE_FUNCTION)((TYPE_CLASS_FUNC_DEC)func).type;
		}
		return null;
	}

	public TYPE_CLASS_VAR_DEC findVariable(String name){
		TYPE_CLASS current = this;
		if(current.data_members == null){
			return null;
		}
		TYPE var = current.data_members.search(name);
		if(var != null && var instanceof TYPE_CLASS_VAR_DEC){
			return (TYPE_CLASS_VAR_DEC)var;
		}
		return null;
	}

	public boolean checkIfInherit(TYPE t2){
		if(!(t2 instanceof TYPE_CLASS)){
			return false;
		}
		TYPE_CLASS curr_father = this;
		while(curr_father != null){
			if(curr_father.name.equals(t2.name)){
				return true;
			}
			curr_father = curr_father.father;
		}
		return false;
	}

	public TYPE findFunctionInInheritanceTree(String name){
		TYPE_CLASS curr_father = this;
		while(curr_father != null){
			TYPE_FUNCTION func = curr_father.findFunction(name);
			if(func != null){
				return (TYPE_FUNCTION)func;
			}
			curr_father = curr_father.father;
		}
	return null;
	}

	public TYPE findVariableInInheritanceTree(String name){
		TYPE_CLASS curr_father = this;
		while(curr_father != null){
			TYPE_CLASS_VAR_DEC var = curr_father.findVariable(name);
			if(var != null){
				return ((TYPE_CLASS_VAR_DEC)var).type;
			}
			curr_father = curr_father.father;
		}
	return null;
	}
	
}
