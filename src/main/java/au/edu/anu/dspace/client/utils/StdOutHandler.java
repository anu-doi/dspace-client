/**
 * 
 */
package au.edu.anu.dspace.client.utils;

import java.io.PrintStream;

/**
 * @author Rahul Khanna
 *
 */
public class StdOutHandler {
	
	
	private PrintStream outStream;
	private int lastLineLength = 0;

	public StdOutHandler() {
		this(System.out);
	}
	
	public StdOutHandler(PrintStream outStream) {
		this.outStream = outStream;
	}

	public void print(String format, Object... args) {
		String formatted = String.format(format, args);
		lastLineLength = formatted.length();
		outStream.print(formatted);
	}
	
	public void println() {
		lastLineLength = 0;
		outStream.println();
	}

	public void println(String format, Object... args) {
		print(format, args);
		println();
	}

	public void eraseLastPrinted() {
		for (int i = 0; i < this.lastLineLength; i++) {
			outStream.print('\b');
		}
		this.lastLineLength = 0;
	}
}
