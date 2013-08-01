package com.code.aon.ui.finance.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

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

	public void onDownloadGeyce( ActionEvent event ) {
		download( new GeyceWriter() );
	}

	public void onDownloadA3( ActionEvent event ) {
		download( new A3Writer() );
	}
	
	private void download( BasicExporter exporter ) {
		try {    	
			if (! getCheckList().isEmpty() ) {
		    	byte[] data = getData(exporter);
		    	if (! ArrayUtils.isEmpty(data) ) {
			    	InputStream in = new ByteArrayInputStream(data);
			        DownloadUtil.downloadAttachment(exporter.getFileName(), MimeType.MIME_TXT, in, data.length);	    		
		    	}
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		}
	}
	
	private byte[] getData( BasicExporter exporter ) throws AonException {
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Session session = HibernateUtil.getSession(sessionFactoryName);
			for( Serializable id : getCheckList() ) {
				Invoice invoice = (Invoice) session.get(Invoice.class, id);
				exporter.init( invoice, out );
				exporter.write();				
			}
			return out.toByteArray();
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