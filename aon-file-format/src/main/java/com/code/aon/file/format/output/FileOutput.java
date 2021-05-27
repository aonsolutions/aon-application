package com.code.aon.file.format.output;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.code.aon.AonVersion;

public class FileOutput implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<Exception> errors;
	private File file;
	private byte[] content;

	public List<Exception> getErrors() {
		return errors;
	}

	public void setErrors(List<Exception> errors) {
		this.errors = errors;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
}