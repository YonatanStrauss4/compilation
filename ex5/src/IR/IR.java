/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IR {

	private IRcommand head = null;
	private IRcommandList tail = null;
	public ControlFlowGraph controlGraph = new ControlFlowGraph();
	public int currLine = 0;

	/*
	 * Adds a new IR command to the list. Initializes the list if empty, appends to the end if not,
	 * updates the control graph with the new command, and increments the command line counter.
	 */
	public void Add_IRcommand(IRcommand cmd) {
		if ((head == null) && (tail == null))
		{
			this.head = cmd;
		}
		else if ((head != null) && (tail == null))
		{
			this.tail = new IRcommandList(cmd,null);
		}
		else
		{
			IRcommandList it = tail;
			while ((it != null) && (it.tail != null))
			{
				it = it.tail;
			}
			it.tail = new IRcommandList(cmd,null);
		}
		// Add the command to the control graph for dataflow analysis or graph representation
		this.controlGraph.addControlNode(cmd);
		this.currLine++;

	}

	public IRcommand findLastDecFunctionCommand(){
		if (head instanceof IRcommand_DecFunction) {
			return head;
		}
		IRcommandList it = tail;
		while(it != null){
			if(it.head instanceof IRcommand_DecFunction){
				return it.head;
			}
			it = it.tail;
		}
		return null;
	}
	
	// USUAL SINGLETON IMPLEMENTATION ...
	private static IR instance = null;

	// PREVENT INSTANTIATION ...
	protected IR() {}

	// GET SINGLETON INSTANCE ...
	public static IR getInstance()
	{
		if (instance == null)
		{
			// [0] The instance itself ...
			instance = new IR();
		}
		return instance;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (head != null) head.MIPSme();
		if (tail != null) tail.MIPSme();
	}
}
