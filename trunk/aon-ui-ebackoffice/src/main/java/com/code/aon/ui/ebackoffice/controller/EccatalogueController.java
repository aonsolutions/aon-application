package com.code.aon.ui.ebackoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.enumeration.CatalogueType;
import com.code.aon.product.ItemAttachment;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.BasicController;


public class EccatalogueController extends BasicController {
	private static final Logger LOGGER = Logger
	.getLogger(EccatalogueController.class.getName());
	
	/** The uploaded file. */
	private AonFile aonFile;
	
	private List<SelectItem> catalogueTypes;
	
	
	public List<SelectItem> getCatalogueTypes() {
		if(catalogueTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			catalogueTypes = new LinkedList<SelectItem>();
			for (CatalogueType e : CatalogueType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				catalogueTypes.add(item);
				
			}
		}
		
		return catalogueTypes;
	}


	public AonFile getAonFile() {
		return aonFile;
	}


	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	
	
	private void writeAttachment(AonFile file,HttpServletResponse response) {
		try {
			String filename = aonFile.getFileName();
		
			response.setHeader("Content-disposition", "attachment;filename=\""
					+ filename + "\"");
			byte[] data = aonFile.getData();
			response.setHeader("Content-Length", String.valueOf(data.length));
			ServletOutputStream sos = response.getOutputStream();
			sos.write(data);
			sos.close();
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void downloadImage(ActionEvent event)
			throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get("index");
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		AonFile file= (AonFile)getManagerBean().get(Integer.valueOf(id));
		writeAttachment(file, response);
		context.responseComplete();
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
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
		try {
			Integer id = (Integer) data;
			Eccatalogue e = (Eccatalogue) getManagerBean().get(id);
			if (e != null && e.getCatalogueImg() != null) {
				out.write(e.getCatalogueImg());	
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
	} 

	
	

	
}
