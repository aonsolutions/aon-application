package com.code.aon.ui.accounting.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
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
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class FinanceEntryController {

	private static final Logger LOGGER = Logger.getLogger(FinanceEntryController.class.getName());
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	private AccountEntryFinanceWriter writer;

	private AccountEntry accountEntry;

	private boolean isNew;

	private Boolean payment;

	private Date date;

	private RegistryBank registryBank;

	private String concept;

	private Company company;

	private DataModel lines;

	private DataModel finances;

	private ArrayList<Finance> lineChecks = new ArrayList<Finance>();

	private ArrayList<Finance> financeChecks = new ArrayList<Finance>();

	public AccountEntryFinanceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
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

	public DataModel getLines() {
		if (lines == null) {
			lines = new ListDataModel(new LinkedList<Finance>());
		}
		return lines;
	}

	public void setLines(DataModel lines) {
		this.lines = lines;
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
		this.accountEntry = null;
		this.isNew = true;
		initializeHeader();
		clearCheckedLines();
		clearCheckedFinances();
		setLines(new ListDataModel(new LinkedList<Finance>()));
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

	@SuppressWarnings("unchecked")
	public double getTotal() {
		double total = 0.0;
		Iterator<Finance> iterator = ((List)lines.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = iterator.next();
			total += finance.getTotalAmount();
		}
		return total;
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
            Expression existingLinesIdsExpr = obtainExistingLinesIds(financeBean);
            if (existingLinesIdsExpr != null) {
                criteria.addExpression(obtainExistingLinesIds(financeBean));
            }
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES));
            criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER));
            this.finances = new ListDataModel(financeBean.getList(criteria));
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading Finance model", e);
        }
    }

	@SuppressWarnings("unchecked")
	private Expression obtainExistingLinesIds(IManagerBean bean) throws ManagerBeanException {
		Expression expression = null;
		Iterator iterator = ((List)lines.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			Expression idExpression = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_ID), finance.getId());
			expression = ExpressionUtilities.getAndExpression(expression, idExpression);
		}
		return expression;
	}

	@SuppressWarnings("unchecked")
	private List orderFinanceList(List financeList) {
		class FinanceComparator implements Comparator {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof Finance && o2 instanceof Finance) {
					Finance finance1 = (Finance)o1;
					Date date1 = finance1.getDueDate();
					String referenceCode1 = finance1.getInvoice().getReferenceCode();
					Finance finance2 = (Finance)o2;
					Date date2 = finance2.getDueDate();
					String referenceCode2 = finance2.getInvoice().getReferenceCode();
					return (date1.compareTo(date2) == 0) ? referenceCode1.compareTo(referenceCode2) : date1.compareTo(date2);
				}
				return 0;
			}
		}

		Collections.sort(financeList, new FinanceComparator());
		return financeList;
	}

	@SuppressWarnings("unchecked")
	public void onAddSelected(ActionEvent event) {
        Iterator iterator = getCheckedFinances().iterator();
        while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			((List)lines.getWrappedData()).add(finance);
			((List)finances.getWrappedData()).remove(finance);
        }
        orderFinanceList((List)lines.getWrappedData());

        clearCheckedLines();
        clearCheckedFinances();
	}

    @SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) {
        Iterator iterator = getCheckedLines().iterator();
        while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			((List)lines.getWrappedData()).remove(finance);
			((List)finances.getWrappedData()).add(finance);
        }
        orderFinanceList((List)finances.getWrappedData());

        clearCheckedLines();
        clearCheckedFinances();
	}

    @SuppressWarnings("unchecked")
    public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			if (!this.isNew) {
				accountEntry = (AccountEntry)HibernateUtil.getSession(sessionName).merge(accountEntry);
				deleteFinanceTracking(false);
				deleteAccountEntryDetails();
				mergeAccountEntry();
			}

			FinanceRecordingTo recordingTo = new FinanceRecordingTo();
			recordingTo.setType((getPayment().booleanValue())?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION);
			recordingTo.setDate(getDate());
			recordingTo.setRegistryBank(getRegistryBank());
			recordingTo.setBalancingConcept(getConcept());
			recordingTo.setSecurityLevel(SecurityLevel.OFFICIAL);
			recordingTo.setFinanceList((List)lines.getWrappedData());
			accountEntry = getWriter().recordFinances(recordingTo, accountEntry);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator<Finance> iterator = ((List)lines.getWrappedData()).iterator();
			while (iterator.hasNext()) {
				Finance finance = iterator.next();
				finance = (Finance)HibernateUtil.getSession(sessionName).merge(finance);
				if (!hasThisTracking(finance)) {
					finance.setFinanceStatus(FinanceStatus.PAID);
					financeBean.update(finance);
	
					String message = AonUtil.getMessage(IAccountingMessages.BUNDLE_KEY, IAccountingMessages.FINANCE_TRACKING_RECORDED) + " " + accountEntry.getId();
					FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getDate(), FinanceTrackingType.RECORDED, message);
					getWriter().insertAccountEntryFinanceTracking(accountEntry, tracking);
				}
			}

			this.isNew = false;
			clearCheckedLines();
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

	public void onRemove(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

	    	deleteFinanceTracking(true);
	    	deleteAccountEntryDetails();
	    	deleteAccountEntry();

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo borrar el apunte contable. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

    @SuppressWarnings("unchecked")
	private void deleteFinanceTracking(boolean removing) throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator iterator = accountEntryFTrackingBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
				FinanceTracking financeTracking = accountEntryFinanceTracking.getFinanceTracking();
				Finance finance = financeTracking.getFinance();

				if (FinanceTrackingWriter.isLastTracking(financeTracking)) {
					accountEntryFTrackingBean.remove(accountEntryFinanceTracking);
					fTrackingBean.remove(financeTracking);
					finance.setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(finance)?FinanceStatus.RETURNED:FinanceStatus.PENDING));
					financeBean.update(finance);
				} else {
					if (removing) {
						String message = AonUtil.getMessage("accountingBundle", "accounting_finance_payment_remove_error");
						throw new AbortProcessingException(message);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails() throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator iterator = accountEntryDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iterator.next();
				accountEntryDetailBean.remove(accountEntryDetail);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAccountEntry() throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator iterator = accountEntryBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntry accountEntry = (AccountEntry)iterator.next();
				accountEntryBean.remove(accountEntry);
			}
		}
	}

	private void mergeAccountEntry() throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntry.setAccountPeriod(AccountUtil.obtainPeriod(getDate()).getId());
		accountEntry.setEntryDate(getDate());
		accountEntry = (AccountEntry)accountEntryBean.update(accountEntry);
	}

	public boolean isLastTracking() throws ManagerBeanException {
		if (accountEntry != null && lines.isRowAvailable()) {
			Finance finance = (Finance)lines.getRowData();
			return isLastTracking(finance);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean isLastTracking(Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), finance.getId());
		Iterator iterator = accountEntryFTrackingBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
			return FinanceTrackingWriter.isLastTracking(accountEntryFinanceTracking.getFinanceTracking());
		}
		return true;
	}

	private boolean hasThisTracking(Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), finance.getId());
		return (accountEntryFTrackingBean.getCount(criteria) > 0);
	}

	public void onViewAccountEntry(ActionEvent event) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), accountEntry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String m = "Error loading AccountEntryController";
			AonUtil.addErrorMessage(m);
			LOGGER.log(Level.SEVERE, m, e);
		}
	}

	/**
	 * FINANCE CHECK LIST CONTROL 
	 */

	public void rowSelectedFinances(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowCheckedFinances(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowCheckedFinances() {
		Finance to = (Finance) finances.getRowData();
		return financeChecks.contains(to);
	}
	
	public void setRowCheckedFinances(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) finances.getRowData();
			if (!financeChecks.contains(to)) {
				financeChecks.add(to);
			}
		} else {
			Finance to = (Finance) finances.getRowData();
			if (financeChecks.contains(to)) {
				financeChecks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return financeChecks;
	}
	
	public void clearCheckedFinances() {
		financeChecks = new ArrayList<Finance>();
	}

	@SuppressWarnings("unchecked")
	public void checkAllFinances(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = ((List)finances.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!financeChecks.contains(finance)) {
				financeChecks.add(finance);
			}
		}
	}

	public void checkNoneFinances(ActionEvent event) {
		clearCheckedFinances();
	}

	/**
	 * LINE CHECK LIST CONTROL 
	 */

	public void rowSelectedLines(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowCheckedLines(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowCheckedLines() {
		Finance to = (Finance) lines.getRowData();
		return lineChecks.contains(to);
	}
	
	public void setRowCheckedLines(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) lines.getRowData();
			if (!lineChecks.contains(to)) {
				lineChecks.add(to);
			}
		} else {
			Finance to = (Finance) lines.getRowData();
			if (lineChecks.contains(to)) {
				lineChecks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedLines() {
		return lineChecks;
	}
	
	public void clearCheckedLines() {
		lineChecks = new ArrayList<Finance>();
	}

	@SuppressWarnings("unchecked")
	public void checkAllLines(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = ((List)lines.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!lineChecks.contains(finance) && isLastTracking(finance)) {
				lineChecks.add(finance);
			}
		}
	}

	public void checkNoneLines(ActionEvent event) {
		clearCheckedLines();
	}

}
