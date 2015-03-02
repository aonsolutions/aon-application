package com.code.aon.ui.finance.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.sign.controller.CertificateController;
import com.code.aon.ui.sign.controller.ISignConstants;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceSignerController extends BasicController implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceSignerController.class.getName());
	
	private Set<Integer> checks = new HashSet<Integer>();

	public void checkAll(ActionEvent event) throws ManagerBeanException{
		List<ITransferObject> list = this.getManagerBean().getList(this.getCriteria());
		for (ITransferObject to : list ){
			Invoice invoice = (Invoice) to;
			checks.add( invoice.getId() );
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to.getId());
	}

	public void setRowChecked(boolean rowChecked) {
		Integer id = ((Invoice) model.getRowData()).getId();		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
	}

	public Set<Integer> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new HashSet<Integer>();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		clearCheckedInvoices();
	}
	
	public boolean isModelToSigned() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return checks.contains(invoice);
		}
		return true;
	}

	public boolean isSelectionEmpty() {
		return this.checks.isEmpty();
	}
	
	private boolean isSignFacturae( Invoice invoice, SignerController signer ) throws ManagerBeanException {
		if ( InvoiceController.isIncludeFacturae(invoice) ) {
			return ! signer.hasSignedFacturae(invoice.getId());
		}
		return false;
	}
	
	public void onSignSelected(ActionEvent event){
		CertificateController cc = (CertificateController) AonUtil.getRegisteredBean(ISignConstants.CERTIFICATE_CONTROLLER);
		if (! cc.resolveCertificado() ) {
			return;
		}
		SignerController signer = (SignerController) AonUtil.getRegisteredBean(SALE_INVOICE_SIGNER_CONTROLLER_NAME);
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			Iterator<Integer> iter = getCheckedInvoices().iterator();
			while(iter.hasNext()){
				Integer id = iter.next();
				try {
					Invoice invoice = (Invoice) getManagerBean().get(id);
					IAttachment attachPDF = null;
					IAttachment attachXML = null;
					if (! invoice.isSigned() ) {
						attachPDF = signer.getReport(invoice);
					}
					if ( isSignFacturae(invoice, signer) ) {
						attachXML = signer.getSignatureController().getUnsignedAttachment(invoice, MimeType.MIME_XML);
					}
					if ( (attachPDF != null) || (attachXML != null) ) {	
						HibernateUtil.beginTransaction(sessionName);
						
						if ( attachPDF != null ) {
							signer.sign(invoice, attachPDF, true);	
						}
						if ( attachXML != null ) {
							signer.sign(invoice, attachXML, true);
						}

						HibernateUtil.getSession(sessionName).flush();					
						HibernateUtil.commitTransaction(sessionName);
					}
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.error(msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.error(msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			cc.setShowSignWindow(false);
		}
	}
	
	public void onCancelSignSelected(ActionEvent event) {
		SignerController signer = (SignerController) AonUtil.getRegisteredBean(SALE_INVOICE_SIGNER_CONTROLLER_NAME);
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			Iterator<Integer> iter = getCheckedInvoices().iterator();
			while(iter.hasNext()){
				Integer id = iter.next();
				try {
					HibernateUtil.beginTransaction(sessionName);

					Invoice invoice = (Invoice) getManagerBean().get(id);
					signer.cancelSign(invoice, true);

					HibernateUtil.getSession(sessionName).flush();					
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.error(msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.error(msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
			clearCheckedInvoices();
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
}