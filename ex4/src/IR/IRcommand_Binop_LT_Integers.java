package IR;
import java.util.*;
import TEMP.*;

public class IRcommand_Binop_LT_Integers extends IRcommand {
	
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	int lineNumber;
	String dstLabeled;

	public IRcommand_Binop_LT_Integers(TEMP dst,TEMP t1,TEMP t2,int line) {
		this.isBinop = true;
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
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
		System.out.println(dst.toString() + " := " + t1.toString() + " < " + t2.toString());
	}
}
