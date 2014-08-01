package com.code.aon.ui.accounting.controller.report;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.AccountRegeneratorController;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class JournalReportController extends BasicController implements IAccountingBookItem {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	private Date fromDate;
	private Date toDate;
	private Date date;
	private String account;
	private String accountDescription;
	private String balancingAccount;
	private String concept;
	private String document;
	private String debit;
	private String credit;
	private int order;
	//private boolean journal;
	private boolean journalCorrect;
	private Integer previousAccountEntryDetail;
	private Integer previousAccountEntry;
	private boolean currentValue = true;
	private boolean odd = true;
	private boolean coverVisible = false;
	private boolean counterVisible = false;
	private int pageCounter = 0;
	private SecurityLevel securityLevel;

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	
	public boolean isCoverVisible() {
		return coverVisible;
	}
	public void setCoverVisible(boolean coverVisible) {
		this.coverVisible = coverVisible;
	}

	public boolean isCounterVisible() {
		return counterVisible;
	}
	public void setCounterVisible(boolean counterVisible) {
		this.counterVisible = counterVisible;
	}

	public int getPageCounter() {
		return pageCounter;
	}
	public void setPageCounter(int pageCounter) {
		this.pageCounter = pageCounter;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}
	public String getBalancingAccount() {
		return balancingAccount;
	}
	public void setBalancingAccount(String balancingAccount) {
		this.balancingAccount = balancingAccount;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getDebit() {
		return debit;
	}
	public void setDebit(String debit) {
		this.debit = debit;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public void onReset(ActionEvent event) {
		initialize();
		super.onReset(event);
	}

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isJournal() {
		return (order == 2);
	}

	public boolean isJournalCorrect() {
		return journalCorrect;
	}
	public void setJournalCorrect(boolean journalCorrect) {
		this.journalCorrect = journalCorrect;
	}

	private void initialize() {
		try {
			setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			setPeriod(null);
		}
		setAccount(null);
		setAccountDescription(null);
		setBalancingAccount(null);
		setConcept(null);
		setDocument(null);
		setDebit(null);
		setCredit(null);
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setSecurityLevel(AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
		setOrder(1);
		setJournalCorrect(true);
		setPageCounter(0);
		setCounterVisible(false);
		setCoverVisible(false);
		previousAccountEntry = null;
		previousAccountEntryDetail = null;
		odd = true;
	}
	public void onBack(ActionEvent event) {
		super.onEditSearch(event);
		setModel(null);
	}

	public void onEditSearch(ActionEvent event) {
		initialize();
		super.onEditSearch(event);
	}

	public void onSearch(ActionEvent event) {
		try {
			previousAccountEntryDetail = null;
			previousAccountEntry= null;
			Criteria criteria = getCriteria();
			if (period != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID),period.getId());
			}
			if (getFromDate() != null) {
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE),getFromDate());
			}
			if (getToDate() != null) {
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE),getToDate());
			}
			if (StringUtils.isNotEmpty(getAccount())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.account.code",getAccount());
			}
			if (StringUtils.isNotEmpty(getAccountDescription())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.account.description",getAccountDescription());
			}
			if (StringUtils.isNotEmpty(getBalancingAccount())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.balancingAccount.code",getBalancingAccount());
			}
			if (StringUtils.isNotEmpty(getCredit())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.credit",getCredit());
			}
			if (StringUtils.isNotEmpty(getDebit())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.debit",getDebit());
			}
			if (StringUtils.isNotEmpty(getConcept())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.concept",getConcept());
			}
			if (StringUtils.isNotEmpty(getDocument())) {
				criteria.addExpression("AccountEntryDetail.accountEntry.detail.documentNumber",getDocument());
			}
			if (getSecurityLevel() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL), getSecurityLevel());
			}
			getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID));
			if (getOrder() == 0 ) {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID));
			} else if (getOrder() == 1) {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE));
			} else {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_JOURNAL));
			}
			super.onSearch(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public boolean isOdd() {
		return odd;
	}
	
	public boolean isFirstEntry() {
		try {
			AccountEntryDetail acd = (AccountEntryDetail) getModel().getRowData();
			if (previousAccountEntryDetail == null || !previousAccountEntryDetail.equals(acd.getId())) {
				previousAccountEntryDetail = acd.getId();
				Integer current = acd.getAccountEntry().getId();
				if (previousAccountEntry == null) {
					previousAccountEntry = current;
					currentValue = true;
				} else {
					if (!previousAccountEntry.equals(current)) {
						previousAccountEntry = current;
						odd = !odd;
						currentValue = true;
					} else {
						currentValue = false;
					}
				}
			}
			return currentValue;
		} catch (java.lang.IllegalArgumentException e) {
			return currentValue;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onAccountEntry(ActionEvent event) {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IEntityAlias.ACCOUNT_ENTRY_ID), detail.getAccountEntry().getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(IAccountingConstants.JOURNAL_LIST_NAVKEY);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onChangeOrder(ActionEvent event) {
		try {
			if (isJournal()) {
				IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
				Criteria c = new Criteria();
				if (getPeriod() != null) {
					c.addEqualExpression( bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), getPeriod().getId());	
				}
				c.addNullExpression( bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_JOURNAL));
				int count = bean.getCount(c);
				setJournalCorrect(count==0);
			}
		} catch (ManagerBeanException e) {
			// Nada, saldrá el listado con el número de diario a NULL.
		}
	}
	public void onRegenerate(ActionEvent event) {
		AccountRegeneratorController arc = (AccountRegeneratorController) AonUtil.getRegisteredBean("accountRegenerator");
		arc.onEditSearch(event);
		arc.setJournal(true);
		arc.setPeriod(getPeriod());
		arc.setSecurityLevel(null);
		arc.regenerateAccount(event);
		onChangeOrder(event);
	}
}
