package com.code.aon.faces.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class FileWrapper {

	private static final DecimalFormat BYTES_FORMAT = new DecimalFormat("0 bytes");
	
	private static final DecimalFormat KB_FORMAT = new DecimalFormat("0.## KB");
	
	private static final DecimalFormat MB_FORMAT = new DecimalFormat("0.## MB");
	
	private static final DecimalFormat GB_FORMAT = new DecimalFormat("0.## GB");
	
	private File file;
	
	public FileWrapper( File file ) {
		this.file = file;
	}	
	
	public File getWrappedObject() {
		return file;
	}

	public boolean isFile() {
		return this.file.isFile();
	}
	
	public String getStyleClass() {
		if ( file.isDirectory() ) {
			return "aon-icon-folder";
		}
		return "aon-icon-csv";
	}

	public String getName() {
		return this.file.getName();
	}

	public void setName(String name) {
		if (! StringUtils.equals(this.file.getName(), name) ) {
			File newFile = new File( this.file.getParentFile(), name );
			this.file.renameTo( newFile );
		}
	}
	
	public String getPermissions() {
		StringBuffer sb = new StringBuffer();
		if ( file.canRead() ) {
			sb.append( "R" );
		}
		if ( file.canWrite() ) {
			sb.append( "W" );
		}
		if ( file.canExecute() ) {
			sb.append( "X" );
		}
		return sb.toString();
	}		
	

	private String getDisplaySize( long value ) {
		String result = "";
		double size = value;
		if ( size != -1 ) {
			if ( size < FileUtils.ONE_KB ) {
				result = BYTES_FORMAT.format(size);
			} else if ( size < FileUtils.ONE_MB ) {
				result = KB_FORMAT.format(size / FileUtils.ONE_KB);
			} else if ( size < FileUtils.ONE_GB ) {
				result = MB_FORMAT.format(size / FileUtils.ONE_MB);
			} else {
				result = GB_FORMAT.format(size / FileUtils.ONE_GB);
			}
		}
		return result;		
	}
	
	public String getLength() {
		if ( ! file.isDirectory() ) {
			return getDisplaySize( file.length() );
		}
		return null;
	}	
	
	public Date getLastModified() {
		return new Date( file.lastModified() );
	}
	
}
