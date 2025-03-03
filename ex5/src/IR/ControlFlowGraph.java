package IR;
import java.util.*;

public class ControlFlowGraph {

    public IRcommand head;
    public IRcommand tail;
    int maxLine;

    public ControlFlowGraph() {
        this.head = null;
        this.tail = null;
        this.maxLine = 0;
    }

    /**
     * Adds a control node to the graph.
     * 
     * @param cmd The IRcommand to add.
     * 
     * If the graph is empty, set as head and tail. Otherwise, add to the end.
     * Increment maxLine and update cmd's kill/gen sets.
     */
    public void addControlNode(IRcommand cmd) {
        if (this.head == null) {
            this.head = cmd;
            this.tail = cmd;
        } else {
            this.tail.next.add(cmd);
            cmd.prev.add(this.tail);
            this.tail = cmd;
        }
        this.maxLine++;
    }
    
    public void update_CFG(IRcommand src, IRcommand trgt) {
		src.next.add(trgt);
        trgt.prev.add(src);
	}

    public void printControlGraph(boolean withNextCmds) {
        IRcommand curr = this.head;
        int j = 0;
		while (curr != null) {
            if (withNextCmds) {
                System.out.println("------------------------");
            }
            System.out.print(j+": ");
    		curr.printIR();
			// Ensure curr.next is not null and contains elements
			if (curr.next != null && !curr.next.isEmpty()) {
                if (withNextCmds) {
                    System.out.println("Connected to -> : ");
                    for (int i=0;i<curr.next.size();i++) {
					    curr.next.get(i).printIR();
				    }
                }
                // Move to the next node
                curr = curr.next.isEmpty() ? null : curr.next.get(0);
                j++;
			} else {
				break; // Exit the loop if there are no more elements
			}
        }
        System.out.println("=======END OF PRINTING=======");
    }

}