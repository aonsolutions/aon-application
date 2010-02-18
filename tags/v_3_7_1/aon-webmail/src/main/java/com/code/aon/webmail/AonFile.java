package com.code.aon.webmail;

import java.io.File;

public class AonFile {

	private File file;

	private String fileName;

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
    /**
     * Sets the file name.
     * 
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }	
	
}
