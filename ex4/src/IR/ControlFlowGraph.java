package IR;
import java.util.*;

public class ControlFlowGraph {

    public IRcommand head;
    public IRcommand tail;

    public ControlFlowGraph() {
        this.head = null;
        this.tail = null;
    }

    public void addControlNode(IRcommand cmd) {
        if (this.head == null) {
            this.head = cmd;
            this.tail = cmd;
        } else {
            this.tail.next.add(cmd);
            this.tail = cmd;
        }
        cmd.updateKillGen();
    }
    
    public void update_CFG(IRcommand src, IRcommand trgt) {
		src.next.add(trgt);
	}

    public void computeInOut() {
        Set<IRcommand> worklist = new HashSet<>();
        IRcommand curr = this.head;
        while (curr != null) {
            worklist.add(curr);
            curr = curr.next.isEmpty() ? null : curr.next.get(0);
        }
        while (!worklist.isEmpty()) {
            IRcommand cmd = worklist.iterator().next();
            worklist.remove(cmd);
            
            // compute new out set
            Set<String> new_out = new HashSet<>();
            for (IRcommand next : cmd.next) {
                new_out.addAll(next.in);
            }

            // compute new in set
            Set<String> new_in = new HashSet<>(cmd.gen);
            Set<String> temp = new HashSet<>(new_out);
            temp.removeAll(cmd.kill);
            new_in.addAll(temp);

            // check if in/out sets changed
            if (!new_in.equals(cmd.in) || !new_out.equals(cmd.out)) {
                cmd.in = new_in;
                cmd.out = new_out;

                // add all predecessors to worklist
                for (IRcommand next : cmd.next) {
                    worklist.add(next);
                }
            }

        }
    }

    public void performUseBeforeDefAnalysis() {
        // Ensure in/out sets are computed
        computeInOut();
    
        IRcommand curr = this.head;
        while (curr != null) {
            // Check for use-before-def errors
            System.out.print("------------------------");
            curr.printIR();
            for (String temp : curr.gen) {
                if (!curr.in.contains(temp)) {
                    System.out.println("Use-before-def error: " + temp + " in command:");
                    curr.printIR();
                } else {
                    System.out.println("no error in: " + temp);
                    System.out.println("curr.in contains:");
                    if (!curr.in.isEmpty()){
                        for (String temp1 : curr.in) {
                            System.out.println(temp1);
                        }
                    } else {
                        System.out.println("curr.in is empty");
                    }
                }
            }
    
            // Move to the next node
            curr = curr.next.isEmpty() ? null : curr.next.get(0);
        }
    }

    public void printControlGraph() {
        IRcommand curr = this.head;
		while (curr != null) {
    		curr.printIR();
			// Ensure curr.next is not null and contains elements
			if (curr.next != null && !curr.next.isEmpty()) {
				System.out.println("Connected to -> : ");
				for (int i=0;i<curr.next.size();i++){
					curr.next.get(i).printIR();
				}
				System.out.println("");
				curr = curr.next.get(0); // Move to the next element
			} else {
				break; // Exit the loop if there are no more elements
			}
        }
    }   
}