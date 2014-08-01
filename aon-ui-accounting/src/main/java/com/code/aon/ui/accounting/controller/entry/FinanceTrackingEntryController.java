package com.code.aon.ui.accounting.controller.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.richfaces.model.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.SortOrderMap;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceTrackingEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceTrackingEntryController.class.getName());

	private AccountEntryFinanceWriter writer;

	private Boolean payment;
	private Period period;
	private SecurityLevel securityLevel;
	private String onGenerateKey;
	
	private DataModel lines;
	private DataModel finances;
	private List<FinanceTracking> lineChecks;
	private List<FinanceTracking> financeChecks;
	private List<Integer> accountEntries;
	private SortOrderMap order;
	
	public FinanceTrackingEntryController() {
		this.lineChecks = new ArrayList<FinanceTracking>();
		this.financeChecks = new ArrayList<FinanceTracking>();
	}

	public AccountEntryFinanceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
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

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public DataModel getLines() {
		if (lines == null) {
			lines = new SerializableListDataModel(new LinkedList<FinanceTracking>());
		}
		return lines;
	}
	public void setLines(DataModel lines) {
		this.lines = lines;
	}

	public DataModel getFinances() {
		if (finances == null) {
			finances = new SerializableListDataModel(new LinkedList<FinanceTracking>());
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
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	private void reset() throws ManagerBeanException {
		initializeHeader();
		clearCheckedLines();
		clearCheckedFinances();
		setLines(new SerializableListDataModel(new LinkedList<FinanceTracking>()));
		setFinances(new SerializableListDataModel(new LinkedList<FinanceTracking>()));
	}

	private void initializeHeader() throws ManagerBeanException {
		payment = null;
		period = (period != null && period.getId() != null) ? period : AccountingPeriodUtil.getDefaultPeriod();
		securityLevel = (securityLevel != null) ? securityLevel : SecurityLevel.OFFICIAL;
	}

	private void resetOrder(){
		this.order = new SortOrderMap();
		this.order.put(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE, Ordering.ASCENDING);
		this.order.put(IEntityAlias.FINANCE_TRACKING_FINANCE_INVOICE_REFERENCE_CODE, Ordering.ASCENDING);
		this.order.put(IEntityAlias.FINANCE_TRACKING_FINANCE_CONCEPT, Ordering.ASCENDING);
	}

	public List<SelectItem> getTypes() {
		List<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(new Boolean(false), "A");
		types.add(item);
		item = new SelectItem(new Boolean(true), "B");
		types.add(item);

		return types;
	}

	public void onTypeChanged(ActionEvent event) {
		Boolean payment = getPayment();
		if (payment != null) {
			loadAvailableFinances(payment.booleanValue());
		}
	}

	public void loadAvailableFinances(boolean payment) {
        try {
        	IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_PAYMENT), new Boolean(payment));
            String ftType = financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE);
            Expression paidExp = ExpressionUtilities.getEqualExpression(ftType, FinanceTrackingType.PAID); 
            Expression returnedExp = ExpressionUtilities.getEqualExpression(ftType, FinanceTrackingType.RETURNED); 
            criteria.addExpression(ExpressionUtilities.getOrExpression(paidExp, returnedExp));
            criteria.addNullExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_BANK_STATEMENT_LINK));
            criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_RECORDED), false);
            if (!AonUtil.getRoleManager().isConfidentiality()) {
            	criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_SECURITY_LEVEL), SecurityLevel.OFFICIAL);	
            } else {
            	criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_SECURITY_LEVEL), getSecurityLevel());
            }
            criteria.addOrder(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE));
            criteria.addOrder(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_INVOICE_REFERENCE_CODE));
            criteria.addOrder(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_CONCEPT));
            resetOrder();
            this.finances = new SerializableListDataModel(financeTrackingBean.getList(criteria));
        } catch (ManagerBeanException e) {
            LOGGER.error("Error loading Finance model", e);
        }
    }


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onAddSelected(ActionEvent event) {
        for (FinanceTracking ft: getCheckedFinances() ) {
			((List)lines.getWrappedData()).add(ft);
			((List)finances.getWrappedData()).remove(ft);
        }
        clearCheckedLines();
        clearCheckedFinances();
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onRemoveSelected(ActionEvent event) {
        for (FinanceTracking ft: getCheckedLines() ) {
			((List)lines.getWrappedData()).remove(ft);
			((List)finances.getWrappedData()).add(ft);
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
			accountEntries = new LinkedList<Integer>();			
			List<FinanceTracking> list = (List<FinanceTracking>) lines.getWrappedData();
			for (FinanceTracking ft : list) {
				AccountEntry entry = getWriter().recordFinanceTracking(ft);
				if (entry != null) {
					accountEntries.add(entry.getId());	
				}
			}
			clearCheckedLines();
			clearCheckedFinances();
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			onViewAccountEntry(event);
			onGenerateKey = IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY;
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
 
	public void onViewAccountEntry(ActionEvent event) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			Expression exp = null;
			for (int i = 0;i< accountEntries.size();i++) {
				Integer id = accountEntries.get(i);
				Expression current = ExpressionUtilities.getEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), id);
				if (i == 0) {
					exp = current;
				} else {
					exp = ExpressionUtilities.getOrExpression(exp, current);
				}
			}
			criteria.addExpression(exp);	
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
		FinanceTracking to = (FinanceTracking) finances.getRowData();
		return financeChecks.contains(to);
	}
	
	public void setRowCheckedFinances(boolean rowChecked) {
		if (rowChecked) {
			FinanceTracking to = (FinanceTracking) finances.getRowData();
			if (!financeChecks.contains(to)) {
				financeChecks.add(to);
			}
		} else {
			FinanceTracking to = (FinanceTracking) finances.getRowData();
			if (financeChecks.contains(to)) {
				financeChecks.remove(to);
			}
		}
	}
	
	public List<FinanceTracking> getCheckedFinances() {
		return financeChecks;
	}
	
	public void clearCheckedFinances() {
		financeChecks = new ArrayList<FinanceTracking>();
	}

	@SuppressWarnings("unchecked")
	public void checkAllFinances(ActionEvent event) {
		List<FinanceTracking> list = (List<FinanceTracking>)finances.getWrappedData();
		for (FinanceTracking ft : list) {
			if (!financeChecks.contains(ft)) {
				financeChecks.add(ft);
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
		FinanceTracking to = (FinanceTracking) lines.getRowData();
		return lineChecks.contains(to);
	}
	
	public void setRowCheckedLines(boolean rowChecked) {
		if (rowChecked) {
			FinanceTracking to = (FinanceTracking) lines.getRowData();
			if (!lineChecks.contains(to)) {
				lineChecks.add(to);
			}
		} else {
			FinanceTracking to = (FinanceTracking) lines.getRowData();
			if (lineChecks.contains(to)) {
				lineChecks.remove(to);
			}
		}
	}
	
	public List<FinanceTracking> getCheckedLines() {
		return lineChecks;
	}
	
	public void clearCheckedLines() {
		lineChecks = new ArrayList<FinanceTracking>();
	}

	@SuppressWarnings("unchecked")
	public void checkAllLines(ActionEvent event) throws ManagerBeanException {
		List<FinanceTracking> list = (List<FinanceTracking>)lines.getWrappedData();
		for (FinanceTracking ft : list) {
			if (!lineChecks.contains(ft)) {
				lineChecks.add(ft);
			}
		}
	}

	public void checkNoneLines(ActionEvent event) {
		clearCheckedLines();
	}

	public String generate() {
		return onGenerateKey;
	}

	public Map<String, Ordering> getOrder() {
		return order;
	}
	
}
