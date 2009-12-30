package com.code.aon.account.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountSummary;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development
 *
 */
public class AccountSummaryManager {

    /**
     * @param entryDetail
     * @param factor
     * @throws ManagerBeanException
     */
    public static void modifyAccountSummary(AccountEntryDetail entryDetail, int factor) throws ManagerBeanException {
        AccountSummary accountSummary;
        String period = entryDetail.getAccountEntry().getAccountPeriod();
        Account account = entryDetail.getAccount();
        Date entryDate = entryDetail.getAccountEntry().getEntryDate();
        double debit = entryDetail.getDebit() * factor;
        double credit = entryDetail.getCredit() * factor;

        IManagerBean summaryBean = BeanManager.getManagerBean(AccountSummary.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(summaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ACCOUNT_PERIOD), period);
        criteria.addEqualExpression(summaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ACCOUNT_ID), account.getId());
        criteria.addEqualExpression(summaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ENTRY_DATE), entryDate);
        Iterator iterator = summaryBean.getList(criteria).iterator();
        if (iterator.hasNext()) {
            accountSummary = (AccountSummary)iterator.next();
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(entryDate);

            accountSummary = new AccountSummary();
            accountSummary.setAccountPeriod(period);
            accountSummary.setAccount(account);
            accountSummary.setEntryDate(entryDate);
            accountSummary.setEntryMonth(calendar.get(Calendar.MONTH) + 1);
        }
        accountSummary.setDebit(accountSummary.getDebit() + debit);
        accountSummary.setCredit(accountSummary.getCredit() + credit);

        if (accountSummary.getId() == null) {
            summaryBean.insert(accountSummary);
        } else {
            summaryBean.update(accountSummary);
        }
    }

}
