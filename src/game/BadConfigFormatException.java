package game;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

@SuppressWarnings("serial")
public class BadConfigFormatException extends Exception {
	public BadConfigFormatException(String reason) {
		super("The configuration file is not valid: " + reason);
		try {
			PrintWriter pw = new PrintWriter("log.txt");
			pw.write("Bad config file!: " + reason);
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Well this is awkward... There was an error opening the file to write errors to!");
		}
	}
}
