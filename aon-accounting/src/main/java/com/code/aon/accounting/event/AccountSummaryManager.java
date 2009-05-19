package com.code.aon.accounting.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development
 *
 */
public class AccountSummaryManager {

	public static void addAccountSummary(AccountSummary accountSummary) throws ManagerBeanException {
		IManagerBean accountSummaryBean = BeanManager.getManagerBean(AccountSummary.class);
		accountSummaryBean.insert(accountSummary);
	}

	@SuppressWarnings("unchecked")
	public static void modifyAccountSummary(AccountEntryDetail accountEntryDetail, int factor) throws ManagerBeanException {
        AccountSummary accountSummary;
        String period = accountEntryDetail.getAccountEntry().getAccountPeriod();
        Account account = accountEntryDetail.getAccount();
        SecurityLevel securityLevel = accountEntryDetail.getAccountEntry().getSecurityLevel();
        Date entryDate = accountEntryDetail.getAccountEntry().getEntryDate();
        double debit = accountEntryDetail.getDebit() * factor;
        double credit = accountEntryDetail.getCredit() * factor;

        IManagerBean accountSummaryBean = BeanManager.getManagerBean(AccountSummary.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ACCOUNT_PERIOD), period);
        criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ACCOUNT_ID), account.getId());
        criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_SECURITY_LEVEL), securityLevel);
        criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ENTRY_DATE), entryDate);
        Iterator iterator = accountSummaryBean.getList(criteria).iterator();
        if (iterator.hasNext()) {
            accountSummary = (AccountSummary)iterator.next();
        } else {
            accountSummary = new AccountSummary();
            accountSummary.setAccountPeriod(period);
            accountSummary.setAccount(account);
            accountSummary.setSecurityLevel(securityLevel);
            accountSummary.setEntryDate(entryDate);
        }
        accountSummary.setDebit(CommonUtil.round(accountSummary.getDebit() + debit, 2));
        accountSummary.setCredit(CommonUtil.round(accountSummary.getCredit() + credit, 2));

        if (accountSummary.getId() == null) {
        	accountSummaryBean.insert(accountSummary);
        } else {
        	if (accountSummary.getDebit() > 0 || accountSummary.getCredit() > 0) {
        		accountSummaryBean.update(accountSummary);
        	} else {
        		accountSummaryBean.remove(accountSummary);
        	}
        }
    }

	public static void deleteAccountSummary(Period accountPeriod) {
		String delete = "delete from AccountSummary as summary";
		delete += (accountPeriod != null)?" where summary.accountPeriod = '" + accountPeriod.getId() + "'":"";
        Session session = HibernateUtil.getSession(null);
        Transaction t = session.beginTransaction();
        Query query = session.createQuery(delete);
        int rows = query.executeUpdate();
        t.commit();
        session.flush();
        System.out.println( "Filas borradas: "  +  rows );
	}

	@SuppressWarnings("unchecked")
	public static void regenerateAccountSummary(Period accountPeriod, IProgression progressionBean) throws ManagerBeanException {
		deleteAccountSummary(accountPeriod);

		String select = "select entryDetail.account, entry.securityLevel, entry.entryDate, " +
						"sum(entryDetail.debit), sum(entryDetail.credit) " +
						"from AccountEntry as entry, AccountEntryDetail as entryDetail " +
						"where entry.id = entryDetail.accountEntry.id " +
						"and entry.accountPeriod = '" + accountPeriod.getId() + "' " + 
						"group by entryDetail.account, entry.securityLevel, entry.entryDate ";
        Session session = HibernateUtil.getSession(null);
        Query query = session.createQuery(select);
        List list = query.list();
        int count = list.size();
        int i = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();

        	AccountSummary accountSummary = new AccountSummary();
        	accountSummary.setAccountPeriod(accountPeriod.getId());
        	accountSummary.setAccount((Account)obj[0]);
        	accountSummary.setSecurityLevel((SecurityLevel)obj[1]);
        	accountSummary.setEntryDate((Date)obj[2]);
        	accountSummary.setDebit(CommonUtil.round(((Double)obj[3]).doubleValue(),2));
        	accountSummary.setCredit(CommonUtil.round(((Double)obj[4]).doubleValue(),2));
        	addAccountSummary(accountSummary);
        	i++;    	
        	progressionBean.setProgressionCurrentValue((long) ( i * 100 / count));
        }
	}

}
