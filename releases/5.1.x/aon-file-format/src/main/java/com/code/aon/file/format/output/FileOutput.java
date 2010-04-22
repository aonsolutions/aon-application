package com.code.aon.file.format.output;

import java.io.File;
import java.util.ArrayList;

public class FileOutput {

	private ArrayList<Exception> errors;
	private File file;

	public ArrayList<Exception> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<Exception> errors) {
		this.errors = errors;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}