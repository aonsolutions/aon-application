package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRemoverController extends BasicController implements IProgression{

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceRemoverController.class.getName());
	
	private ArrayList<Invoice> checks = new ArrayList<Invoice>();
	
	private Long progressionCurrentValue = -1L;
	private boolean progressionEnabled = false;


	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
			if(isRemovable(invoice)){
				if (!checks.contains( invoice )) {
					checks.add( invoice );
				}
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Invoice to = (Invoice) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Invoice to = (Invoice) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<Invoice> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new ArrayList<Invoice>();
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		clearCheckedInvoices();
	}
	
	public boolean isModelToRemovable() throws ManagerBeanException{
		if ( getModel().isRowAvailable() ) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return isRemovable(invoice);
		}
		return false;
	}
	
	private boolean isRemovable(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING));
		if(financeBean.getCount(criteria) == 0){
			return true;
		}
		return false;
	}

	public void onRemoveSelected(ActionEvent event){
		int count = getCheckedInvoices().size();
		if (count == 0) {
			String msg = AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.EMPTY_INVOICE_LIST_ERROR_KEY);
			throw new AbortProcessingException(msg);
		}
		setProgressionCurrentValue(0L);
		setProgressionEnabled(true);
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Iterator<Invoice> iter = getCheckedInvoices().iterator();
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );
			long i = 0;
			while(iter.hasNext()){
				Invoice invoice = iter.next();
				try {
					HibernateUtil.beginTransaction(sessionName);
					getManagerBean().remove(invoice);
					HibernateUtil.getSession(sessionName).flush();					
					HibernateUtil.commitTransaction(sessionName);
					i++;
					setProgressionCurrentValue(( i * 100 / count));
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg =  "Unable to rollback transaction!";
						LOGGER.error(msg, e);
					}
					String msg =  "Error deleting invoice:  " + invoice.getReferenceCode();
					LOGGER.error(msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			setProgressionEnabled(false);
			clearCheckedInvoices();
			this.onSearch(null);
		}
	}

	

	@Override
	public Long getProgressionCurrentValue() {
		return progressionCurrentValue;
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
		progressionCurrentValue = currentValue;
	}

	@Override
	public boolean isProgressionEnabled() {
		return this.progressionEnabled;
	}

	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}
}