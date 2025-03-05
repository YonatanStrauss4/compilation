/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.*;
import java.util.*;
import IR.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class MIPSGenerator
{
	private int WORD_SIZE = 4;
    private PrintWriter fileWriter;
    private PrintWriter dataWriter;
    private static final String DIRNAME = "./output/";
    private static final String MIPS_FILENAME = "MIPS.txt";
    private static final String DATA_FILENAME = "MIPS_data.txt";


	// This function is called when the file is done being written
	public void finalizeFile()
	{
		dataWriter.print("\n");
		dataWriter.print(".text\n");
		dataWriter.print("\n");

		dataWriter.format("\n");
		dataWriter.format("\tabort_access_violation:\n");
		dataWriter.format("\tla $s0, string_access_violation\n");
		dataWriter.format("\tlw $s0, 0($s0)\n");
		dataWriter.format("\n");

		dataWriter.format("\t# inline implementation of PrintString:\n");
		dataWriter.format("\tmove $a0, $s0\n");
		dataWriter.format("\tli $v0, 4\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\tli $v0, 10\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\n");

		dataWriter.format("\tabort_illegal_division_by_0:\n");
		dataWriter.format("\tla $s0, string_illegal_div_by_0\n");
		dataWriter.format("\tlw $s0, 0($s0)\n");
		dataWriter.format("\n");

		dataWriter.format("\t# inline implementation of PrintString:\n");
		dataWriter.format("\tmove $a0, $s0\n");
		dataWriter.format("\tli $v0, 4\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\tli $v0, 10\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\n");

		dataWriter.format("\tabort_invalid_ptr_dref:\n");
		dataWriter.format("\tla $s0, string_invalid_ptr_dref\n");
		dataWriter.format("\tlw $s0, 0($s0)\n");
		dataWriter.format("\n");

		dataWriter.format("\t# inline implementation of PrintString:\n");
		dataWriter.format("\tmove $a0, $s0\n");
		dataWriter.format("\tli $v0, 4\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\tli $v0, 10\n");
		dataWriter.format("\tsyscall\n");
		dataWriter.format("\n");

		dataWriter.close();

		fileWriter.println();
		fileWriter.println("main:");
		fileWriter.print("\tjal user_main\n");
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();

        mergeFiles();
	}

	// checking equality between two strings
	public void string_equality(TEMP t1, TEMP t2, TEMP dst){
		int idx1 = t1.getSerialNumber();
		int idx2 = t2.getSerialNumber();
		int idxdst = dst.getSerialNumber();
		fileWriter.format("\n");
		fileWriter.format("\t # inline implementation of string equality:\n");
		fileWriter.format("\tli $t%d, 1\n", idxdst);
		fileWriter.format("\tmove $s0, $t%d\n", idx1);
		fileWriter.format("\tmove $s1, $t%d\n", idx2);
		fileWriter.format("str_eq_loop:\n");
		fileWriter.format("\tlb $s2, 0($s0)\n");
		fileWriter.format("\tlb $s3, 0($s1)\n");
		fileWriter.format("\tbne $s2, $s3, neq_label\n");
		fileWriter.format("\tbeq $s2, 0, str_eq_end\n");
		fileWriter.format("\taddu $s0, $s0, 1\n");
		fileWriter.format("\taddu $s1, $s1, 1\n");
		fileWriter.format("\tj str_eq_loop\n");
		fileWriter.format("neq_label:\n");
		fileWriter.format("\tli $t%d, 0\n", idxdst);
		fileWriter.format("str_eq_end:\n");
		fileWriter.format("\n");
	}
	
	// finalizing a class
	public void finiliazeClass(String name){
		dataWriter.format("\n");
		dataWriter.format("vt_%s:\n", name);
		List<METHOD_ENTRY> methods = CLASSES_MAP.getInstance().getMethods(name);
		for (int i = 0; i < methods.size(); i++) {
			dataWriter.format("\t.word %s_%s\n", methods.get(i).className, methods.get(i).methodName);
		}
		dataWriter.format("\n");
	}

	// return
	public void ret(TEMP t){
		int idx=t.getSerialNumber();
		fileWriter.format("\tmove $v0, $t%d\n", idx);
	}

	// allocate global int
	public void allocate_global_int(String var_name, int value)
	{
		dataWriter.format("\tg_%s: .word %d\n" ,var_name, value);
	}

	// allocate global string
	public void allocate_global_string(String var_name, String value)
	{
		String str = value.substring(1, value.length()-1);
		dataWriter.format("\tg_%s: .word str_%s\n" ,var_name, str);
	}

	// allocate class instance
	public void allocate_class_instance(TEMP dst, String className, int numOfFields)
	{
		int idxdst = dst.getSerialNumber();
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tli $a0, %d\n", (numOfFields+1)*WORD_SIZE);
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
		fileWriter.format("\tla $s0, vt_%s\n", className);
		fileWriter.format("\tsw $s0, 0($t%d)\n", idxdst);
		List<FIELD_ENTRY> fields = CLASSES_MAP.getInstance().getFields(className);
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).type.contains("str_")) {
				fileWriter.format("\tla $s0, %s\n", fields.get(i).type);
				fileWriter.format("\tsw $s0, %d($t%d)\n", (i+1)*WORD_SIZE, idxdst);
			}
			else if (fields.get(i).type.equals("INT")) {
				fileWriter.format("\tli $s0, %d\n", fields.get(i).value);
				fileWriter.format("\tsw $s0, %d($t%d)\n", (i+1)*WORD_SIZE, idxdst);
			}
			else {
				fileWriter.format("\tli $s0, 0\n");
				fileWriter.format("\tsw $s0, %d($t%d)\n", (i+1)*WORD_SIZE, idxdst);
			}
		}
	}

	// function prologue
	public void function_prologue(String func_name, int localsOffset)
	{
		if(func_name.equals("main"))
		{
			fileWriter.format("user_main:\n");
		}
		else
		{
			fileWriter.format("%s:\n",func_name);
		}
		fileWriter.format("\t# prologue:\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $ra, 0($sp)\n");
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $fp, 0($sp)\n");
		fileWriter.format("\tmove $fp, $sp\n");	
		for (int i = 0; i < 10; i++) {
			fileWriter.format("\tsubu $sp, $sp, 4\n");
			fileWriter.format("\tsw $t%d, 0($sp)\n", i);
		}
		fileWriter.format("\tsubu $sp, $sp, %d\n",localsOffset);

		fileWriter.format("\n");
	}
	
	// function epilogue
	public void function_epilogue()
	{
		fileWriter.format("\n");
		fileWriter.format("\t# epilogue:\n");
		fileWriter.format("\tmove $sp, $fp\n");
		for (int i = 0; i < 10; i++) {
			fileWriter.format("\tlw $t%d, %d($sp)\n", i, -(i+1)*4);
		}
		fileWriter.format("\tlw $fp, 0($sp)\n");
		fileWriter.format("\tlw $ra, 4($sp)\n");
		fileWriter.format("\taddu $sp, $sp, 8\n");
		fileWriter.format("\tjr $ra\n");

		fileWriter.format("\n");
	}

	// allocate string
	public void allocate_string(String value, String label)
	{
		dataWriter.format("\t%s: .asciiz %s\n" ,label, value);
	}

	// allocate global without args
	public void allocate_global_no_args(String var_name)
	{
		dataWriter.format("\tg_%s: .word 0\n" ,var_name);
	}

	// allocate local with args
	public void allocate_local_no_args(int offset, TEMP dst)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tli $t%d, 0\n", idx);
		fileWriter.format("\tsw $t%d, %d($fp)\n", idx, offset);
	}

	// load function parameter
	public void loadParam(TEMP dst, int offset)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, offset);
	}

	// load local variable
	public void loadLocal(TEMP dst, int offset)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, offset);
	}

	// load global variable
	public void loadGlobal(TEMP dst, String var_name)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, g_%s\n", idx, var_name);
	}

	// load global string
	public void loadGlobalString(TEMP dst, String var_name)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tla $t%d, g_%s\n", idx, var_name);
		fileWriter.format("\tlw $t%d, 0($t%d)\n", idx, idx);
	}

	// load string
	public void load_string(TEMP dst, String value)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tla $t%d, %s\n", idx, value);
	}

	// store local variable
	public void storeLocal(TEMP src, int offset)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw $t%d, %d($fp)\n",idxsrc ,offset);
	}

	// store global variable
	public void storeGlobal(TEMP src, String var_name)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw $t%d, 0(g_%s)\n",idxsrc ,var_name);
	}

	// store function parameter
	public void storeParam(TEMP src)
	{
		int idxsrc = src.getSerialNumber();
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n",idxsrc);

	}

	// access class field
	public void access_field(TEMP dst, TEMP var, String fieldName, int offset)
	{
		int idxdst = dst.getSerialNumber();
		int idxvar = var.getSerialNumber();
		fileWriter.format("\tbeq $t%d, 0, abort_invalid_ptr_dref\n", idxvar);
		fileWriter.format("\tlw $t%d, %d($t%d)\n", idxdst, offset, idxvar);
	}

	// set class field
	public void set_field(TEMP classInstanceTemp, TEMP var, String name, int offset)
	{
		int idxclassInstanceTemp = classInstanceTemp.getSerialNumber();
		int idxvar = var.getSerialNumber();
		fileWriter.format("\tbeq $t%d, 0, abort_invalid_ptr_dref\n", idxclassInstanceTemp);
		fileWriter.format("\tsw $t%d, %d($t%d)\n", idxvar, offset, idxclassInstanceTemp);
	}

	//  jal function with void return
	public void jalNotVoid(TEMP dst, String func_name, int numOfArgs)
	{
		fileWriter.format("\tjal %s\n",func_name);
		fileWriter.format("\taddu $sp, $sp, %d\n", numOfArgs*WORD_SIZE);
		int idxsrc = dst.getSerialNumber();
		fileWriter.format("\tmove $t%d, $v0\n", idxsrc);
	}

	// load immediate
	public void li(TEMP t,int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli $t%d, %d\n",idx,value);
	}

	// add two temporaries
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd $t%d, $t%d, $t%d\n",dstidx,i1,i2);
	}

	// subtract two temporaries
	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// div two integers
	public void div(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tdiv $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// set less than
	public void slt(TEMP oprnd1,TEMP oprnd2, TEMP dst)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tslt $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// print integer syscall
	public void PrintIntSyscall(TEMP t)
	{
		int idx = t.getSerialNumber();
		fileWriter.format("\n");
		fileWriter.format("\t# inline implementation of PrintInt:\n");
		fileWriter.format("\tmove $a0, $t%d\n", idx);
		fileWriter.format("\tli $v0, 1\n");
		fileWriter.format("\tsyscall\n");
	}

	// print string syscall
	public void PrintStringSyscall(TEMP t)
	{
		int idx = t.getSerialNumber();
		fileWriter.format("\n");
		fileWriter.format("\t# inline implementation of PrintString:\n");
		fileWriter.format("\tmove $a0, $t%d\n", idx);
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
	}

	// malloc syscall
	public void mallocSyscall(int size)
	{
		fileWriter.format("\tli $a0, %d\n", size);
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");
	}

	// exit syscall
	public void exitSyscall()
	{
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
	}
	
	// set if equal
	public void seq(TEMP oprnd1,TEMP oprnd2,TEMP dst)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tseq $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// mul two integers
	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmul t%d, t%d ,t%d\n", dstidx, i1, i2);
	}

	// label
	public void label(String inlabel)
	{
		fileWriter.format("%s:\n",inlabel);
	}	

	// jump
	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
	}	

	// allocate new array
	public void new_array_alloc(TEMP dst,TEMP size)
	{
		int idxdst = dst.getSerialNumber();
		int idxsize = size.getSerialNumber();

		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tmove $a0, $t%d\n", idxsize);
		fileWriter.format("\tadd $a0, $a0, 1\n");
		fileWriter.format("\tmul $a0, $a0, 4\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
		fileWriter.format("\tsw $t%d, 0($t%d)\n", idxsize, idxdst);
	}

	// array access
	public void array_access(TEMP dst, TEMP arrayTemp, TEMP index)
	{
		int idxdst = dst.getSerialNumber();
		int idxarray = arrayTemp.getSerialNumber();
		int idxindex = index.getSerialNumber();

		fileWriter.format("\tbltz $t%d, abort_access_violation\n", idxindex);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxarray);
		fileWriter.format("\tbge $t%d, $s0, abort_access_violation\n", idxindex);
		fileWriter.format("\tmove $s0, $t%d\n", idxindex);
		fileWriter.format("\tadd $s0, $s0, 1\n");
		fileWriter.format("\tmul $s0, $s0, 4\n");
		fileWriter.format("\taddu $s0, $s0, $t%d\n", idxarray);
		fileWriter.format("\tlw $t%d, 0($s0)\n", idxdst);
	}

	// set an array entry
	public void set_array(TEMP array, TEMP index, TEMP value)
	{
		int idxarray = array.getSerialNumber();
		int idxindex = index.getSerialNumber();
		int idxvalue = value.getSerialNumber();

		fileWriter.format("\tbltz $t%d, abort_access_violation\n", idxindex);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxarray);
		fileWriter.format("\tbge $t%d, $s0, abort_access_violation\n", idxindex);
		fileWriter.format("\tmove $s0, $t%d\n", idxindex);
		fileWriter.format("\tadd $s0, $s0, 1\n");
		fileWriter.format("\tmul $s0, $s0, 4\n");
		fileWriter.format("\taddu $s0, $s0, $t%d\n", idxarray);
		fileWriter.format("\tsw $t%d, 0($s0)\n", idxvalue);
	}

	public void beqz(TEMP oprnd1,String label)
	{
		int i1 =oprnd1.getSerialNumber();
				
		fileWriter.format("\tbeq $t%d, 0, %s\n", i1, label);				
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MIPSGenerator instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MIPSGenerator() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/

	// singleton instance
	public static MIPSGenerator getInstance()
	{
		 if (instance == null) {
            instance = new MIPSGenerator();
            try {
                instance.fileWriter = new PrintWriter(DIRNAME + MIPS_FILENAME);
                instance.dataWriter = new PrintWriter(DIRNAME + DATA_FILENAME);

                instance.dataWriter.println(".data");
				instance.dataWriter.format("\n");
                instance.dataWriter.format("\tstring_access_violation: .asciiz \"Access Violation\"\n");
                instance.dataWriter.format("\tstring_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
                instance.dataWriter.format("\tstring_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
				instance.dataWriter.format("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

	// merge the data and text sections into a single file called MIPS.txt
	private void mergeFiles() {
        try (BufferedWriter mergedWriter = new BufferedWriter(new FileWriter(DIRNAME + "MIPS_final.txt"));
             BufferedWriter textWriter = new BufferedWriter(new FileWriter(DIRNAME + "MIPS.txt", true));
             BufferedWriter dataReader = new BufferedWriter(new FileWriter(DIRNAME + "MIPS_data.txt", true))) {

            // Write .data section first
            File dataFile = new File(DIRNAME + DATA_FILENAME);
            java.util.Scanner dataScanner = new java.util.Scanner(dataFile);
            while (dataScanner.hasNextLine()) {
                mergedWriter.write(dataScanner.nextLine() + "\n");
            }
            dataScanner.close();

            // Write .text section
            File textFile = new File(DIRNAME + MIPS_FILENAME);
            java.util.Scanner textScanner = new java.util.Scanner(textFile);
            while (textScanner.hasNextLine()) {
                mergedWriter.write(textScanner.nextLine() + "\n");
            }
            textScanner.close();
            
            // Overwrite MIPS.txt with merged content
            mergedWriter.close();
            textWriter.close();
            dataReader.close();
            
            // Rename merged file to MIPS.txt
            File mergedFile = new File(DIRNAME + "MIPS_final.txt");
            if (mergedFile.renameTo(textFile)) {
                System.out.println("Successfully merged files!");
            } else {
                System.out.println("Failed to rename merged file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
