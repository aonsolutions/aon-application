package com.code.aon.ui.common.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;


/**
 * @author ecastellano
 * 
 */
public class AonFile {

	private List<IAonFileListener> listeners = new ArrayList<IAonFileListener>();
	private byte[] data;
	private String fileName;

	/**
	 * @return byte[]
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data 
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return int
	 * @throws IOException 
	 */
	public int getSize() throws IOException {
		byte[] data = getData();
		if (data != null) {
			return data.length; 
		}
		return 0;
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

	/**
	 * @return the file
	 */

	public String getKey() {
		return this.toString();
	}


	/**
	 * @param event
	 */
	public void fileDeleted(ActionEvent event){
		for (IAonFileListener l: listeners) {
			l.fileDeleted(this);
		}
	}

	/**
	 * @param l
	 */
	public void addAonFileListener(IAonFileListener l){
		listeners.add(l);
	}

	/**
	 * @param l
	 */
	public void removeAonFileListener(IAonFileListener l){
		listeners.remove(l);
	}
	
}


