package IR;
import TEMP.*;

public class IRcommand_Return extends IRcommand {
    public TEMP return_value;
    
    public IRcommand_Return(TEMP return_value) {
        this.return_value = return_value;
    }

    public void printIR() {
        System.out.println("return " + return_value.toString());
    }
}
