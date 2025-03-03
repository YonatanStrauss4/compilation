package TYPES;

public class TYPE_CLASS_VAR_DEC_LIST
{
	public TYPE_CLASS_VAR_DEC head;
	public TYPE_CLASS_VAR_DEC_LIST tail;
	
	public TYPE_CLASS_VAR_DEC_LIST(TYPE_CLASS_VAR_DEC head,TYPE_CLASS_VAR_DEC_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}	

	// insert at the end of the type list
	public void insertAtEnd(TYPE_CLASS_VAR_DEC newType) {
        // If the newType is null, do nothing
        if (newType == null) {
            return;
        }
    
        // If the list is empty, set the head to the new TYPE
        if (this.head == null) {
            this.head = newType;
            return;
        }
    
        // Traverse to the end of the list
        TYPE_CLASS_VAR_DEC_LIST current = this;
        while (current.tail != null) {
            current = current.tail;
        }
    
        // Insert the new TYPE at the end
        current.tail = new TYPE_CLASS_VAR_DEC_LIST(newType, null);
    }

    // Clear the list
    public void clear() {
        // Set head and tail to null, effectively clearing the list
        this.head = null;
        this.tail = null;
    }

    public TYPE search(String name) {
        TYPE_CLASS_VAR_DEC_LIST current = this;
        while (current != null) {
            if (current.head != null && current.head.name.equals(name)) { 
                return current.head.type;
            }
            current = current.tail;
        }
        return null;
    }

}