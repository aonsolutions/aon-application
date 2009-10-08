package com.code.aon.ui.accounting.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.accounting.IAccountingMessages;
import com.code.aon.ui.util.AonUtil;

public class FinanceEntryController {

	private static final Logger LOGGER = Logger.getLogger(FinanceEntryController.class.getName());

	private AccountEntryFinanceWriter writer;

	private boolean isNew;

	private Boolean payment;

	private Date date;

	private RegistryBank registryBank;

	private String concept;

	private Company company;

	private DataModel finances;

	private ArrayList<Finance> checks = new ArrayList<Finance>();

	public AccountEntryFinanceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public double getFinanceTotal() {
		double total = 0.0;
		Iterator<Finance> iterator = checks.iterator();
		while (iterator.hasNext()) {
			Finance finance = iterator.next();
			total += finance.getTotalAmount();
		}
		return total;
	}

	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					setCompany((Company) iter.next());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error obtaining Company!");
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DataModel getFinances() {
		if (finances == null) {
			finances = new ListDataModel(new LinkedList<Finance>());
		}
		return finances;
	}

	public void setFinances(DataModel finances) {
		this.finances = finances;
	}


	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	private void reset() throws ManagerBeanException {
		this.isNew = true;
		initializeHeader();
		clearCheckedFinances();
		setFinances(new ListDataModel(new LinkedList<Finance>()));
	}

	private void initializeHeader() throws ManagerBeanException {
		payment = null;
		date = new Date();
		registryBank = null;
		concept = null;
	}

	public List<SelectItem> getTypes() {
		List<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(new Boolean(false), "A");
		types.add(item);
		item = new SelectItem(new Boolean(true), "B");
		types.add(item);

		return types;
	}

	public List<SelectItem> getCompanyRegistryBanks() {
		return getRegistryBanks(getCompany());
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getRegistryBanks(Registry registry) {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, StringUtils.abbreviate(rBank.getBank().getName(), 30)
						+ " [" + rBank.getBankAccount().toString() + "]");
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks", e);
		}
		return rBanks;
	}

	public void onTypeChanged(ValueChangeEvent event) {
		Boolean payment = (Boolean)event.getNewValue();
		if (payment != null) {
			loadAvailableFinances(payment.booleanValue());
		}
	}

	public void loadAvailableFinances(boolean payment) {
        try {
        	IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
            Expression amountExpr = ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), new Double(0));
            Expression expensesExpr = ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_EXPENSES), new Double(0));
            criteria.addExpression(ExpressionUtilities.getOrExpression(amountExpr, expensesExpr));
            Expression pendingExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
            Expression returnedExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
            criteria.addExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES));
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER));
            this.finances = new ListDataModel(financeBean.getList(criteria));
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading Finance model", e);
        }
    }

	public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			FinanceRecordingTo recordingTo = new FinanceRecordingTo();
			recordingTo.setType((getPayment().booleanValue())?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION);
			recordingTo.setDate(getDate());
			recordingTo.setRegistryBank(getRegistryBank());
			recordingTo.setBalancingConcept(getConcept());
			recordingTo.setSecurityLevel(SecurityLevel.OFFICIAL);
			recordingTo.setFinanceList(checks);
			AccountEntry entry = getWriter().recordFinances(recordingTo, null);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator<Finance> iterator = checks.iterator();
			while (iterator.hasNext()) {
				Finance finance = iterator.next();
				finance.setFinanceStatus(FinanceStatus.PAID);
				financeBean.update(finance);

				String message = AonUtil.getMessage(IAccountingMessages.BUNDLE_KEY, IAccountingMessages.FINANCE_TRACKING_RECORDED) + " " + entry.getId();
				FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getDate(), FinanceTrackingType.RECORDED, message);
				getWriter().insertAccountEntryFinanceTracking(entry, tracking);
			}

			this.isNew = false;
			clearCheckedFinances();

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo generar el apunte contable. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}


	/**
	 * CHECK LIST CONTROL 
	 */

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Finance to = (Finance) finances.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) finances.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) finances.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}

}