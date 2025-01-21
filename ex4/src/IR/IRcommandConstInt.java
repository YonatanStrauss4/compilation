package IR;
import java.util.*;
import TEMP.*;

public class IRcommandConstInt extends IRcommand {
	
	TEMP t;
	int value;
	int lineNumber;
	String tLabeled;
	
	public IRcommandConstInt(TEMP t,int value,int line) {
		this.lineNumber = line;
		this.t = t;
		this.value = value;
		this.tLabeled = t.toString()+";"+line;
	}

	public Set<String> genVars() {
		return new HashSet<>(Arrays.asList(tLabeled));
	}

	public Set<String> killVars(int maxLine) {
		List<String> arr = new ArrayList<>();
		for (int j=0;j<maxLine;j++) {
			if (j != this.lineNumber) {
				arr.add(t.toString()+";"+j);
			}
		}
		arr.add(t.toString()+";?");
		return new HashSet<>(arr);
	}

	public void printIR() {
		System.out.println(t.toString() + " := " + String.format("%d", value));
	}
}
