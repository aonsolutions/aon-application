package com.code.aon.accounting.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountSummaryManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailSummaryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private AccountSummaryManager manager;
	
    private AccountSummaryManager getManager() {
    	if (manager == null) {
    		manager = new AccountSummaryManager();
    	}
		return manager;
	}

    @SuppressWarnings("unchecked")
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
        AccountEntryDetail accountEntryDetail = (AccountEntryDetail)evt.getTo();
        try {
			String select = "select entryDetail.account account, entry.account_period period," +
							"  entry.security_level security_level, entry.entry_date entry_date, " +
							"  entryDetail.debit debit, entryDetail.credit credit " +
							" from account_entry as entry, account_entry_detail as entryDetail " +
							" where entryDetail.id = " + accountEntryDetail.getId() +
							" and entry.id = entryDetail.account_entry ";
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			SQLQuery query = session.createSQLQuery(select);
	        List list = query
	        	.addScalar("account", Hibernate.STRING )
	        	.addScalar("period", Hibernate.STRING )
	        	.addScalar("security_level", Hibernate.BYTE )
	        	.addScalar("entry_date", Hibernate.DATE )
	        	.addScalar("debit", Hibernate.DOUBLE )
	        	.addScalar("credit", Hibernate.DOUBLE )
	        	.list();
	        Iterator iterator = list.iterator();
	        if (iterator.hasNext()) {
	        	Object[] obj = (Object[])iterator.next();
	            String accountId= (String) obj[0];
	        	String period = (String) obj[1];
	        	Byte securityLevelValue = (Byte) obj[2];
	            SecurityLevel securityLevel= SecurityLevel.values()[securityLevelValue];
	            Date entryDate= (Date) obj[3];
	            double debit=(Double) obj[4];
	            double credit=(Double) obj[5];
	            IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
	            Account account = (Account) accountBean.get(accountId);
	            if (account != null) {
	                getManager().modifyAccountSummary(period, account, securityLevel, entryDate, debit, credit, -1);
	            }
	        }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
    }

}
