/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.*;

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
	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile()
	{
		fileWriter.format("\n");
		fileWriter.format("\tabort:\n");
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.println();
		fileWriter.println("main:");
		fileWriter.print("\tjal user_main\n");
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();
		dataWriter.print("\n");
		dataWriter.print(".text\n");
		dataWriter.print("\n");
		dataWriter.close();
        mergeFiles();
	}

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
	

	

	public void ret(TEMP t){
		int idx=t.getSerialNumber();
		fileWriter.format("\tmove $v0, $t%d\n", idx);
	}
	public void allocate_global_int(String var_name, int value)
	{
		dataWriter.format("\tg_%s: .word %d\n" ,var_name, value);
	}
	public void allocate_global_string(String var_name, String value)
	{
		String str = value.substring(1, value.length()-1);
		dataWriter.format("\tg_%s: .word str_%s\n" ,var_name, str);
	}

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
	
	public void function_epilogue()
	{
		fileWriter.format("\n");
		fileWriter.format("\t# epilogue:\n");
		fileWriter.format("\tmove $sp, $fp\n");
		for (int i = 9; i >= 0; i--) {
			fileWriter.format("\tlw $t%d, %d($sp)\n", i, -(10-i)*WORD_SIZE);
		}
		fileWriter.format("\tlw $fp, 0($sp)\n");
		fileWriter.format("\tlw $ra, 4($sp)\n");
		fileWriter.format("\taddu $sp, $sp, 8\n");
		fileWriter.format("\tjr $ra\n");

		fileWriter.format("\n");
	}

	public void allocate_string(String value, String label)
	{
		dataWriter.format("\t%s: .asciiz %s\n" ,label, value);
	}

	public void allocate_global_no_args(String var_name)
	{
		dataWriter.format("\tg_%s: .word 0\n" ,var_name);
	}

	public void allocate_local_no_args(int offset, TEMP dst)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tli $t%d, 0\n", idx);
		fileWriter.format("\tsw $t%d, %d($fp)\n", idx, offset);
	}

	public void loadParam(TEMP dst, int index)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, (index+2)*WORD_SIZE);
	}

	public void loadLocal(TEMP dst, int offset)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, offset);
	}

	public void loadGlobal(TEMP dst, String var_name)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw $t%d, g_%s\n", idx, var_name);
	}

	public void load_string(TEMP dst, String value)
	{
		int idx=dst.getSerialNumber();
		fileWriter.format("\tla $t%d, %s\n", idx, value);
	}

	public void storeLocal(TEMP src, int offset)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw $t%d, %d($fp)\n",idxsrc ,offset);
	}

	public void storeGlobal(TEMP src, String var_name)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw $t%d, 0(g_%s)\n",idxsrc ,var_name);
	}

	public void storeParam(TEMP src)
	{
		int idxsrc = src.getSerialNumber();
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n",idxsrc);

	}


	public void jalNotVoid(TEMP dst, String func_name, int numOfArgs)
	{
		fileWriter.format("\tjal %s\n",func_name);
		fileWriter.format("\taddu $sp, $sp, %d\n", numOfArgs*WORD_SIZE);
		int idxsrc = dst.getSerialNumber();
		fileWriter.format("\tmove $t%d, $v0\n", idxsrc);
	}


	public void li(TEMP t,int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli $t%d, %d\n",idx,value);
	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd $t%d, $t%d, $t%d\n",dstidx,i1,i2);
	}

	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	public void div(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tdiv $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	public void slt(TEMP oprnd1,TEMP oprnd2, TEMP dst)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tslt $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	public void PrintIntSyscall(TEMP t)
	{
		int idx = t.getSerialNumber();
		fileWriter.format("\n");
		fileWriter.format("\t# inline implementation of PrintInt:\n");
		fileWriter.format("\tmove $a0, $t%d\n", idx);
		fileWriter.format("\tli $v0, 1\n");
		fileWriter.format("\tsyscall\n");
	}

	public void PrintStringSyscall(TEMP t)
	{
		int idx = t.getSerialNumber();
		fileWriter.format("\n");
		fileWriter.format("\t# inline implementation of PrintInt:\n");
		fileWriter.format("\tla $a0, $t%d\n", idx);
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tsyscall\n");
	}

	public void mallocSyscall(int size)
	{
		fileWriter.format("\tli $a0, %d\n", size);
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");
	}

	public void exitSyscall()
	{
		fileWriter.format("\tli $v0, 10\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void seq(TEMP oprnd1,TEMP oprnd2,TEMP dst)
	{
		int i1 = oprnd1.getSerialNumber();
		int i2 = oprnd2.getSerialNumber();
		int dstidx = dst.getSerialNumber();

		fileWriter.format("\tseq $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmul t%d, t%d ,t%d\n", dstidx, i1, i2);
	}
	public void label(String inlabel)
	{
		fileWriter.format("%s:\n",inlabel);
	}	


	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
	}	
	public void blt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}

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

	public void array_access(TEMP dst, TEMP arrayTemp, TEMP index)
	{
		int idxdst = dst.getSerialNumber();
		int idxarray = arrayTemp.getSerialNumber();
		int idxindex = index.getSerialNumber();

		fileWriter.format("\tbltz $t%d, abort\n", idxindex);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxarray);
		fileWriter.format("\tbge $t%d, $s0, abort\n", idxindex);
		fileWriter.format("\tmove $s0, $t%d\n", idxindex);
		fileWriter.format("\tadd $s0, $s0, 1\n");
		fileWriter.format("\tmul $s0, $s0, 4\n");
		fileWriter.format("\taddu $s0, $s0, $t%d\n", idxarray);
		fileWriter.format("\tlw $t%d, 0($s0)\n", idxdst);
	}

	public void set_array(TEMP array, TEMP index, TEMP value)
	{
		int idxarray = array.getSerialNumber();
		int idxindex = index.getSerialNumber();
		int idxvalue = value.getSerialNumber();

		fileWriter.format("\tbltz $t%d, abort\n", idxindex);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxarray);
		fileWriter.format("\tbge $t%d, $s0, abort\n", idxindex);
		fileWriter.format("\tmove $s0, $t%d\n", idxindex);
		fileWriter.format("\tadd $s0, $s0, 1\n");
		fileWriter.format("\tmul $s0, $s0, 4\n");
		fileWriter.format("\taddu $s0, $s0, $t%d\n", idxarray);
		fileWriter.format("\tsw $t%d, 0($s0)\n", idxvalue);
	}


	public void bge(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbge Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}
	public void bne(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}
	public void beq(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbeq Temp_%d,Temp_%d,%s\n",i1,i2,label);				
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
