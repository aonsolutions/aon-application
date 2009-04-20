package com.code.aon.ui.db.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Configuration;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.db.HibernateDataManager;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.util.AonUtil;

public class DBManager {

	private static final Logger LOGGER = Logger.getLogger(DBManager.class.getName());
	
	private List<AonFile> files;

	private void responseZip(File file) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        writeZip(response, file);
        context.responseComplete();    	
    }
    
    private void writeZip(HttpServletResponse response, File xmlFile) throws IOException {
        String outFilename = "db.zip";
        response.setContentType("application/x-zip-compressed");
		response.setHeader("Content-disposition", "attachment; filename=\""
				+ outFilename + "\"");

		File tempFile = File.createTempFile(outFilename, ".zip");
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(tempFile) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);

		InputStream in = new BufferedInputStream( new FileInputStream(xmlFile) );
		zipOut.putNextEntry(new ZipEntry("aon_master.xml"));
		IOUtils.copy( in, zipOut );
       	zipOut.closeEntry();
       	in.close();                

		zipOut.close();
        long size = tempFile.length();
        if ( size > 0 ) {
            response.setHeader("Content-Length", String.valueOf(size));	
        }
        ServletOutputStream sos = response.getOutputStream();
        InputStream fileIn = new BufferedInputStream( new FileInputStream(tempFile) );
        IOUtils.copy( fileIn, sos );
        fileIn.close();
        sos.close();
		response.flushBuffer();
		tempFile.delete();
    }
	
	public void onExport( ActionEvent event ) {
		HibernateDataManager hdm = new HibernateDataManager();
		hdm.setExportData(true);
		try {
			File xmlFile = File.createTempFile("aon_master", ".xml");
			hdm.setFile( xmlFile );
			String factoryName = HibernateUtil.getSessionFactoryName();
			Configuration cfg = HibernateUtil.getConfigurationFactory().getConfiguration(factoryName);
			hdm.setExportConfiguration( cfg );
			hdm.execute();
			responseZip( xmlFile );
			xmlFile.delete();
		} catch (Throwable e) {
			LOGGER.severe( ">>>> onExport " + e.getMessage() );
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onInitImport( ActionEvent event ) {
		this.files = new ArrayList<AonFile>();
	}
	
	public List<AonFile> getFiles() {
		return files;
	}

	public void setFiles(List<AonFile> files) { 
		this.files = files;
	}	
	
	public synchronized void fileUploaded(UploadEvent event) throws IOException {
	    UploadItem item = event.getUploadItem();
	    AonFile file = new AonFile();
	    file.setFileName(item.getFileName());
	    if ( item.isTempFile() ) {
	    	byte[] data = FileUtils.readFileToByteArray(item.getFile());
	    	file.setData(data);
	    } else {
		    file.setData(item.getData());	
	    }
	    files.add(file);	    
	}	
	
	public void fileDeleted( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		String indexValue = context.getExternalContext().getRequestParameterMap().get("index");
		if (! StringUtils.isEmpty(indexValue) ) {
			int index = Integer.valueOf(indexValue);
			this.files.remove(index);
		}
	}

	public void onImport( ActionEvent event ) {
		HibernateDataManager hdm = new HibernateDataManager();
		hdm.setImportData(true);
		try {
			String factoryName = HibernateUtil.getSessionFactoryName();
			Configuration cfg = HibernateUtil.getConfigurationFactory().getConfiguration(factoryName);
			hdm.setImportConfiguration( cfg );
			TransferObjectImportVisitor visitor = new TransferObjectImportVisitor();
			hdm.setVisitor(visitor);
			for( AonFile file : this.files ) {
				ByteArrayInputStream in = new ByteArrayInputStream(file.getData());
				hdm.setInputStream( in );
				hdm.execute();	
			}
		} catch (Throwable e) {
			LOGGER.severe( ">>>> onExport " + e.getMessage() );
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
