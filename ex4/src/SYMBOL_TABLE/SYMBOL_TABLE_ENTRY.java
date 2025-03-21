/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/**********************/
/* SYMBOL TABLE ENTRY */
/**********************/
public class SYMBOL_TABLE_ENTRY
{
	/*********/
	/* index */
	/*********/
	int index;
	
	/********/
	/* name */
	/********/
	public String name;

	/******************/
	/* TYPE value ... */
	/******************/
	public TYPE type;

	/*********************************************/
	/* prevtop and next symbol table entries ... */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY prevtop;
	public SYMBOL_TABLE_ENTRY next;

	/****************************************************/
	/* The prevtop_index is just for debug purposes ... */
	/****************************************************/
	public int prevtop_index;
	public boolean isClassDec;
	public int scopeLevel;
	public boolean isFuncArg;
	
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(String name, TYPE type, int index, SYMBOL_TABLE_ENTRY next, SYMBOL_TABLE_ENTRY prevtop, int prevtop_index, boolean isClassDec, int scopeLevel, boolean isFuncArg)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.isClassDec = isClassDec;
		this.scopeLevel = scopeLevel;
		this.isFuncArg = isFuncArg;
	}
}
