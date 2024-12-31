package TYPES;

public class TYPE_LIST extends TYPE
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public TYPE_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}

	public TYPE search(String name){
		TYPE_LIST current = this;
		while(current != null){
			if(current.head != null && current.head.name.equals(name)){
				return current.head;
			}
			current = current.tail;
		}
		return null;
	}

public boolean checkArgumentsList(TYPE_LIST params) {
    TYPE_LIST current = this;
    TYPE_LIST other = params;

    // Traverse both lists simultaneously
    while (current != null && other != null) {
        // Check if either head is null (incomplete TYPE_LIST)
        if (current.head == null || other.head == null) {
            return false;
        }

        // Handle TYPE_CLASS compatibility
        if (current.head instanceof TYPE_CLASS && other.head instanceof TYPE_CLASS) {
            if (!current.head.equals(other.head) && !((TYPE_CLASS)current.head).checkIfInherit((TYPE_CLASS)other.head)) {
                return false; // Mismatch found
            }
        } 

        // Handle nil compatibility
        if (current.head instanceof TYPE_NIL) {
            if (!(other.head instanceof TYPE_ARRAY || other.head instanceof TYPE_CLASS ||other.head instanceof TYPE_NIL)) {
                return false; // `nil` is only compatible with nil, array, and class
            }
        }

        // Handle primitive types and arrays
        if(!(current.head.equals(other.head))){
            if((current.head instanceof TYPE_INT && !(other.head instanceof TYPE_INT)) || (other.head instanceof TYPE_INT && !(current.head instanceof TYPE_INT))){
                return false;
            }

            if((current.head instanceof TYPE_STRING && !(other.head instanceof TYPE_STRING)) || (other.head instanceof TYPE_STRING && !(current.head instanceof TYPE_STRING))){
                return false;
            }

            if((current.head instanceof TYPE_ARRAY && !(other.head instanceof TYPE_ARRAY)) || (other.head instanceof TYPE_ARRAY && !(current.head instanceof TYPE_ARRAY))){
                return false;
            }
        }

            // Move to the next elements in both lists
            current = current.tail;
            other = other.tail;
        }

        // Both lists must be fully traversed (same length)
        return current == null && other == null;
    }
}


