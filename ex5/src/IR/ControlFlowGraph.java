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


    public void computeLiveness() {
        printControlGraph(true);  // Print the control graph for debugging

        // Use TreeSet to store the commands, sorted by line number in descending order
        TreeSet<IRcommand> worklist = new TreeSet<>(new Comparator<IRcommand>() {
            public int compare(IRcommand cmd1, IRcommand cmd2) {
                // Compare commands based on line number in descending order
                return Integer.compare(cmd2.lineNumber, cmd1.lineNumber);
            }
        });

        // Set to track commands we've already processed
        Set<IRcommand> processedCommands = new HashSet<>();

        // Step 1: Add the last command to the worklist
        worklist.add(this.tail);

        // Step 2: Process the worklist
        while (!worklist.isEmpty() && !(worklist.first() instanceof IRcommand_DecFunction)) {
            IRcommand cmd = worklist.pollFirst();  // Get the highest line command (first in TreeSet)

            // Backup old values
            Set<String> oldIn = new HashSet<>(cmd.inSet);

            // Compute OUT[n]
            cmd.inSet.clear();
            for (IRcommand succ : cmd.next) {
                cmd.inSet.addAll(succ.outSet);
            }

            // Compute IN[n]
            cmd.outSet.clear();
            cmd.outSet.addAll(cmd.use);
            Set<String> tempIn = new HashSet<>(cmd.inSet);
            tempIn.removeAll(cmd.def);
            cmd.outSet.addAll(tempIn);

            // Debug print to show IN and OUT sets
            System.out.println("Processing line " + cmd.lineNumber);
            System.out.println("IN: " + cmd.inSet);
            System.out.println("OUT: " + cmd.outSet);

            // If the command is not processed, add it to processedCommands
            if (!processedCommands.contains(cmd)) {
                processedCommands.add(cmd);
                // Always add predecessors to worklist
                for (IRcommand pred : cmd.prev) {
                    worklist.add(pred);  // Add predecessor to worklist
                }
            }
            // If the command is processed, check if IN has changed and add its predecessors if needed
            else if (!cmd.inSet.equals(oldIn)) {
                // If IN changed, add predecessors to the worklist
                for (IRcommand pred : cmd.prev) {
                    worklist.add(pred);  // Add predecessor to worklist
                }
            }
        }
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