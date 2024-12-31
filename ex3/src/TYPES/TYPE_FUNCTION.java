package TYPES;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TYPE_LIST params;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}

	public boolean isSameSignature(TYPE_FUNCTION func1, TYPE_FUNCTION func2) {
		// return type check
		if (!func1.returnType.equals(func2.returnType)) {
			return false;
		}

		// name check
		if (!func1.name.equals(func2.name)) {
			return false;
		}

		// params check
		TYPE_LIST p1 = func1.params;
		TYPE_LIST p2 = func2.params;
		while (p1 != null && p2 != null) {
			if (!p1.head.equals(p2.head) && p1.head != null && p2.head != null) {
				return false;
			}
			p1 = p1.tail;
			p2 = p2.tail;
		}

		if (p1 != null || p2 != null) {
			return false;
		}
		
		return true;
	}

}
