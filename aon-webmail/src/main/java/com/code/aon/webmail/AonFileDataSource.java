package com.code.aon.webmail;

import jakarta.activation.FileDataSource;

import com.code.aon.common.util.AonFile;

public class AonFileDataSource extends FileDataSource {

	private AonFile aonFile;
	
	public AonFileDataSource(AonFile aonFile) {
		super(aonFile.getFile());
		this.aonFile = aonFile;
	}

	@Override
	public String getContentType() {
		if ( aonFile.getMimeType() != null ) {
			return aonFile.getMimeType().getName();
		}
		return super.getContentType();
	}
	
}
