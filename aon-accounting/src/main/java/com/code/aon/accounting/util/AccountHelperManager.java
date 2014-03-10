package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountHelperManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IManagerBean bean;
	private String accountAlias;
	private String balancingAccountAlias;
	private String accountIdAlias;
	private String balancingAccountIdAlias;
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountHelperManager.class.getName()); 
	
	public IManagerBean getBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AccountHelper.class);
		}
		return bean;
	}

	public String getAccountAlias() throws ManagerBeanException {
		if (accountAlias == null) {
			accountAlias = getBean().getFieldName(IEntityAlias.ACCOUNT_HELPER_ACCOUNT_CODE);
		}
		return accountAlias;
	}

	public String getAccountIdAlias() throws ManagerBeanException {
		if (accountIdAlias == null) {
			accountIdAlias = getBean().getFieldName(IEntityAlias.ACCOUNT_HELPER_ACCOUNT_ID);
		}
		return accountIdAlias;
	}

	public String getBalancingAccountAlias() throws ManagerBeanException {
		if (balancingAccountAlias == null) {
			balancingAccountAlias = getBean().getFieldName(IEntityAlias.ACCOUNT_HELPER_BALANCING_ACCOUNT_CODE);
		}
		return balancingAccountAlias;
	}

	public String getBalancingAccountIdAlias() throws ManagerBeanException {
		if (balancingAccountIdAlias == null) {
			balancingAccountIdAlias = getBean().getFieldName(IEntityAlias.ACCOUNT_HELPER_BALANCING_ACCOUNT_ID);
		}
		return balancingAccountIdAlias;
	}

	public Integer addOccurrence(Account account, Account balancingAccount) throws ManagerBeanException {
		return updateOccurrence(account, balancingAccount, 1);
	}
	
	public Integer subtractOccurrence(Account account, Account balancingAccount) throws ManagerBeanException {
		return updateOccurrence(account, balancingAccount, -1);
	}
	
	private Integer updateOccurrence(Account account, Account balancingAccount, int i) throws ManagerBeanException {
		if (account != null && account.getId() != null && 
			balancingAccount != null && balancingAccount.getId() != null) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getAccountIdAlias(), account.getId());
			criteria.addEqualExpression(getBalancingAccountIdAlias(), balancingAccount.getId());
			List<ITransferObject> list = getBean().getList(criteria);
			if (list != null && list.size() > 0) {
				AccountHelper ah = (AccountHelper) list.get(0);
				int c = ah.getCounter() + i;
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


	public void regenerateAccountHelper() throws ManagerBeanException {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
				
				// Borrado de las filas actuales.
				String delete = "delete from AccountHelper as ah where " + DomainManager.getSQLWhereClause("ah.domain");
		        int rows = session.createQuery(delete).executeUpdate();
		        LOGGER.info( "Filas borradas: "  +  rows );

		        IManagerBean accountHelperBean = BeanManager.getManagerBean(AccountHelper.class);
				String select = "SELECT detail.account, detail.balancingAccount, count(*)" +
								" FROM AccountEntryDetail AS detail " +
								" WHERE " + DomainManager.getSQLWhereClause("detail.domain") +
								" AND detail.balancingAccount is not null" +
								" GROUP BY detail.account, detail.balancingAccount ";
				
		        Query query = session.createQuery(select);
		        List<?> list = query.list();
		        Iterator<?> iterator = list.iterator();
		        rows = 0;
		        while (iterator.hasNext()) {
		        	Object[] obj = (Object[])iterator.next();
		        	AccountHelper ah = new AccountHelper();
		        	ah.setAccount((Account)obj[0]);
		        	ah.setBalancingAccount((Account) obj[1]);
		        	Long c = (Long)obj[2];
		        	ah.setCounter(c.intValue());
		    		accountHelperBean.insert(ah);
		    		++rows;
		        }
		        LOGGER.info( "Filas insertadas: "  +  rows );
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
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
}
