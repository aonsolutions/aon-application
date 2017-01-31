package com.code.aon.ui.finance.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceExporterController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceExporterController.class.getName());
	
	private boolean scored = false;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	public boolean isScored() {
		return scored;
	}

	public void setScored(boolean scored) {
		this.scored = scored;
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
		ExporterController ec = (ExporterController) AonUtil.getRegisteredBean(IFinanceConstants.EXPORTER_CONTROLLER_NAME);
		BasicExporter exporter = ec.start(); 
		obtainData( exporter );
		ec.setDataMap(exporter.getDataMap());
		ec.finish();
		if ( ! this.scored ) {
			super.onSearch(event);
		}
	}
	
	private void exportInvoice( BasicExporter exporter, Invoice invoice ) {
		LogPanelController log = LogPanelController.getInstance();
		log.info(AonUtil.getMessage(ICommonMessages.FINANCE_INVOICE_EXPORT, exporter.getType().getName(AonUtil.getCurrentLocale()), invoice.getReferenceCode()) );
		try {
			exporter.init( invoice );
			exporter.write();
		} catch ( Throwable e ) {
			String msg = AonUtil.getMessage(ICommonMessages.FINANCE_INVOICE_EXPORT_ERROR, invoice.getReferenceCode(), e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);			
		}		
	}
	
	private void recordInvoice( String sessionName, Invoice invoice ) {
		LogPanelController log = LogPanelController.getInstance();
		try {
			log.info(AonUtil.getMessage(ICommonMessages.FINANCE_INVOICE_RECORD, invoice.getReferenceCode()) );
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
			String msg = AonUtil.getMessage(ICommonMessages.FINANCE_INVOICE_RECORD_ERROR, invoice.getReferenceCode(), e.getMessage());
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
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}		    
		    log.error(AonUtil.getMessage(ICommonMessages.FINANCE_INVOICES_EXPORT_ERROR, t.getMessage()));
		} finally {
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