package com.code.aon.ui.finance.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.finance.A3Writer;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.GeyceWriter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class InvoiceExporterController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceExporterController.class.getName());
	
	private boolean showGeyceWindow;
	
	private String enterpriseCode;
	
	private String journal;
	
	public boolean isShowGeyceWindow() {
		return showGeyceWindow;
	}

	public void setShowGeyceWindow(boolean showGeyceWindow) {
		this.showGeyceWindow = showGeyceWindow;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public void onShowGeyceWindow( ActionEvent event ) {
		if (! getCheckList().isEmpty() ) {
			setShowGeyceWindow(true);	
		}
	}
	
	public void onDownloadGeyce( ActionEvent event ) {
		setShowGeyceWindow(false);
		download( new GeyceWriter(enterpriseCode, journal) );
	}

	public void onDownloadA3( ActionEvent event ) {
		download( new A3Writer() );
	}
	
	private void downloadFile( String name, byte[] data ) {
    	InputStream in = new ByteArrayInputStream(data);
        DownloadUtil.downloadAttachment(name, MimeType.MIME_TXT, in, data.length);	    				    					
	}

    private File getZipFile( Map<String,byte[]> dataMap ) throws IOException {
    	File file = File.createTempFile( "geyce", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for( Map.Entry<String,byte[]> entry : dataMap.entrySet() ) {
			byte[] data = entry.getValue();
			if (! ArrayUtils.isEmpty(data) ) {
	            zipOut.putNextEntry(new ZipEntry(entry.getKey()));
	            zipOut.write(data);
	        	zipOut.closeEntry();				
			}
		}
		zipOut.close();
		return file;
    }
	
	private void downloadZip( Map<String,byte[]> dataMap ) throws IOException {
		File zipFile = getZipFile(dataMap);
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
        DownloadUtil.downloadAttachment("geyce.zip", MimeType.MIME_ZIP, in, zipFile.length() );
	}
	
	private void download( BasicExporter exporter ) {
		try {    	
			if (! getCheckList().isEmpty() ) {
				Map<String,byte[]> dataMap = getData(exporter);
		    	if (! dataMap.isEmpty() ) {
		    		if ( dataMap.size() == 1 ) {
		    			Map.Entry<String,byte[]> entry = dataMap.entrySet().iterator().next();
		    			downloadFile(entry.getKey(), entry.getValue());
		    		} else {
		    			downloadZip(dataMap);
		    		}
		    	}
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		}
	}
	
	private Map<String,byte[]> getData( BasicExporter exporter ) throws AonException {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			Session session = HibernateUtil.getSession(sessionFactoryName);
			for( Serializable id : getCheckList() ) {
				Invoice invoice = (Invoice) session.get(Invoice.class, id);
				exporter.init( invoice );
				exporter.write();				
			}
			return exporter.getDataMap();
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionFactoryName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		    throw new AonException( t.getMessage(), t);
		} finally {
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}		
	}
	
}