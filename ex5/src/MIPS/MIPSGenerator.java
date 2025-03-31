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
    private static String DIRNAME = "./output/";
    private static String MIPS_FILENAME;
    private static final String DATA_FILENAME = "MIPS_data.txt";
	Map<String, Integer> regMap = IR.getInstance().getRegMap();


	public int getAllocatedReg(String regName) {
		System.out.println("getAllocatedReg8888: " + regName);
		if (regMap.containsKey(regName)) {
			return regMap.get(regName);
		}
		return -1;
	}
	// This function is called when the file is done being written
	public void finalizeFile()
	{
		dataWriter.print("\n");
		dataWriter.print(".text\n");
		dataWriter.print("\n");

		generateAllocateStringSpaceFunction();
		generateAddStringsFunction();
		defineStringEqualityFunction();
		dataWriter.print("\n");
		writeCheckBoundsSubroutine();
		dataWriter.print("\n");
		generateFloorFixSubroutine();

		dataWriter.format("\n");
		dataWriter.format("\tabort_access_violation:\n");
		dataWriter.format("\tla $s0, string_access_violation\n");
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

	public void generateFloorFixSubroutine() {
		dataWriter.format("floor_fix:\n");
		// If remainder == 0, return
		dataWriter.format("\tbeq $s3, $zero, return_floor_fix\n");

		// Check if dividend and divisor have opposite signs (XOR sign bits)
		dataWriter.format("\txor $s4, $s0, $s1\n"); // XOR dividend and divisor
		dataWriter.format("\tbltz $s4, adjust_floor\n"); // If negative, adjust

		dataWriter.format("return_floor_fix:\n");
		dataWriter.format("\tjr $ra\n"); // Return

		// Adjust for flooring: decrement quotient
		dataWriter.format("adjust_floor:\n");
		dataWriter.format("\tsub $s2, $s2, 1\n"); // Quotient -= 1
		dataWriter.format("\tj return_floor_fix\n");
	}


	public void writeCheckBoundsSubroutine() {
		dataWriter.format("check_bounds:\n");

		dataWriter.format("\tlw $s0, min\n");  // Load min = -32768
    	dataWriter.format("\tlw $s1, max\n");  // Load max = 32767

		// If $a0 is greater than max, set $a0 to max.
		dataWriter.format("\tble $a0, $s1, checkBoundsMin\n");
		dataWriter.format("\tmove  $a0, $s1\n");
		// Label for checking the minimum bound.
		dataWriter.format("checkBoundsMin:\n");
		// If $a0 is less than min, set $a0 to min.
		dataWriter.format("\tbge $a0, $s0, checkBoundsReturn\n");
		dataWriter.format("\tmove  $a0, $s0\n");
		dataWriter.format("checkBoundsReturn:\n");
		// Return from the subroutine.
		dataWriter.format("\tjr $ra\n");
	}

	// Define the string equality function in MIPS assembly
	public void defineStringEqualityFunction() {
		dataWriter.format("\n");
		dataWriter.format("string_equality:\n"); // Start of the string equality function
		dataWriter.format("\tli $v0, 1\n");  // Initialize return value to 1 (true)
		dataWriter.format("\tmove $s0, $a0\n");  // $a0 = t1 (first string)
		dataWriter.format("\tmove $s1, $a1\n");  // $a1 = t2 (second string)
		dataWriter.format("str_eq_loop:\n");
		dataWriter.format("\tlb $s2, 0($s0)\n");  // Load byte from t1
		dataWriter.format("\tlb $s3, 0($s1)\n");  // Load byte from t2
		dataWriter.format("\tbne $s2, $s3, neq_label\n");  // If bytes don't match, jump to neq_label
		dataWriter.format("\tbeq $s2, 0, str_eq_end\n");  // If we reach null byte (end of string), end comparison
		dataWriter.format("\taddu $s0, $s0, 1\n");  // Move to the next byte in t1
		dataWriter.format("\taddu $s1, $s1, 1\n");  // Move to the next byte in t2
		dataWriter.format("\tj str_eq_loop\n");  // Continue checking the next bytes
		dataWriter.format("neq_label:\n");
		dataWriter.format("\tli $v0, 0\n");  // Set return value to 0 (false) if strings are not equal
		dataWriter.format("str_eq_end:\n");
		dataWriter.format("\tjr $ra\n");  // Return from function
	}

	// Calling the string equality function in MIPS
	public void checkStringEquality(TEMP dst, TEMP t1, TEMP t2) {
		int idx1 = getAllocatedReg(t1.toString());
		int idx2 = getAllocatedReg(t2.toString());
		int idxdst = getAllocatedReg(dst.toString());

		// Move the string locations (t1 and t2) into $a0 and $a1
		fileWriter.format("\tmove $a0, $t%d\n", idx1);  // Load t1 into $a0
		fileWriter.format("\tmove $a1, $t%d\n", idx2);  // Load t2 into $a1
		
		// Call the string equality function
		fileWriter.format("\tjal string_equality\n");
		
		// The result (0 or 1) will be in $v0, move it to dst
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
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
	public void ret(TEMP t, String funcName){
		int idx = getAllocatedReg(t.toString());
		fileWriter.format("\tmove $v0, $t%d\n", idx);
		fileWriter.format("\tj epilogue_%s\n", funcName);	
	}

	public void ret_no_exp(String funcName){
		fileWriter.format("\tj epilogue_%s\n", funcName);	
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
		int idxdst = getAllocatedReg(dst.toString());
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
	public void function_epilogue(String funcName)
	{
		fileWriter.format("\n");
		fileWriter.format("\t# epilogue:\n");
		fileWriter.format("epilogue_%s:\n", funcName);
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
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tli $t%d, 0\n", idx);
		fileWriter.format("\tsw $t%d, %d($fp)\n", idx, offset);
	}

	// load function parameter
	public void loadParam(TEMP dst, int offset)
	{

		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, offset);
	}

	// load local variable
	public void loadLocal(TEMP dst, int offset)
	{
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tlw $t%d, %d($fp)\n", idx, offset);
	}

	// load global variable
	public void loadGlobal(TEMP dst, String var_name)
	{
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tla $t%d, g_%s\n", idx, var_name);
		fileWriter.format("\tlw $t%d, 0($t%d)\n", idx, idx);
	}

	// load global string
	public void loadGlobalString(TEMP dst, String var_name)
	{
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tla $t%d, g_%s\n", idx, var_name);
		fileWriter.format("\tlw $t%d, 0($t%d)\n", idx, idx);
	}

	// load string
	public void load_string(TEMP dst, String value)
	{
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tla $t%d, %s\n", idx, value);
	}

	public void loadField(TEMP dst, int offset)
	{
		int idx = getAllocatedReg(dst.toString());
		fileWriter.format("\tlw $s0, 8($fp)\n");
		fileWriter.format("\tlw $t%d, %d($s0)\n", idx, offset);
	}

	// store local variable
	public void storeLocal(TEMP src, int offset)
	{
		int idxsrc = getAllocatedReg(src.toString());
		fileWriter.format("\tsw $t%d, %d($fp)\n",idxsrc ,offset);
	}

	// store global variable
	public void storeGlobal(TEMP src, String var_name)
	{
		int idxsrc = getAllocatedReg(src.toString());
		fileWriter.format("\tla $s0, g_%s\n", var_name);
		fileWriter.format("\tsw $t%d, 0($s0)\n",idxsrc);
	}

	// store function parameter
	public void storeParam(TEMP src, int offset)
	{
		int idxsrc = getAllocatedReg(src.toString());
		fileWriter.format("\tsw $t%d, %d($fp)\n",idxsrc ,offset);
	}	

	public void storeParamForCall(TEMP src){
		int idxsrc = getAllocatedReg(src.toString());
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n",idxsrc);
	}

	public void storeField(TEMP src, int offset)
	{
		int idxsrc = getAllocatedReg(src.toString());
		fileWriter.format("\tlw $s0, 8($fp)\n");
		fileWriter.format("\tsw $t%d, %d($s0)\n",idxsrc ,offset);
	}

	// access class field
	public void access_field(TEMP dst, TEMP var, String fieldName, int offset)
	{
		int idxdst = getAllocatedReg(dst.toString());
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tbeq $t%d, 0, abort_invalid_ptr_dref\n", idxvar);
		fileWriter.format("\tlw $t%d, %d($t%d)\n", idxdst, offset, idxvar);
	}

	// set class field
	public void set_field(TEMP classInstanceTemp, TEMP var, String name, int offset)
	{
		int idxclassInstanceTemp = getAllocatedReg(classInstanceTemp.toString());
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tbeq $t%d, 0, abort_invalid_ptr_dref\n", idxclassInstanceTemp);
		fileWriter.format("\tsw $t%d, %d($t%d)\n", idxvar, offset, idxclassInstanceTemp);
	}

	//  jal function with void return
	public void jalNotVoid(TEMP dst, String func_name, int numOfArgs)
	{
		fileWriter.format("\tjal %s\n",func_name);
		fileWriter.format("\taddu $sp, $sp, %d\n", numOfArgs*WORD_SIZE);
		int idxsrc = getAllocatedReg(dst.toString());
		fileWriter.format("\tmove $t%d, $v0\n", idxsrc);
	}

	// jal function with void return
	public void jalVoid(String func_name, int numOfArgs)
	{
		fileWriter.format("\tjal %s\n",func_name);
		fileWriter.format("\taddu $sp, $sp, %d\n", numOfArgs*WORD_SIZE);
	}

	public void virtualCallArgsNotVoid(TEMP dst, TEMP var, int offset, int numOfArgs){
		int idxdst = getAllocatedReg(dst.toString());
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n", idxvar);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxvar);
		fileWriter.format("\t$s1, %d($s0)\n", offset);
		fileWriter.format("\tjalr $s1\n");
		fileWriter.format("\taddu $sp, $sp, %d\n", (numOfArgs+1)*WORD_SIZE);
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
	} 

	public void virtualCallNotArgsNotVoid(TEMP dst, TEMP var, int offset){
		int idxdst = getAllocatedReg(dst.toString());
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n", idxvar);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxvar);
		fileWriter.format("\tlw $s1, %d($s0)\n", offset);
		fileWriter.format("\tjalr $s1\n");
		fileWriter.format("\taddu $sp, $sp, 4\n");
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
	} 


	public void virtualCallArgsVoid(TEMP var, int offset, int numOfArgs){
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n", idxvar);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxvar);
		fileWriter.format("\tlw $s1, %d($s0)\n", offset);
		fileWriter.format("\tjalr $s1\n");
		fileWriter.format("\taddu $sp, $sp, %d\n", (numOfArgs+1)*WORD_SIZE);
	}

	public void virtualCallNotArgsVoid(TEMP var, int offset){
		int idxvar = getAllocatedReg(var.toString());
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $t%d, 0($sp)\n", idxvar);
		fileWriter.format("\tlw $s0, 0($t%d)\n", idxvar);
		fileWriter.format("\tlw $s1, %d($s0)\n", offset);
		fileWriter.format("\tjalr $s1\n");
		fileWriter.format("\taddu $sp, $sp, 4\n");
	}

	public void jokerCallNotArgsVoid(int offset) {
		fileWriter.format("\tlw $s0, 8($fp)\n");  // Load 'this'
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $s0, 0($sp)\n");
		fileWriter.format("\tlw $s1, 0($s0)\n");  // Load VMT
		fileWriter.format("\tlw $s2, %d($s1)\n", offset);  // Load method address from VMT
		fileWriter.format("\tjalr $s2\n");  // Jump to method
		fileWriter.format("\taddu $sp, $sp, 4\n");
	}

	public void jokerCallArgsVoid(int offset, int numOfArgs) {
		fileWriter.format("\tlw $s0, 8($fp)\n");  // Load 'this'
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $s0, 0($sp)\n");
		fileWriter.format("\tlw $s1, 0($s0)\n");  // Load VMT
		fileWriter.format("\tlw $s2, %d($s1)\n", offset);  // Load method address from VMT
		fileWriter.format("\tjalr $s2\n");  // Jump to method
		fileWriter.format("\taddu $sp, $sp, %d\n", (numOfArgs+1)*WORD_SIZE);
	}

	public void jokerCallArgsNotVoid(TEMP dst, int offset, int numOfArgs){
		int idxdst = getAllocatedReg(dst.toString());
		fileWriter.format("\tlw $s0, 8($fp)\n");  // Load 'this'
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $s0, 0($sp)\n");
		fileWriter.format("\tlw $s1, 0($s0)\n");  // Load VMT
		fileWriter.format("\tlw $s2, %d($s1)\n", offset);  // Load method address from VMT
		fileWriter.format("\tjalr $s2\n");  // Jump to method
		fileWriter.format("\taddu $sp, $sp, %d\n", (numOfArgs+1)*WORD_SIZE);
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
	} 

	public void jokerCallNotArgsNotVoid(TEMP dst, int offset){
		int idxdst = getAllocatedReg(dst.toString());
		fileWriter.format("\tlw $s0, 8($fp)\n");  // Load 'this'
		fileWriter.format("\tsubu $sp, $sp, 4\n");
		fileWriter.format("\tsw $s0, 0($sp)\n");
		fileWriter.format("\tlw $s1, 0($s0)\n");  // Load VMT
		fileWriter.format("\tlw $s2, %d($s1)\n", offset);  // Load method address from VMT
		fileWriter.format("\tjalr $s2\n");  // Jump to method
		fileWriter.format("\taddu $sp, $sp, 4\n");
		fileWriter.format("\tmove $t%d, $v0\n", idxdst);
	} 




	public void addStrings(TEMP dst, TEMP src1, TEMP src2) {
		int idxdst = getAllocatedReg(dst.toString());
		int idxsrc1 = getAllocatedReg(src1.toString());
		int idxsrc2 = getAllocatedReg(src2.toString());

		fileWriter.format("\n\t# Call string concatenation function\n");

		// Pass source strings to function
		fileWriter.format("\tmove $a1, $t%d\n", idxsrc1); // src1 pointer
		fileWriter.format("\tmove $a2, $t%d\n", idxsrc2); // src2 pointer
		
		// Call function to allocate space for concatenated string
		fileWriter.format("\tjal allocateStringSpace\n"); // Allocate space
		fileWriter.format("\tmove $a0, $v0\n");  // Store allocated address in $a0
		fileWriter.format("\tmove $t%d, $v0\n", idxdst); // Store result in dst temp

		// Call addStrings function
		fileWriter.format("\tjal addStrings\n");

		fileWriter.format("\n");
	}


	public void generateAllocateStringSpaceFunction() {
		dataWriter.format("\n.globl allocateStringSpace\n");
		dataWriter.format("allocateStringSpace:\n");
		
		// Save registers on stack
		dataWriter.format("\taddi $sp, $sp, -12\n");
		dataWriter.format("\tsw $ra, 8($sp)\n");
		dataWriter.format("\tsw $s0, 4($sp)\n");
		dataWriter.format("\tsw $s1, 0($sp)\n");

		// Load src1 and src2 pointers
		dataWriter.format("\tmove $s0, $a1\n");  // src1 pointer
		dataWriter.format("\tmove $s1, $a2\n");  // src2 pointer
		dataWriter.format("\tli $t0, 0\n");      // String length counter

		// Compute length of src1
		dataWriter.format("alloc_str_len1:\n");
		dataWriter.format("\tlb $s3, 0($s0)\n");
		dataWriter.format("\tbeq $s3, $zero, alloc_str_len2\n");
		dataWriter.format("\taddiu $t0, $t0, 1\n");
		dataWriter.format("\taddiu $s0, $s0, 1\n");
		dataWriter.format("\tj alloc_str_len1\n");

		// Compute length of src2
		dataWriter.format("alloc_str_len2:\n");
		dataWriter.format("\tlb $s3, 0($s1)\n");
		dataWriter.format("\tbeq $s3, $zero, alloc_str_done\n");
		dataWriter.format("\taddiu $t0, $t0, 1\n");
		dataWriter.format("\taddiu $s1, $s1, 1\n");
		dataWriter.format("\tj alloc_str_len2\n");

		// Allocate space using syscall 9 (malloc)
		dataWriter.format("alloc_str_done:\n");
		dataWriter.format("\taddiu $t0, $t0, 1\n");  // Space for null terminator
		dataWriter.format("\tmove $a0, $t0\n");
		dataWriter.format("\tli $v0, 9\n");
		dataWriter.format("\tsyscall\n");

		// Restore registers and return
		dataWriter.format("\tlw $ra, 8($sp)\n");
		dataWriter.format("\tlw $s0, 4($sp)\n");
		dataWriter.format("\tlw $s1, 0($sp)\n");
		dataWriter.format("\taddi $sp, $sp, 12\n");
		dataWriter.format("\tjr $ra\n\n");
	}


	public void generateAddStringsFunction() {
		dataWriter.format("\n.globl addStrings\n");
		dataWriter.format("addStrings:\n");

		// Save registers
		dataWriter.format("\taddi $sp, $sp, -20\n");
		dataWriter.format("\tsw $ra, 16($sp)\n");
		dataWriter.format("\tsw $s0, 12($sp)\n");
		dataWriter.format("\tsw $s1, 8($sp)\n");
		dataWriter.format("\tsw $s2, 4($sp)\n");
		dataWriter.format("\tsw $s3, 0($sp)\n");

		// Load arguments
		dataWriter.format("\tmove $s0, $a1\n"); // src1 pointer
		dataWriter.format("\tmove $s1, $a2\n"); // src2 pointer
		dataWriter.format("\tmove $s3, $a0\n"); // dst pointer

		// Copy src1 into dst
		dataWriter.format("str_concat_loop1:\n");
		dataWriter.format("\tlb $s2, 0($s0)\n");
		dataWriter.format("\tbeq $s2, $zero, str_concat_loop2\n");
		dataWriter.format("\tsb $s2, 0($s3)\n");
		dataWriter.format("\taddiu $s0, $s0, 1\n");
		dataWriter.format("\taddiu $s3, $s3, 1\n");
		dataWriter.format("\tj str_concat_loop1\n");

		// Copy src2 into dst
		dataWriter.format("str_concat_loop2:\n");
		dataWriter.format("\tlb $s2, 0($s1)\n");
		dataWriter.format("\tbeq $s2, $zero, str_concat_done\n");
		dataWriter.format("\tsb $s2, 0($s3)\n");
		dataWriter.format("\taddiu $s1, $s1, 1\n");
		dataWriter.format("\taddiu $s3, $s3, 1\n");
		dataWriter.format("\tj str_concat_loop2\n");

		// Null terminate dst
		dataWriter.format("str_concat_done:\n");
		dataWriter.format("\tsb $zero, 0($s3)\n");

		// Restore registers and return
		dataWriter.format("\tlw $ra, 16($sp)\n");
		dataWriter.format("\tlw $s0, 12($sp)\n");
		dataWriter.format("\tlw $s1, 8($sp)\n");
		dataWriter.format("\tlw $s2, 4($sp)\n");
		dataWriter.format("\tlw $s3, 0($sp)\n");
		dataWriter.format("\taddi $sp, $sp, 20\n");
		dataWriter.format("\tjr $ra\n\n");
	}





	

	// load immediate
	public void li(TEMP t,int value)
	{
		int idx = getAllocatedReg(t.toString());
		fileWriter.format("\tli $t%d, %d\n",idx,value);
	}

	// add two temporaries
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 = getAllocatedReg(oprnd1.toString());
		int i2 = getAllocatedReg(oprnd2.toString());
		int dstidx = getAllocatedReg(dst.toString());

		fileWriter.format("\tadd $t%d, $t%d, $t%d\n", dstidx, i1, i2);
		fileWriter.format("\tmove $a0, $t%d\n", dstidx);
		fileWriter.format("\tjal check_bounds\n");
		fileWriter.format("\tmove $t%d, $a0\n", dstidx);
	}

	// subtract two temporaries
	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 = getAllocatedReg(oprnd1.toString());
		int i2 = getAllocatedReg(oprnd2.toString());
		int dstidx = getAllocatedReg(dst.toString());

		fileWriter.format("\tsub $t%d, $t%d, $t%d\n", dstidx, i1, i2);
		fileWriter.format("\tmove $a0, $t%d\n", dstidx);
		fileWriter.format("\tjal check_bounds\n");
		fileWriter.format("\tmove $t%d, $a0\n", dstidx);
	}

	// div two integers
	public void div(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
		int dividendIdx = getAllocatedReg(oprnd1.toString());
		int divisorIdx = getAllocatedReg(oprnd2.toString());
		int dstIdx = getAllocatedReg(dst.toString());

		fileWriter.format("\tmove $s0, $t%d\n", dividendIdx); // Dividend
		fileWriter.format("\tmove $s1, $t%d\n", divisorIdx);  // Divisor

		fileWriter.format("\tbeq $s1, $zero, abort_illegal_division_by_0\n");

		fileWriter.format("\tdiv $s0, $s1\n");
		
		fileWriter.format("\tmflo $s2\n"); // Quotient in $s2

		fileWriter.format("\tmfhi $s3\n"); // Remainder in $s3

		fileWriter.format("\tjal floor_fix\n");

		fileWriter.format("\tmove $s%d, $s2\n", dstIdx);

		fileWriter.format("\tmove $a0, $s%d\n", dstIdx);
		fileWriter.format("\tjal check_bounds\n");
		fileWriter.format("\tmove $t%d, $a0\n", dstIdx);
	}


	// set less than
	public void slt(TEMP oprnd1,TEMP oprnd2, TEMP dst)
	{
		int i1 = getAllocatedReg(oprnd1.toString());
		int i2 = getAllocatedReg(oprnd2.toString());
		int dstidx = getAllocatedReg(dst.toString());

		fileWriter.format("\tslt $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// print integer syscall
	public void PrintIntSyscall(TEMP t)
	{
		int idx = getAllocatedReg(t.toString());
		fileWriter.format("\n");
		fileWriter.format("\t# inline implementation of PrintInt:\n");
		
		// Print the integer
		fileWriter.format("\tmove $a0, $t%d\n", idx);  // Move integer into $a0
		fileWriter.format("\tli $v0, 1\n");  // Syscall for printing an integer
		fileWriter.format("\tsyscall\n");

		// Print a space after the integer
		fileWriter.format("\tli $a0, 0x20\n");  // Load ASCII value of space (0x20) into $a0
		fileWriter.format("\tli $v0, 11\n");  // Syscall for printing a character
		fileWriter.format("\tsyscall\n");
	}


	// print string syscall
	public void PrintStringSyscall(TEMP t)
	{
		int idx = getAllocatedReg(t.toString());
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
		int i1 = getAllocatedReg(oprnd1.toString());
		int i2 = getAllocatedReg(oprnd2.toString());
		int dstidx = getAllocatedReg(dst.toString());

		fileWriter.format("\tseq $t%d, $t%d ,$t%d\n", dstidx, i1, i2);
	}

	// mul two integers
	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 = getAllocatedReg(oprnd1.toString());
		int i2 = getAllocatedReg(oprnd2.toString());
		int dstidx = getAllocatedReg(dst.toString());

		fileWriter.format("\tmul $t%d, $t%d, $t%d\n", dstidx, i1, i2);
		fileWriter.format("\tmove $a0, $t%d\n", dstidx);
		fileWriter.format("\tjal check_bounds\n");
		fileWriter.format("\tmove $t%d, $a0\n", dstidx);
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
		int idxdst = getAllocatedReg(dst.toString());
		int idxsize = getAllocatedReg(size.toString());

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
		int idxdst = getAllocatedReg(dst.toString());
		int idxarray = getAllocatedReg(arrayTemp.toString());
		int idxindex = getAllocatedReg(index.toString());

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
		int idxarray = getAllocatedReg(array.toString());
		int idxindex = getAllocatedReg(index.toString());
		int idxvalue = getAllocatedReg(value.toString());

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
		int i1 = getAllocatedReg(oprnd1.toString());
				
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


	public void setOutoutputFileName(String name)
	{
		try {
			MIPS_FILENAME = name;
            instance.fileWriter = new PrintWriter(name);
            instance.dataWriter = new PrintWriter(DIRNAME + DATA_FILENAME);

            instance.dataWriter.println(".data");
			instance.dataWriter.format("\n");
            instance.dataWriter.format("\tstring_access_violation: .asciiz \"Access Violation\"\n");
            instance.dataWriter.format("\tstring_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
            instance.dataWriter.format("\tstring_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
			instance.dataWriter.format("\n");

			instance.dataWriter.format("\t# Global variables\n");
			instance.dataWriter.format("\n");
			instance.dataWriter.format("\t max: .word 32767\n");
			instance.dataWriter.format("\t min: .word -32768\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/

	// singleton instance
	public static MIPSGenerator getInstance()
	{
		 if (instance == null) {
            instance = new MIPSGenerator();
        }
        return instance;
    }

	// merge the data and text sections into a single file called MIPS.txt
	private void mergeFiles() {
		System.out.println("Merging files...");	
		System.out.println("mipsfile: " + MIPS_FILENAME);
		System.out.println("datafile: " + DATA_FILENAME);
		System.out.println("dir: " + DIRNAME);
        try (BufferedWriter mergedWriter = new BufferedWriter(new FileWriter(DIRNAME + "MIPS_final.txt"));
             BufferedWriter textWriter = new BufferedWriter(new FileWriter(MIPS_FILENAME, true));
             BufferedWriter dataReader = new BufferedWriter(new FileWriter(DIRNAME + "MIPS_data.txt", true))) {

            // Write .data section first
            File dataFile = new File(DIRNAME + DATA_FILENAME);
            java.util.Scanner dataScanner = new java.util.Scanner(dataFile);
            while (dataScanner.hasNextLine()) {
                mergedWriter.write(dataScanner.nextLine() + "\n");
            }
            dataScanner.close();

            // Write .text section
            File textFile = new File(MIPS_FILENAME);
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
