   
import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import AST.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AST_PROGRAM AST;
		FileReader file_reader;
		PrintWriter file_writer;
		String inputFilename = argv[0];
		String outputFilename = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			file_writer = new PrintWriter(outputFilename);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			try {
				p = new Parser(l);
	
				/***********************************/
				/* [5] 3 ... 2 ... 1 ... Parse !!! */
				/***********************************/
				AST = (AST_PROGRAM) p.parse().value;

				file_writer.write("OK\n");
				
				/*************************/
				/* [6] Print the AST ... */
				/*************************/
				AST.PrintMe();
				
				/*************************/
				/* [7] Close output file */
				/*************************/
				file_writer.close();
				
				/*************************************/
				/* [8] Finalize AST GRAPHIZ DOT file */
				/*************************************/
				AST_GRAPHVIZ.getInstance().finalizeFile();
			}
			catch (lexError ex){
				file_writer.print("ERROR");
				file_writer.close();
			}
			catch (syError ex){
				file_writer.print("ERROR(");
				file_writer.print(l.getLine());
				file_writer.print(")");
				file_writer.close();
			}

			catch (RuntimeException ex){
				file_writer.print("ERROR(");
				file_writer.print(ex.getMessage());
				file_writer.print(")");
				file_writer.close();
				ex.printStackTrace();
				AST_GRAPHVIZ.getInstance().finalizeFile();
			}
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

class syError extends RuntimeException 
{
	public syError(String msg){
		super(msg);
	}
}

class lexError extends RuntimeException 
{
	public lexError(String msg){
		super(msg);
	}
}
