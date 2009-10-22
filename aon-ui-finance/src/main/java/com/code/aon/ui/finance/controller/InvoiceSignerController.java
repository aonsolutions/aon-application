package com.code.aon.ui.finance.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceSignerController extends BasicController implements IFinanceConstants {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceSignerController.class.getName());
	
	private Set<Integer> checks = new HashSet<Integer>();

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
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
	
	public void onSignSelected(ActionEvent event){
		SignerController signer = (SignerController) AonUtil.getRegisteredBean(SALE_INVOICE_SIGNER_CONTROLLER_NAME);
		if (! signer.resolveCertificado() ) {
			return;
		}
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
					if (! invoice.isSigned() ) {
						byte[] pdfData = signer.getReport(invoice);
						
						HibernateUtil.beginTransaction(sessionName);
						
						signer.sign(invoice, pdfData, true);

						HibernateUtil.getSession(sessionName).flush();					
						HibernateUtil.commitTransaction(sessionName);
					}
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.log(Level.SEVERE, msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.log(Level.SEVERE, msg, e);
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
			signer.setShowSignWindow(false);
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
						LOGGER.log(Level.SEVERE, msg, e);
					}
					String msg =  "Error recording invoice:  " + id;
					LOGGER.log(Level.SEVERE, msg, e);
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