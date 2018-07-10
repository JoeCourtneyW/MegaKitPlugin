package com.sly.main.resources;
//A class with instance variables for input stream and output streams

//(two, one for appending, one for overwriting)

//	File I/O Step 1
//for java.io.File;
//for java.io.PrintWriter;
//for java.io.FileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileIO
{
	private Scanner inputStream;
	private PrintWriter outputStream;
	private File file;

	public FileIO(File file) {
		this.file = file;
	}

	public void connectAsInputFile() {
		try {
			inputStream = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file: " + file.getName());
			System.exit(0x0);
		}
	}

	public void connectAsOutputFile(boolean append) {
		try {
			outputStream = new PrintWriter(new FileOutputStream(file, append));
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file: " + file.getName() + ", creating it");
			try {
				if (!file.exists()) {
					file.createNewFile();
					connectAsOutputFile(append);
				}
			} catch (IOException e1) {
				System.out.println("Unable to create file: " + file.getName());
				System.exit(0x0);
			}
		}
	}

	public String readLines() {
		StringBuilder sb = new StringBuilder("");
		while (inputStream.hasNextLine()) {
			sb.append(inputStream.nextLine() + "\n");
		}
		return sb.toString();
	}

	public Scanner getReader() {
		return inputStream;
	}
	public PrintWriter getWriter() {
		return outputStream;
	}
	public int totalLines() {
		int total = 0;
		while (inputStream.hasNextLine()) {
			inputStream.nextLine();
			total++;
		}
		return total;
	}

	public void writeLines(String... lines) {
		for (String line : lines) {
			outputStream.println(line);
		}
	}

	public void writeLines(Scanner stream) {
		while (stream.hasNextLine()) {
			outputStream.println(stream.nextLine());
		}
	}
	public void write(String contents) {
		outputStream.print(contents);
	}

	public void closeStream() {
		if (inputStream != null)
			inputStream.close();
		if (outputStream != null)
			outputStream.close();
	}
}
