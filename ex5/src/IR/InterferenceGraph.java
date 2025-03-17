package IR;

import java.util.*;
import java.io.*;

public class InterferenceGraph {
    public Map<String, Set<String>> adjList; // Adjacency list representation
    private int numRegisters;

    public InterferenceGraph() {
        this.adjList = new HashMap<>();
        this.numRegisters = 10; // MIPS has 10 registers (0-9) for allocation
    }

    public void buildInterferenceGraph() {
        // Start from the head of the IR commands
        IRcommand current = IR.getInstance().head;
        IRcommandList it = IR.getInstance().tail; // Iterator for the tail list

        while (current != null) {
            // Process live-in set
            for (String var1 : current.inSet) {
                this.addVariable(var1);
                for (String var2 : current.inSet) {
                    if (!var1.equals(var2)) {
                        this.addInterference(var1, var2);
                    }
                }
            }

            // Process live-out set
            for (String var1 : current.outSet) {
                this.addVariable(var1);
                for (String var2 : current.outSet) {
                    if (!var1.equals(var2)) {
                        this.addInterference(var1, var2);
                    }
                }
            }

            // Move to the next instruction
            if (it != null) {
                current = it.head; // Get the head of the next list node
                it = it.tail;      // Move iterator forward
            } else {
                current = null;
            }
        }

        this.writeGraphToFile("interference_graph.dot");
    }



    public void addVariable(String var) {
        adjList.putIfAbsent(var, new HashSet<>());
    }

    public void addInterference(String var1, String var2) {
        if (!var1.equals(var2)) {
            adjList.computeIfAbsent(var1, k -> new HashSet<>()).add(var2);
            adjList.computeIfAbsent(var2, k -> new HashSet<>()).add(var1);
        }
    }

    public Set<String> getInterferences(String var) {
        return adjList.getOrDefault(var, Collections.emptySet());
    }

    public void removeVariable(String var) {
        if (adjList.containsKey(var)) {
            for (String neighbor : adjList.get(var)) {
                adjList.get(neighbor).remove(var);
            }
            adjList.remove(var);
        }
    }

    public int getDegree(String var) {
        return adjList.getOrDefault(var, Collections.emptySet()).size();
    }

    public void printGraph() {
        adjList.forEach((key, value) -> System.out.println(key + " -> " + value));
    }

    public List<String> getVariablesSortedByDegree() {
        List<String> vars = new ArrayList<>(adjList.keySet());
        vars.sort(Comparator.comparingInt(this::getDegree).reversed());
        return vars;
    }

    public Map<String, Integer> allocateRegisters() {
        Stack<String> stack = new Stack<>();
        Map<String, Integer> colorMap = new HashMap<>();
        List<String> vars = new ArrayList<>(adjList.keySet());
        Map<String, Set<String>> tempAdjList = new HashMap<>();

        // Make a deep copy of adjList to avoid modifying the original structure
        for (String var : adjList.keySet()) {
            tempAdjList.put(var, new HashSet<>(adjList.get(var)));
        }

        while (!vars.isEmpty()) {
            boolean removed = false;
            Iterator<String> iterator = vars.iterator();
            while (iterator.hasNext()) {
                String var = iterator.next();
                if (tempAdjList.getOrDefault(var, Collections.emptySet()).size() < numRegisters) {
                    stack.push(var);
                    
                    // Remove var from the adjacency list safely
                    for (String neighbor : tempAdjList.getOrDefault(var, new HashSet<>())) {
                        tempAdjList.get(neighbor).remove(var);
                    }
                    tempAdjList.remove(var);
                    
                    iterator.remove();
                    removed = true;
                }
            }
            if (!removed) {
                System.err.println("Register Allocation Failed: Not enough registers available.");
                return Collections.emptyMap();
            }
        }

        while (!stack.isEmpty()) {
            String var = stack.pop();
            Set<Integer> neighborColors = new HashSet<>();

            for (String neighbor : adjList.getOrDefault(var, Collections.emptySet())) {
                if (colorMap.containsKey(neighbor)) {
                    neighborColors.add(colorMap.get(neighbor));
                }
            }

            int color = 0;
            while (neighborColors.contains(color)) {
                color++;
            }

            if (color >= numRegisters) {
                System.err.println("Register Allocation Failed: Not enough registers available.");
                return Collections.emptyMap();
            }

            colorMap.put(var, color);
        }

        return colorMap;
    }


    public String toDOTFormat() {
        StringBuilder dotBuilder = new StringBuilder("graph G {\n");
        adjList.forEach((node, neighbors) -> neighbors.stream()
                .filter(neighbor -> node.compareTo(neighbor) < 0)
                .forEach(neighbor -> dotBuilder.append("  \"").append(node)
                        .append("\" -- \"").append(neighbor).append("\";\n")));
        dotBuilder.append("}\n");
        return dotBuilder.toString();
    }

    public void writeGraphToFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(toDOTFormat());
            System.out.println("Graph written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
