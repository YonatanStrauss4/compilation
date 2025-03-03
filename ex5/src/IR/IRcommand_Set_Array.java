package IR;
import java.util.*;
import TEMP.*;
import MIPS.*;

public class IRcommand_Set_Array extends IRcommand {
    TEMP array;
    TEMP index;
    TEMP value;
    int line;

    public IRcommand_Set_Array(TEMP array, TEMP index, TEMP value, int line) {
        this.array = array;
        this.index = index;
        this.value = value;
        this.line = line;
    }

	public void printIR() {
        System.out.println("array set: " + array.toString() + " " + index.toString() + " " + value.toString());
	}

    public void MIPSme(){
        MIPSGenerator.getInstance().set_array(array, index, value);
    }


}