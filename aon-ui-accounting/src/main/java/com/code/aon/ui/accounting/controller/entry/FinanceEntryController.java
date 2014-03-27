package com.code.aon.ui.accounting.controller.entry;

import static com.code.aon.ui.common.ICommonMessages.TRACKING_RECORDED;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.richfaces.model.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.SortOrderMap;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceEntryController.class.getName());

	private AccountEntryFinanceWriter writer;
	private AccountEntry accountEntry;
	private AccountBridgeUtil accountBridgeUtil;
	private AccountingUtil accountingUtil;

	private boolean isNew;
	private Boolean payment;
	private Period period;
	private Date date;
	private int deposit;
	private RegistryBank registryBank;
	private PayMethodTypeDetail payMethodTypeDetail;
	private SecurityLevel securityLevel;
	private String concept;
	private DataScrollerState linesState;
	private DataScrollerState financesState;
	private List<Finance> lineChecks;
	private List<Finance> financeChecks;
	private String onGenerateKey;
	private SortOrderMap order;

	public FinanceEntryController() {
		this.lineChecks = new ArrayList<Finance>();
		this.financeChecks = new ArrayList<Finance>();
	}

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

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

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDeposit() {
		return deposit;
	}

	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return payMethodTypeDetail;
	}

	public void setPayMethodTypeDetail(PayMethodTypeDetail payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isConfidential() {
		return getSecurityLevel() == SecurityLevel.CONFIDENTIAL;
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL );
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public DataModel getLines() {
		return getLinesState().getDirectModel();
	}

	public void setLines(DataModel lines) {
		if ( lines == null ) {
			setLinesState(null);
		} else {
			getLinesState().setModel(lines);
		}
	}
	
	public DataScrollerState getLinesState() {
		if (linesState == null) {
			DataModel model = new ListDataModel(new LinkedList<Finance>());
			setLinesState( new DataScrollerState(model, "lines"));
		}		
		return linesState;
	}

	public void setLinesState(DataScrollerState linesState) {
		this.linesState = linesState;
	}

	public DataModel getFinances() {
		return getFinancesState().getDirectModel();
	}

	public void setFinances(DataModel finances) {
		if ( finances == null ) {
			setFinancesState(null);
		} else {
			getFinancesState().setModel(finances);
		}
	}
	
	public DataScrollerState getFinancesState() {
		if (financesState == null) {
			DataModel model = new ListDataModel(new LinkedList<Finance>());
			setFinancesState( new DataScrollerState(model, "finances"));
		}		
		return financesState;
	}

	public void setFinancesState(DataScrollerState financesState) {
		this.financesState = financesState;
	}

	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
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

	private void resetOrder(){
		this.order = new SortOrderMap();
		this.order.put(IEntityAlias.FINANCE_DUE_DATE, Ordering.ASCENDING);
		this.order.put(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE, Ordering.ASCENDING);
		this.order.put(IEntityAlias.FINANCE_CONCEPT, Ordering.ASCENDING);
	}
	
	private void initializeHeader() throws ManagerBeanException {
		payment = null;
		concept = null;
		period = (period != null && period.getId() != null) ? period : AccountingPeriodUtil.getDefaultPeriod();
		date = (date != null) ? date : new Date();
		securityLevel = (securityLevel != null) ? securityLevel : SecurityLevel.OFFICIAL;
	}

	public List<SelectItem> getTypes() {
		List<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(new Boolean(false), "A");
		types.add(item);
		item = new SelectItem(new Boolean(true), "B");
		types.add(item);

		return types;
	}

	@SuppressWarnings("unchecked")
	public double getTotal() {
		double total = 0.0;
		Iterator<Finance> iterator = ((List<Finance>)getLines().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = iterator.next();
			total += finance.getTotalAmount();
		}
		return total;
	}

	public void onTypeChanged(ActionEvent event) {
		Boolean payment = getPayment();
		if (payment != null) {
			loadAvailableFinances(payment.booleanValue());
		}
	}

	public void loadAvailableFinances(boolean payment) {
        try {
        	IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), new Boolean(payment));
            Expression amountExpr = ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT), new Double(0));
            Expression expensesExpr = ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_EXPENSES), new Double(0));
            criteria.addExpression(ExpressionUtilities.getOrExpression(amountExpr, expensesExpr));
            Expression pendingExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
            Expression returnedExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
            criteria.addExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
            if (AonUtil.getRoleManager().isConfidentiality()) {
            	criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), getSecurityLevel());	
            } else {
            	criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
            }
            Expression existingLinesIdsExpr = obtainExistingLinesIds(financeBean);
            if (existingLinesIdsExpr != null) {
                criteria.addExpression(obtainExistingLinesIds(financeBean));
            }
            criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
            criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
            criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_CONCEPT));
            resetOrder();
            setFinances(new ListDataModel(financeBean.getList(criteria)));
        } catch (ManagerBeanException e) {
            LOGGER.error("Error loading Finance model", e);
        }
    }

	private Expression obtainExistingLinesIds(IManagerBean bean) throws ManagerBeanException {
		Expression expression = null;
		Iterator<?> iterator = ((List<?>)getLines().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			Expression idExpression = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_ID), finance.getId());
			expression = ExpressionUtilities.getAndExpression(expression, idExpression);
		}
		return expression;
	}

	public int getPayMethodTypeDetailsSize() throws ManagerBeanException {
		IManagerBean payMethodTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		return payMethodTypeDetailBean.getCount(null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onAddSelected(ActionEvent event) {
        Iterator<?> iterator = getCheckedFinances().iterator();
        while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			((List)getLines().getWrappedData()).add(finance);
			((List)getFinances().getWrappedData()).remove(finance);
        }
        clearCheckedLines();
        clearCheckedFinances();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onRemoveSelected(ActionEvent event) {
        Iterator<?> iterator = getCheckedLines().iterator();
        while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			((List)getLines().getWrappedData()).remove(finance);
			((List)getFinances().getWrappedData()).add(finance);
        }
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
				deleteFinanceTracking(false);
				deleteAccountEntryDetails();
				accountEntry = (AccountEntry)HibernateUtil.getSession(sessionName).merge(accountEntry);
				mergeAccountEntry();
			}

			FinanceRecordingTo recordingTo = new FinanceRecordingTo();
			recordingTo.setType((getPayment().booleanValue())?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION);
			recordingTo.setPeriod(getPeriod());
			recordingTo.setDate(getDate());
			recordingTo.setPaymentAccount(obtainPaymentAccount());
			recordingTo.setBalancingConcept(getConcept());
			recordingTo.setSecurityLevel(getSecurityLevel());
			recordingTo.setFinanceList((List<Finance>)getLines().getWrappedData());
			accountEntry = getWriter().recordFinances(recordingTo, accountEntry);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator<Finance> iterator = ((List<Finance>)getLines().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				Finance finance = iterator.next();
				finance = (Finance)HibernateUtil.getSession(sessionName).merge(finance);
				if (!hasThisTracking(finance)) {
					finance.setFinanceStatus(FinanceStatus.PAID);
					financeBean.update(finance);
	
					String message = AonUtil.getMessage(TRACKING_RECORDED) + " " + accountEntry.getId();
					FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getDate(), FinanceTrackingType.PAID, message, 
								getDeposit()==0?getRegistryBank():null, getDeposit()==1?getPayMethodTypeDetail():null, finance.getTotalAmount(), true);
					getWriter().insertAccountEntryFinanceTracking(accountEntry, tracking);
				}
			}

			this.isNew = false;
			clearCheckedLines();
			clearCheckedFinances();

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			onViewAccountEntry(event);
			onGenerateKey = "accountEntry_form";
		} catch (Exception e) {
			onGenerateKey = null;
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo generar el apunte contable. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Account obtainPaymentAccount() throws ManagerBeanException {
		Account account = null;
		if (getDeposit() == 0 && getRegistryBank() != null) {
			account = getAccountBridgeUtil().obtainRBankAccount(getRegistryBank());
		} else if (getDeposit() == 1 && getPayMethodTypeDetail() != null) {
			account = getPayMethodTypeDetail().getAccount();
		}
		return (account!=null) ? account : getAccountingUtil().obtainCashAccount();
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
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo borrar el apunte contable. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void deleteFinanceTracking(boolean removing) throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator<?> iterator = accountEntryFTrackingBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
				FinanceTracking financeTracking = accountEntryFinanceTracking.getFinanceTracking();
				Finance finance = financeTracking.getFinance();

				accountEntryFTrackingBean.remove(accountEntryFinanceTracking);
				if (financeTracking.getBankStatementLink() == null && FinanceTrackingWriter.isLastTracking(financeTracking)) {
					fTrackingBean.remove(financeTracking);
					finance.setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(finance)?FinanceStatus.RETURNED:FinanceStatus.PENDING));
					financeBean.update(finance);
				} else {
					if (removing) {
						financeTracking.setRecorded(false);
						fTrackingBean.update(financeTracking);
					}
				}
			}
		}
	}

	private void deleteAccountEntryDetails() throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator<?> iterator = accountEntryDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iterator.next();
				accountEntryDetailBean.remove(accountEntryDetail);
			}
		}
	}

	private void deleteAccountEntry() throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator<?> iterator = accountEntryBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				AccountEntry accountEntry = (AccountEntry)iterator.next();
				accountEntryBean.remove(accountEntry);
			}
		}
	}

	private void mergeAccountEntry() throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntry.setAccountPeriod(getPeriod());
		accountEntry.setEntryDate(getDate());
		accountEntry.setSecurityLevel(getSecurityLevel());
		accountEntry = (AccountEntry)accountEntryBean.update(accountEntry);
	}

	public boolean isLastTracking() throws ManagerBeanException {
		if (getLines().isRowAvailable()) {
			Finance finance = (Finance)getLines().getRowData();
			return isLastTracking(finance);
		}
		return true;
	}

	private boolean isLastTracking(Finance finance) throws ManagerBeanException {
		if (accountEntry != null) {
			IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
			criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), finance.getId());
			Iterator<?> iterator = accountEntryFTrackingBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
				return FinanceTrackingWriter.isLastTracking(accountEntryFinanceTracking.getFinanceTracking());
			}
		}
		return true;
	}

	private boolean hasThisTracking(Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), accountEntry.getId());
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), finance.getId());
		return (accountEntryFTrackingBean.getCount(criteria) > 0);
	}

	public void onViewAccountEntry(ActionEvent event) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), accountEntry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String m = "Error loading AccountEntryController";
			AonUtil.addErrorMessage(m);
			LOGGER.error(m, e);
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
		Finance to = (Finance) getFinances().getRowData();
		return financeChecks.contains(to);
	}
	
	public void setRowCheckedFinances(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) getFinances().getRowData();
			if (!financeChecks.contains(to)) {
				financeChecks.add(to);
			}
		} else {
			Finance to = (Finance) getFinances().getRowData();
			if (financeChecks.contains(to)) {
				financeChecks.remove(to);
			}
		}
	}
	
	public List<Finance> getCheckedFinances() {
		return financeChecks;
	}
	
	public void clearCheckedFinances() {
		financeChecks = new ArrayList<Finance>();
	}

	public void checkAllFinances(ActionEvent event) {
		Iterator<?> iterator = ((List<?>)getFinances().getWrappedData()).iterator();
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
		Finance to = (Finance) getLines().getRowData();
		return lineChecks.contains(to);
	}
	
	public void setRowCheckedLines(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) getLines().getRowData();
			if (!lineChecks.contains(to)) {
				lineChecks.add(to);
			}
		} else {
			Finance to = (Finance) getLines().getRowData();
			if (lineChecks.contains(to)) {
				lineChecks.remove(to);
			}
		}
	}
	
	public List<Finance> getCheckedLines() {
		return lineChecks;
	}
	
	public void clearCheckedLines() {
		lineChecks = new ArrayList<Finance>();
	}

	public void checkAllLines(ActionEvent event) throws ManagerBeanException {
		Iterator<?> iterator = ((List<?>)getLines().getWrappedData()).iterator();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);

		IManagerBean accountEntryFBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFBatchBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_BATCH_ACCOUNT_ENTRY_ID), entry.getId());
		Iterator<?> iter = accountEntryFBatchBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			String msg = "Asiento generado automáticamente. No se puede modificar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
		setAccountEntry(entry);
		setPayment(entry.getType().equals(AccountEntryType.PAYMENT) ? true : false);
		setPeriod(entry.getAccountPeriod());
		setDate(entry.getEntryDate());
		setSecurityLevel(entry.getSecurityLevel());
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
		criteria.addOrder(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ID), false);
		iter = accountEntryDetailBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iter.next();
			if (accountEntryDetail.getAccount().getCode().substring(0, 3).equals(AccountConstants.CASH_ACCOUNT_PREFIX.substring(0, 3))) {
				setDeposit(1);
				setRegistryBank(null);
				setPayMethodTypeDetail(getAccountBridgeUtil().obtainPayMethodTypeDetail(accountEntryDetail.getAccount()));
			} else {
				setDeposit(0);
				setRegistryBank(getAccountBridgeUtil().obtainRBank(accountEntryDetail.getAccount()));
				setPayMethodTypeDetail(null);
			}

			setConcept(accountEntryDetail.getConcept());
		}

		IManagerBean accountEntryFTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID), entry.getId());
		iter = accountEntryFTrackingBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iter.next();
			((List)getLines().getWrappedData()).add(accountEntryFinanceTracking.getFinanceTracking().getFinance());
		}
		loadAvailableFinances(getPayment());
	}

	@Override
	public String getNavigationKey() {
		return "financeEntry_form";
	}

	public String generate() {
		return onGenerateKey;
	}

	public Map<String, Ordering> getOrder() {
		return order;
	}
}
