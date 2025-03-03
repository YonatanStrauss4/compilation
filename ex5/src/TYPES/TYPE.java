package TYPES;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	/*************/
	/* isClass() */
	/*************/
	public boolean isClass(){ return this instanceof TYPE_CLASS;}

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray(){ return this instanceof TYPE_ARRAY;}
	

	public boolean equals(TYPE t){
		if (t == null || this.name == null || t.name == null)
		{
        	return false;
    	}
		return this.name.equals(t.name);
	}


}
