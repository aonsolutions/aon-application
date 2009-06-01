package com.code.aon.ui.webmail.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.webmail.listener.IAonFileListener;

public class AonFile{

	private File file;

    private List<IAonFileListener> listeners = new ArrayList<IAonFileListener>();

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
	
    public void fileDeleted(ActionEvent event){
    	for (IAonFileListener l: listeners) {
    		l.fileDeleted(this);
    	}
    }

    public void addAonFileListener(IAonFileListener l){
    	listeners.add(l);
    }
    
    public void removeAonFileListener(IAonFileListener l){
    	listeners.remove(l);
    }
	
}
