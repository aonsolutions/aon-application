package com.code.ui.gbp.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.SupplierEconomicData;

public class SupplierEconomicDataController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(SupplierEconomicDataController.class.getName());

	private UploadedFile file;

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public void fileUploaded( ActionEvent ent ) throws IOException {
		if ( this.file!= null ) {
			SupplierEconomicData to = (SupplierEconomicData)getTo();
			to.setData( this.file.getBytes() );
			to.setMimeType(MimeType.get(this.file.getContentType()));
			String name = this.getFile().getName().substring(this.getFile().getName().lastIndexOf("\\") + 1, this.getFile().getName().length());
			to.setFileName(name);
		}
	}
	
	public byte[] getFileData() throws IOException {
    	SupplierEconomicData to = (SupplierEconomicData)getTo();
		if ( to != null ) {
			return to.getData();
		}
    	return null; 
    }
	
	@SuppressWarnings({"unused"})
	public void downloadFile(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
	        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

	        SupplierEconomicData to = (SupplierEconomicData)getTo();
	        OutputStream out = response.getOutputStream();
			out.write(to.getData(), 0, to.getData().length);
	        out.close();

	        String fileName = (to.getFileName());

	        response.setContentType(to.getMimeType().getName());
	        response.setContentLength(to.getData().length);
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
	        faces.responseComplete();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage("Imposible downloading file");
		}
	}
}