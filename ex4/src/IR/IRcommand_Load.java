
package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Load extends IRcommand {

	TEMP dst;
	String var_name;
	int lineNumber;
	String dstLabeled;
	
	public IRcommand_Load(TEMP dst,String var_name,int line) {
		this.dst      = dst;
		this.var_name = var_name;
		this.lineNumber = line;
		this.dstLabeled = dst.toString()+";"+line;
	}

	public Set<String> genVars() {
		return new HashSet<>(Arrays.asList(dstLabeled));
	}

	public Set<String> killVars(int maxLine) {
		List<String> arr = new ArrayList<>();
		for (int j=0;j<maxLine;j++) {
			if (j != this.lineNumber) {
				arr.add(dst.toString()+";"+j);
			}
		}
		arr.add(dst.toString()+";?");
		return new HashSet<>(arr);
	}

	public void printIR() {
		System.out.println(String.format("%s := load(%s)", dst.toString(), var_name));
	}
}
