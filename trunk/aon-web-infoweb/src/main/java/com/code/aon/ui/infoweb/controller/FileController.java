package com.code.aon.ui.infoweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.common.io.IAonFileListener;
import com.code.aon.ui.form.BasicController;

public class FileController extends BasicController implements IAonFileListener {

	/** The uploaded file. */
	private AonFile aonFile;

	private long maximumSize = -1;
	

	public FileController() {
		this.maximumSize = -1;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = 524288;
	}

	public IAttachment getAttachment() {
		return (IAttachment) getTo();
	}
	
	/**
	 * Gets the uploaded file.
	 * 
	 * @return the file
	 */
	public AonFile getAonFile() {
		return this.aonFile;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			f.addAonFileListener(this);
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void fileDeleted(AonFile aonFile) {
		setAonFile(null);
	}

	public void updateFile( String fileName, byte[] data, MimeType mimeType ) {
		IAttachment attach = getAttachment();
		attach.setData( data );
		attach.setMimeType(mimeType);
		if ( StringUtils.isEmpty(attach.getDescription()) ) {
			int index = fileName.lastIndexOf(".");
			attach.setDescription((index==-1?fileName:fileName.substring(0, index)));
		}
	}
	
	public void fileRemove( ActionEvent ent ) throws IOException {
		IAttachment attach = getAttachment();
		if ( attach != null ) {
			attach.setData( null );
			attach.setMimeType( null );
		}
	}

	public byte[] getFileData() throws IOException {
    	IAttachment attach = getAttachment();
		if ( attach != null ) {
			return attach.getData();
		}
    	return null; 
    }
    
    public String getMimeType() {
    	IAttachment attach = getAttachment();
		if ( (attach != null) && (attach.getMimeType() != null) ) {
			Random r = new Random();
			String mimetype = attach.getMimeType().getName()+r.nextInt();
			return mimetype;
		}
    	return MimeType.MIME_JPEG.getName(); 
    }
    
    public void paint(OutputStream out, Object data) throws IOException {
    	if ( getFileData() != null ) {
    		out.write( getFileData() );
    	}
    }

}