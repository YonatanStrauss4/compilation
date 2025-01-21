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
        cmd.updateKillGen(this.maxLine);
    }
    
    public void update_CFG(IRcommand src, IRcommand trgt) {
		src.next.add(trgt);
        trgt.prev.add(src);
	}

    /**
     * Performs a use-before-definition analysis on the control flow graph.
     * This method identifies variables that are used before being defined within the intermediate representation (IR) commands.
     * It processes the commands in the control flow graph, computes 'in' and 'out' sets for each command, and checks for use-before-definition errors.
     * 
     * @return A set of variable names that are used before being defined.
     */
    public Set<String> performUseBeforeDefAnalysis() {
        // Initialize the worklist to process IR commands and a set to store errors
        List<IRcommand> worklist = new ArrayList<>();
        Set<String> errors = new HashSet<>();
        
        // Start from the head of the command list and initialize a set of variables
        IRcommand curr = this.head;
        Set<String> vars = getVaris();
        boolean firstFlag = true;

        // Populate the worklist with all commands in the control flow graph
        while (curr != null) {
            worklist.add(curr);
            curr = curr.next.isEmpty() ? null : curr.next.get(0);
        }

        // Iterate through the worklist until it is empty
        while (!worklist.isEmpty()) {
            IRcommand cmd = worklist.get(0);
            worklist.remove(0);
            
            // compute new in set
            Set<String> new_in = new HashSet<>();
            if (isScopeEnder(cmd)) {
                // If the command ends a scope, retrieve the scope starter's 'out' set
                IRcommand scopeStarter = getScopeStarter(cmd);
                if (scopeStarter != null) {
                    if (!scopeStarter.prev.isEmpty()) {
                        new_in.addAll(scopeStarter.prev.get(0).out);
                    }
                }
            } else {
                // Otherwise, accumulate 'out' sets from predecessors
                for (IRcommand currCmd : cmd.prev){
                new_in.addAll(currCmd.out);
                }
            }
            if (firstFlag) {
                // Add all variables to 'in' set on the first iteration
                new_in.addAll(vars);
                firstFlag = false;
            }

            // Compute the new 'out' set: (in - kill) union gen
            Set<String> new_out = new HashSet<>(new_in);
            new_out.removeAll(cmd.kill);
            new_out.addAll(cmd.gen);

            // Check if the 'in' set contains any variables that are in the 'out' set
            if (cmd instanceof IRcommand_Load) {
                // For load commands, check if the variable is uninitialized
                IRcommand_Load loadCmd = (IRcommand_Load) cmd;
                if (new_in.contains(loadCmd.var_name+";?")) {
                    new_out.add(loadCmd.dst.toString()+";?");
                    errors.add(loadCmd.var_name);
                }
            } 
            
            if (cmd instanceof IRcommand_Store) {
                // For store commands, handle initialization checks
                IRcommand_Store storeCmd = (IRcommand_Store) cmd;
                if (new_in.contains(storeCmd.src.toString()+";?")) {
                    new_out.add(storeCmd.var_name+";?");
                }
            }

            if (cmd instanceof IRcommand_Allocate) {
                // For allocation commands, mark the variable as uninitialized
                IRcommand_Allocate allocCmd = (IRcommand_Allocate) cmd;
                new_out.add(allocCmd.var_name+";?");
            }

            if (cmd.isBinop) {
                // For binary operations, check if the destination variable is uninitialized
                String addToOut = getBinopDstLabeled(cmd, new_in);
                if (addToOut != null) {
                    new_out.add(addToOut);
                }
            }

            // check if in/out sets changed
            if (!new_in.equals(cmd.in) || !new_out.equals(cmd.out)) {
                cmd.in = new_in;
                cmd.out = new_out;

                // add all successors to worklist
                for (IRcommand next : cmd.next) {
                    worklist.add(next);
                }
            }
        }
        return errors;
    }

    /**
     * Retrieves a set of variable names from the control flow graph.
     * 
     * This method traverses the control flow graph starting from the head node,
     * collecting variable names from the 'gen' set of each IRcommand. It also
     * includes variable names from IRcommand_Allocate commands. Each variable
     * name is appended with a ";?" suffix before being added to the set.
     * 
     * @return A set of variable names with the ";?" suffix.
     */
    public Set<String> getVaris() {
        IRcommand curr = this.head;
        Set<String> vars = new HashSet<>();

        // Traverse the control flow graph and collect variable names
        while (curr != null) {
            // Collect variables from the 'gen' set of the current command
            for (String vari : curr.gen) {
                String variable = vari.split(";")[0];
                vars.add(variable+";?");
            }
            // If the command is an 'Allocate' command, handle it specifically
            if (curr instanceof IRcommand_Allocate) {
                IRcommand_Allocate allocateCmd = (IRcommand_Allocate) curr;
                vars.add(allocateCmd.var_name+";?");
            }
            // Move to the next node
            curr = curr.next.isEmpty() ? null : curr.next.get(0);
        }
        clearInOut();
        return vars;
    }


    /**
     * Clears the 'in' and 'out' sets for each IRcommand node in the control flow graph.
     * This method iterates through the linked list of IRcommand nodes starting from the head,
     * and for each node, it initializes the 'in' and 'out' sets to empty HashSet instances.
     */
    public void clearInOut() {
        IRcommand curr = this.head;
        // Traverse the control flow graph and clear the 'in' and 'out' sets
        while (curr != null) {
            curr.in = new HashSet<String>();
            curr.out = new HashSet<String>();
            // Move to the next node
            curr = curr.next.isEmpty() ? null : curr.next.get(0);
        }
    }

    /**
     * This method determines the destination label for a given binary operation command.
     * It checks if the operands of the command are present in the provided set of strings (`new_in`).
     * If either operand is found in the set, it returns the destination of the command with a ";?" suffix.
     * Otherwise, it returns null.
     *
     * @param cmd The binary operation command to be checked.
     * @param new_in A set of strings representing the operands to be checked against.
     * @return The destination label with a ";?" suffix if either operand is found in the set, otherwise null.
     */
    public String getBinopDstLabeled(IRcommand cmd, Set<String> new_in) {
        if (cmd instanceof IRcommand_Binop_Add_Integers) {
            IRcommand_Binop_Add_Integers castCmd = (IRcommand_Binop_Add_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else if (cmd instanceof IRcommand_Binop_Add_Strings) {
            IRcommand_Binop_Add_Strings castCmd = (IRcommand_Binop_Add_Strings) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else if (cmd instanceof IRcommand_Binop_Div_Integers) {
            IRcommand_Binop_Div_Integers castCmd = (IRcommand_Binop_Div_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else if (cmd instanceof IRcommand_Binop_EQ_Integers) {
            IRcommand_Binop_EQ_Integers castCmd = (IRcommand_Binop_EQ_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else if (cmd instanceof IRcommand_Binop_LT_Integers) {
            IRcommand_Binop_LT_Integers castCmd = (IRcommand_Binop_LT_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        }  else if (cmd instanceof IRcommand_Binop_Mul_Integers) {
            IRcommand_Binop_Mul_Integers castCmd = (IRcommand_Binop_Mul_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else if (cmd instanceof IRcommand_Binop_Sub_Integers) {
            IRcommand_Binop_Sub_Integers castCmd = (IRcommand_Binop_Sub_Integers) cmd;
            // Check if either operand (t1 or t2) is uninitialized
            if (new_in.contains(castCmd.t1.toString()+";?") || (new_in.contains(castCmd.t2.toString()+";?"))) {
                return (castCmd.dst.toString()+";?");
            }
            return null;
        } else {
            return null;
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

    /**
     * Determines if the given IR command signifies the end of a scope.
     *
     * @param cmd the IR command to check
     * @return true if the command is an instance of IRcommand_Label and does not mark the start of a scope, false otherwise
     */
    public boolean isScopeEnder(IRcommand cmd) {
        if (cmd instanceof IRcommand_Label) {
            IRcommand_Label lblCmd = (IRcommand_Label) cmd;
            if (!lblCmd.isScopeStart) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the starting label of the scope for a given IR command.
     *
     * This method takes an IR command, casts it to an IRcommand_Label, and then
     * traverses the previous commands to find the corresponding starting label
     * of the scope. The starting label is identified by checking if the label
     * name matches the expected pattern of the next label in the sequence.
     *
     * @param cmd The IR command for which the scope starter label is to be found.
     * @return The IRcommand_Label that represents the starting label of the scope,
     *         or null if no matching starting label is found.
     */
    public IRcommand getScopeStarter(IRcommand cmd) {
        IRcommand_Label castCmd = (IRcommand_Label) cmd;
        IRcommand curr = castCmd;
        int labelNum = Integer.parseInt(castCmd.label_name.split("_")[1]);
        String labelStrPlusOne = ("Label_"+(labelNum+1)+"_"+castCmd.label_name.split("_\\d+_")[1]);
        while (curr != null) {
            if (curr instanceof IRcommand_Label) {
                IRcommand_Label currLbl = (IRcommand_Label) curr;
                if (labelStrPlusOne.split("_end")[0].equals(currLbl.label_name.split("_start")[0])) {
                    return currLbl;
                }
            }
            curr = curr.prev.isEmpty() ? null : curr.prev.get(0);
        }
        return null;
    }
}