package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountEntryLink;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.SocialInsuranceEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class SocialInsuranceEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = LoggerFactory.getLogger(SocialInsuranceEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	private AccountEntry accountEntry;
	private SocialInsuranceEntryHeader header;
	private String navigationKey;

	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private Balance socialInsuranceBalance;
	private List<AccountEntryDetail> socialInsuranceDetail;
	private Account socialInsuranceAccount;

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}
	
	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public Account getSocialInsuranceAccount() throws ManagerBeanException {
		if (socialInsuranceAccount == null) {
			socialInsuranceAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT); 
		}
		return socialInsuranceAccount;  
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	public SocialInsuranceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(SocialInsuranceEntryHeader header) {
		this.header = header;
	}
	
	public void onReset(ActionEvent event){
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void reset() throws ManagerBeanException {
		this.isNew = true;
		socialInsuranceAccount = null;
		this.header = initializeHeader();
	}
	
	private SocialInsuranceEntryHeader initializeHeader() throws ManagerBeanException {
		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		header.setDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
		return header;
	}
	
	public String accept(){
		return navigationKey; 	
	}

	public void onAccept(ActionEvent event) {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				this.navigationKey = "accountEntry_form";
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
				AccountEntry entry = new AccountEntry();
				if (!this.isNew) {
					deleteAccountEntryDetails(getAccountEntry());
					entry = this.getAccountEntry();
				}
				entry.setEntryDate(getHeader().getDate());
				entry.setAccountPeriod(getHeader().getPeriod().getId());
				entry.setType(AccountEntryType.SOCIAL_INSURANCE);
				entry.setSecurityLevel(getHeader().getSecurityLevel());
				if (this.isNew) {
					entry = (AccountEntry)entryBean.insert(entry);
				} else {
					entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
					entry = (AccountEntry) entryBean.update(entry);
				}
				insertEntryDetails(entry);
				AccountEntry adjust = null;
				if ( getHeader().isPaymentAdjustable()) {
					adjust = adjustSocialInsuranceEntry(entry);	
				}
				setAccountEntry(entry);
				this.isNew = false;
				
				loadAccountEntryController(entry,adjust);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				navigationKey = null;
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public void onRemove(ActionEvent event){
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				if (getHeader().getAdjustEntryLink() != null) {
					IManagerBean linkBean = BeanManager.getManagerBean(AccountEntryLink.class);
					linkBean.remove(getHeader().getAdjustEntryLink());
					deleteAccountEntryDetails(getHeader().getAdjustEntryLink().getEntryTo());
					deleteAccountEntry(getHeader().getAdjustEntryLink().getEntryTo());
				}
				deleteAccountEntryDetails(getAccountEntry());
				deleteAccountEntry(getAccountEntry());
				
				AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
				entryController.onEditSearch(null);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			Account socialInsuranceAccount = getSocialInsuranceAccount();
			Account bankAccount = null;
			if (header.getRegistryBank() != null && header.getRegistryBank().getId() != null) {
				bankAccount = getAccountBridgeUtil().obtainRBankAccount(header.getRegistryBank());
			} else {
				bankAccount = getAccountingUtil().obtainCashAccount();
			}
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(bankAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
			
			if (getHeader().getRecharge() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getHeader().getRechargeAccount());
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(bankAccount);
				detail.setConcept(getHeader().getConcept());
				detail.setDebit(getHeader().getRecharge());
				accountEntryDetailBean.insert(detail);
			}
			
			detail = new AccountEntryDetail();
			detail.setAccount(bankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getTotal());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}

	private AccountEntry adjustSocialInsuranceEntry(AccountEntry entry) throws ManagerBeanException  {
		double dif = CommonUtil.round(getSocialInsuranceBalance().getCreditBalance() - getHeader().getAmount()); 
		AccountEntry adjustEntry = null;
		IManagerBean linkBean = BeanManager.getManagerBean(AccountEntryLink.class);
		if ( dif != 0 ) {
			boolean mustInsertLink = false;
			if (!isNew()) {
				if (getHeader().getAdjustEntryLink() != null) {
					deleteAccountEntryDetails(getHeader().getAdjustEntryLink().getEntryTo());
					adjustEntry = getHeader().getAdjustEntryLink().getEntryTo();
				} else {
					adjustEntry = new AccountEntry();
					mustInsertLink = true;
				}
			} else {
				adjustEntry = new AccountEntry();
				mustInsertLink = true;
			}
			Account companySocialInsuranceAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT);
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			
			adjustEntry.setEntryDate(getSocialInsuranceBalance().getToDate()); 
			adjustEntry.setType(AccountEntryType.SOCIAL_INSURANCE_ADJUST);
			adjustEntry.setSecurityLevel(entry.getSecurityLevel() );
			adjustEntry.setAccountPeriod(entry.getAccountPeriod());
			adjustEntry = (AccountEntry) entryBean.insert(adjustEntry);
			String concept = StringUtils.abbreviate("AJUSTE " + getHeader().getConcept(),32);
			
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(adjustEntry);
			detail.setAccount(getSocialInsuranceAccount());
			detail.setLine(0);
			detail.setConcept(concept);
			detail.setDebit(dif);
			detail.setBalancingAccount(companySocialInsuranceAccount);
			entryDetailBean.insert(detail);
			
			detail = new AccountEntryDetail();
			detail.setAccountEntry(adjustEntry);
			detail.setAccount(companySocialInsuranceAccount);
			detail.setLine(1);
			detail.setConcept(concept);
			detail.setCredit(dif);
			detail.setBalancingAccount(getSocialInsuranceAccount());
			entryDetailBean.insert(detail);

			if (mustInsertLink) {
				AccountEntryLink link = new AccountEntryLink();
				link.setEntryFrom(entry);
				link.setEntryTo(adjustEntry);
				linkBean.insert(link);
			}
		} else {
			if (!isNew()) {
				linkBean.remove(getHeader().getAdjustEntryLink());
				deleteAccountEntryDetails(getHeader().getAdjustEntryLink().getEntryTo());
				deleteAccountEntry(getHeader().getAdjustEntryLink().getEntryTo());
			}
		}
		return adjustEntry;
	}

	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}
	
	private void loadAccountEntryController(AccountEntry entry,AccountEntry adjust) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		Expression expr1 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
		if (adjust == null) {
			criteria.addExpression(expr1);
		} else {
			Expression expr2 = ExpressionUtilities.getEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), adjust.getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		if (entry == null ) {
			throw new IllegalArgumentException("entry must not be null.");
		}
		AccountEntryLink adjustEntryLink = null;
		IManagerBean linkBean = BeanManager.getManagerBean(AccountEntryLink.class);
		Criteria criteria = new Criteria();
		if (entry.getType() == AccountEntryType.SOCIAL_INSURANCE_ADJUST) {
			String alias = linkBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_LINK_ENTRY_TO_ID);
			criteria.addEqualExpression(alias, entry.getId());
			List<ITransferObject> list = linkBean.getList(criteria);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanException("Imposible encontrar el apunte de Seg. Social vinculado.");
			}
			adjustEntryLink = (AccountEntryLink)list.get(0);			
			entry = adjustEntryLink.getEntryFrom();
		} else {
			String alias = linkBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_LINK_ENTRY_FROM_ID);
			criteria.addEqualExpression(alias, entry.getId());
			List<ITransferObject> list = linkBean.getList(criteria);
			if (list != null && list.size() > 0) {
				adjustEntryLink = ( (AccountEntryLink)list.get(0) );
			}
		}
		loadSocialInsuranceEntry(entry,adjustEntryLink);
	}
	
	private void loadSocialInsuranceEntry(AccountEntry entry, AccountEntryLink adjustEntryLink) throws ManagerBeanException{
		onReset(null);
		setNew(false);
		setAccountEntry(entry);

		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setPeriod(new Period());
		header.getPeriod().setId(entry.getAccountPeriod());
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "570*");
		if (accountEntryDetail == null) {
			accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			header.setRegistryBank(getAccountBridgeUtil().obtainRBank(accountEntryDetail.getAccount().getId()));
		}
		header.setConcept(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		header.setAmount(accountEntryDetail.getCredit());

		AccountEntryDetail rechargeDetail  = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "6*");
		if (rechargeDetail != null) {
			header.setRechargeAccount(rechargeDetail.getAccount());
			double a = rechargeDetail.getDebit()==0?rechargeDetail.getCredit():rechargeDetail.getDebit();
			header.setRecharge(a);
		}
		
		if (adjustEntryLink != null) {
			Date date = adjustEntryLink.getEntryTo().getEntryDate();
			Month month = Month.getMonthByValue(CommonUtil.getMonth(date));
			header.setMonth(month);
			header.setPaymentAdjustable(true);
			header.setAdjustEntryLink(adjustEntryLink);
		}
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_social_insurance_entry";
	}
	
	public boolean isPaymentAdjustDisabled() {
		boolean a = 
		(!getHeader().isPaymentAdjustable() ||
			getHeader().getMonth() == null );
		return a;
	}
	
	public void onChangeMonth(ValueChangeEvent event) {
		setSocialInsuranceBalance(null);
		setSocialInsuranceDetail(null);
	}
	
	public Balance getSocialInsuranceBalance() {
		try {
			if (socialInsuranceBalance == null) {
				int year = Integer.parseInt( getHeader().getPeriod().getId() );
				Date fromDate = CommonUtil.getDate(year, getHeader().getMonth().getValue(), 1);
				int days = CommonUtil.daysInMonth(fromDate);
				Date toDate = CommonUtil.getDate(year, getHeader().getMonth().getValue(), days);
				
				IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
				IManagerBean linkBean = BeanManager.getManagerBean(AccountEntryLink.class);
				String dateAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
				String typeAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE);
				String periodAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD);
				String accountAlias = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
				String accountEntryFromAlias = linkBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_LINK_ENTRY_FROM_ID);
				Criteria c = new Criteria();
				c.addEqualExpression(periodAlias, getHeader().getPeriod().getId() );
				c.addBetweenExpression(dateAlias, fromDate,toDate);
				c.addEqualExpression(accountAlias, getSocialInsuranceAccount().getId() );
				c.addExpression(ExpressionUtilities.getNotEqualExpression(typeAlias, AccountEntryType.SOCIAL_INSURANCE_ADJUST));
				Balance balance = new Balance();
				balance.setAccount(getSocialInsuranceAccount().getId());
				balance.setDescription(getSocialInsuranceAccount().getDescription());
				balance.setFromDate(fromDate);
				balance.setToDate(toDate);
				List<ITransferObject> list = bean.getList(c);
				socialInsuranceDetail = new LinkedList<AccountEntryDetail>();
				for (ITransferObject to :list) {
					AccountEntryDetail detail = (AccountEntryDetail) to;
					boolean add = true;
					if (detail.getAccountEntry().getType() == AccountEntryType.SOCIAL_INSURANCE) {
						// Si existe un apunte de seguridad social en el periodo, es ncesario saber si 
						// el apunte de ajuste generado por él, es del mismo periodo, para tenerlo en 
						// cuenta.
						Criteria c1 = new Criteria();
						c1.addEqualExpression(accountEntryFromAlias, detail.getAccountEntry().getId());
						List<ITransferObject> links = linkBean.getList(c1);
						if (links != null && links.size() > 0) {
							AccountEntryLink link =  (AccountEntryLink) links.get(0);
							AccountEntry linked = link.getEntryTo();
							Date date = linked.getEntryDate();
							add = 
								((DateUtils.isSameDay(fromDate, date) || fromDate.before(date) ) && 
								 (DateUtils.isSameDay(toDate, date) || toDate.after(date) ));
						} 
					}
					if (add) {	
						balance.addBalance(detail);
						socialInsuranceDetail.add(detail);
					}
				}
				setSocialInsuranceBalance( balance );
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener el saldo de la cuenta.";
			AonUtil.addErrorMessage(msg);
		}
		return socialInsuranceBalance;
	}
	public void setSocialInsuranceBalance(Balance balance) {
		this.socialInsuranceBalance = balance;
	}	

	public List<AccountEntryDetail> getSocialInsuranceDetail() {
		return socialInsuranceDetail;
	}

	public void setSocialInsuranceDetail(List<AccountEntryDetail> socialInsuranceDetail) {
		this.socialInsuranceDetail = socialInsuranceDetail;
	}
}

