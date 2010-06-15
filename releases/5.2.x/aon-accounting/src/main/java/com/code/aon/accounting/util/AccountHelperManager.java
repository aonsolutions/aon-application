package com.code.aon.accounting.util;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;

public class AccountHelperManager {

	private IManagerBean bean;
	private String accountAlias;
	private String balancingAccountAlias;

	public IManagerBean getBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AccountHelper.class);
		}
		return bean;
	}

	public String getAccountAlias() throws ManagerBeanException {
		if (accountAlias == null) {
			accountAlias = getBean().getFieldName(IAccountingAlias.ACCOUNT_HELPER_ACCOUNT_ID);
		}
		return accountAlias;
	}

	public String getBalancingAccountAlias() throws ManagerBeanException {
		if (balancingAccountAlias == null) {
			balancingAccountAlias = getBean().getFieldName(IAccountingAlias.ACCOUNT_HELPER_BALANCING_ACCOUNT_ID);
		}
		return balancingAccountAlias;
	}

	public Integer addOccurrence(Account account, Account balancingAccount) throws ManagerBeanException {
		return updateOccurrence(account, balancingAccount, 1);
	}
	
	public Integer subtractOccurrence(Account account, Account balancingAccount) throws ManagerBeanException {
		return updateOccurrence(account, balancingAccount, -1);
	}
	
	private Integer updateOccurrence(Account account, Account balancingAccount, int i) throws ManagerBeanException {
		if (account != null && balancingAccount != null) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getAccountAlias(), account.getId());
			criteria.addEqualExpression(getBalancingAccountAlias(), balancingAccount.getId());
			List<ITransferObject> list = getBean().getList(criteria);
			if (list != null && list.size() > 0) {
				AccountHelper ah = (AccountHelper) list.get(0);
				int c = (ah.getCounter()==null?0:ah.getCounter()) + i;
				ah.setCounter( c );
				if (c == 0) {
					getBean().remove(ah);
					return 0;
				}
				ah = (AccountHelper) getBean().update(ah);
				return ah.getCounter();
			} 
			AccountHelper ah = new AccountHelper();
			ah.setAccount(account);
			ah.setBalancingAccount(balancingAccount);
			ah.setCounter( 1 );
			ah = (AccountHelper) getBean().insert(ah);
			return ah.getCounter();
		}
		return null;
	}

	private void deleteAccountHelper() {
		String delete = "delete from AccountHelper as ah";
        Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
        Transaction t = session.beginTransaction();
        Query query = session.createQuery(delete);
        int rows = query.executeUpdate();
        t.commit();
        session.flush();
        System.out.println( "Filas borradas: "  +  rows );
	}

	@SuppressWarnings("unchecked")
	public void regenerateAccountHelper(IProgression progressionBean) throws ManagerBeanException {
		deleteAccountHelper();

		IManagerBean accountHelperBean = BeanManager.getManagerBean(AccountHelper.class);
		String select = "select detail.account, detail.balancingAccount, count(*)" +
						" from AccountEntryDetail as detail " +
						" where detail.balancingAccount is not null" +
						" group by detail.account, detail.balancingAccount ";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
        Query query = session.createQuery(select);
        List list = query.list();
        int count = list.size();
        int i = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();
        	AccountHelper ah = new AccountHelper();
        	ah.setAccount((Account)obj[0]);
        	ah.setBalancingAccount((Account) obj[1]);
        	Long c = (Long)obj[2];
        	ah.setCounter(c.intValue());
    		accountHelperBean.insert(ah);
        	i++;    	
        	progressionBean.setProgressionCurrentValue((long) ( i * 100 / count));
        }
	}
}
