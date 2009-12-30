package es.code.cdr.ui.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import es.code.ecm.util.DocumentUpload;

public class DocumentUploadBean implements Serializable {
	
	private static final long serialVersionUID = -5834366175408005806L;

	private List<DocumentUpload> documents = new ArrayList<DocumentUpload>();
	private int uploadsAvailable = 5;
	private int selected = -1;

	public DocumentUploadBean() {
	}

	public List<DocumentUpload> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentUpload> documents) { 
		this.documents = documents;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public DocumentUpload getSelectedDocument() {
		return documents.get( selected );
	}

	public int getSize() {
		return documents.size();
	}

	public void listener(UploadEvent event) throws IOException{
	    UploadItem item = event.getUploadItem();
	    DocumentUpload doc = new DocumentUpload();
	    doc.setLength( item.getData().length );
	    Calendar lastModified = Calendar.getInstance();
		lastModified.setTimeInMillis( ( new Date() ).getTime() );
	    doc.setLastModified( lastModified );
	    doc.setName( getFileName( item.getFileName() ) );
	    doc.setData( item.getData() );
	    documents.add( doc );
	    uploadsAvailable--;
	}
	
	public void clearUploadData(ActionEvent event) {
		documents.clear();
		setUploadsAvailable(5);
	    selected = -1;
	}
	
	public void clearSelectedUploadData() {
	    documents.remove( selected );
	    uploadsAvailable++;
	    selected = -1;
	}

	private String getFileName(String path) {
		int index = path.lastIndexOf( "\\" );
		if ( index == -1 )
			index = path.lastIndexOf( "/" );
		return path.substring( index + 1, path.length() );
	}
}
