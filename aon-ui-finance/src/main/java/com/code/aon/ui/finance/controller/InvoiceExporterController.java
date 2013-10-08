package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EXPORT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EXPORT_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_RECORD;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_RECORD_ERROR;

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

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ui.finance.A3Writer;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.GeyceWriter;
import com.code.aon.ui.finance.InvoiceExportConfiguration;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceExporterController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceExporterController.class.getName());
	
	private boolean scored = true;
	
	private InvoiceExportConfiguration configuration;
	
	private String backAction;
	
	private boolean finished;
	
	private Map<String,byte[]> dataMap;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	public boolean isScored() {
		return scored;
	}

	public void setScored(boolean scored) {
		this.scored = scored;
	}
	
	public InvoiceExportConfiguration getConfiguration() {
		return configuration;
	}
	
	public String initialAction() {
		if (! this.configuration.isConfigured() ) {
			this.backAction = searchAction();
			return formAction();
		}
		return searchAction();
	}
	
	public String backAction() {
		return backAction;
	}	

	public void onInit( ActionEvent event ) {
		this.configuration = new InvoiceExportConfiguration();
		this.finished = false;
		this.dataMap = null;
		onEditSearch(event);
	}

	public void onEditConfiguration( ActionEvent event ) {
		this.backAction = listAction();
	}
	
	public void onSaveConfiguration( ActionEvent event ) {
		this.configuration.save();
	}
	
	public boolean isFinished() {
		return this.finished;
	}

	@Override
	public void onSearch(ActionEvent arg0) { 
		try {
			InvoiceStatus status = scored ? InvoiceStatus.SCORED : InvoiceStatus.PENDING;
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_STATUS), status);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		super.onSearch(arg0);
	}
	
	public void onStart( ActionEvent event ) {
		this.dataMap = null;
		this.finished = false;
		switch ( this.configuration.getType() ) {
			case GEYCE:
				obtainData( new GeyceWriter(this.configuration) );
				break;
			case A3:
				obtainData( new A3Writer(this.configuration) );
				break;
		}
		this.finished = true;
		if ( ! this.scored ) {
			super.onSearch(event);
		}
	}
	
	public void onDownload( ActionEvent event ) {
    	if ((dataMap != null) && !dataMap.isEmpty() ) {
    		try {
	    		if ( dataMap.size() == 1 ) {
	    			Map.Entry<String,byte[]> entry = dataMap.entrySet().iterator().next();
	    			downloadFile(entry.getKey(), entry.getValue());
	    		} else {
	    			downloadZip(dataMap);
	    		}
    		} catch (Throwable th) {
    			LOGGER.error(th.getMessage(), th);
    			AonUtil.addErrorMessage(th.getMessage());
    			throw new AbortProcessingException(th.getMessage(), th);
    		}
    	}
		onFinish(event);
	}
	
	public void onFinish( ActionEvent event ) {
    	this.finished = false;
    	this.dataMap = null;
    	LogPanelController.getInstance().onCloseWindow(event);
	}
	
	private void downloadFile( String name, byte[] data ) {
    	InputStream in = new ByteArrayInputStream(data);
        DownloadUtil.downloadAttachment(name, null, in, data.length);	    				    					
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
	
	private void exportInvoice( BasicExporter exporter, Invoice invoice ) {
		LogPanelController log = LogPanelController.getInstance();
		log.info(AonUtil.getMessage(FINANCE_INVOICE_EXPORT, configuration.getType().getName(AonUtil.getCurrentLocale()), invoice.getReferenceCode()) );
		try {
			exporter.init( invoice );
			exporter.write();
		} catch ( Throwable e ) {
			String msg = AonUtil.getMessage(FINANCE_INVOICE_EXPORT_ERROR, invoice.getReferenceCode(), e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);			
		}		
	}
	
	private void recordInvoice( String sessionName, Invoice invoice ) {
		LogPanelController log = LogPanelController.getInstance();
		try {
			log.info(AonUtil.getMessage(FINANCE_INVOICE_RECORD, invoice.getReferenceCode()) );
			HibernateUtil.beginTransaction(sessionName);
			getAccountEntryInvoiceWriter().recordInvoice(invoice);
			invoice.setStatus(InvoiceStatus.SCORED);
			HibernateUtil.getSession(sessionName).merge(invoice);
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				LOGGER.error(e.getMessage(), e);
			}
			String msg = AonUtil.getMessage(FINANCE_INVOICE_RECORD_ERROR, invoice.getReferenceCode(), e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);
		}			
	}
	
	private AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}	
	
	private void obtainData( BasicExporter exporter ) {
		LogPanelController log = LogPanelController.getInstance();
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			Session session = HibernateUtil.getSession(sessionName);
			if ( ! this.scored ) {			
				for( Serializable id : getCheckList() ) {
					Invoice invoice = (Invoice) session.get(Invoice.class, id);
					if (invoice.getStatus() == InvoiceStatus.PENDING) {
						recordInvoice(sessionName, invoice);
					}
				}
			}
			HibernateUtil.closeSession(sessionName);
			session = HibernateUtil.getSession(sessionName);
			for( Serializable id : getCheckList() ) {
				Invoice invoice = (Invoice) session.get(Invoice.class, id);
				exportInvoice(exporter, invoice);
			}
			this.dataMap = exporter.getDataMap();
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}		    
		    log.error(AonUtil.getMessage(FINANCE_INVOICE_EXPORT_ERROR, t.getMessage()));
		} finally {
			log.finish();
			HibernateUtil.closeSession(sessionName);
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}		
	}
	
}