/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.CommandLogger.Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author ibhh
 */
public class CommandConfigHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	public ArrayList<String> commands = new ArrayList<String>();

	public CommandConfigHandler() {
	}

	public Iterator<String> getCommands() {
		return commands.iterator();
	}

	public boolean isInList(String in) {
		return commands.contains(in);
	}

	public boolean addCommand(String in) {
		if (commands.contains(in)) {
			return false;
		}
		return commands.add(in);
	}

	public boolean removeCommand(String in) {
		return commands.remove(in);
	}

	@SuppressWarnings("resource")
	public void loadFromFile(String path) {
		try {
			File file = new File(path + File.separator
					+ "LogNotThisCommands.txt");
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String s = scan.nextLine();
				if (s.length() > 1) {
					commands.add(s);
				}
			}
		} catch (FileNotFoundException ex) {
		}
	}

	public void saveToFile(final String path) {
		new File(path).mkdirs();
		File file = new File(path + File.separator + "LogNotThisCommands.txt");
		FileWriter fstream = null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file = new File(path + File.separator + "LogNotThisCommands.txt");
			file.createNewFile();
			fstream = new FileWriter(file, true);
			PrintWriter out = new PrintWriter(fstream);
			for (String element : commands) {
				out.println(element);
			}
			// Close the output stream
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(CommandConfigHandler.class.getName()).log(
					Level.SEVERE, null, ex);
		} finally {
			try {
				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(CommandConfigHandler.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}
}
