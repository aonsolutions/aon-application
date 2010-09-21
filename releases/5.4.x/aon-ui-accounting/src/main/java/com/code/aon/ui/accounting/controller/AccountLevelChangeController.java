package com.code.aon.ui.accounting.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.AccountBudgetDetail;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.util.AccountSummaryManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountLevelChangeController extends BasicController implements IProgression {

	private Long progressionCurrentValue = -1L;
	private boolean progressionEnabled;

	private List<String> resultList;

	public ListDataModel getResultModel() {
		return new ListDataModel(resultList);
	}

	@SuppressWarnings("unchecked")
	public void validateLevelChange(ActionEvent event) {
		resultList = new LinkedList<String>();

		String select = "SELECT SUBSTR(account.id, 1, 3) AS prefix, " +
						"IF (SUBSTR(account.id, 4, 1) = '0', SUBSTR(account.id, 5, 1), SUBSTR(account.id, 4, 1)) AS control, " +
						"COUNT(*) " +
						"FROM account " +
						"WHERE account.level = 4 " +
						"AND LENGTH(account.id) = 5 " +
						"GROUP BY prefix, control " +
						"HAVING COUNT(*) > 1 " +
						"ORDER BY 1";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		Iterator iterator = query.list().iterator();
		while (iterator.hasNext()) {
			Object[] obj = (Object[])iterator.next();
        	String prefix = (String)obj[0];
        	String control = (String)obj[1];

        	String msg = "";
        	select = "SELECT SUBSTR(account.id, 1, 5) AS id " +
        			 "FROM account " +
        			 "WHERE account.level = 4 " +
        			 "AND LENGTH(account.id) = 5 " +
        			 "AND (account.id LIKE '" + prefix + control + "%' OR account.id = '" + prefix + "0" + control+ "')";
    		query = session.createSQLQuery(select);
    		Iterator iter = query.list().iterator();
    		while (iter.hasNext()) {
    			String account = (String)iter.next();
    			msg += ((!msg.equals("")) ? "-" : "") + account;
    		}
    		resultList.add(msg + " se fusionarán en la cuenta " + prefix + control + ".");
		}

    	select = "SELECT SUBSTR(account.id, 1, 3) AS prefix, " +
    			 "IF (SUBSTR(account.id, 4, 1) = '0', SUBSTR(account.id, 5, 1), SUBSTR(account.id, 4, 1)) AS control1, " +
    			 "IF (level = 4, " +
    			 		"CONCAT('0000', IF (SUBSTR(account.id, 4, 1) = '0', SUBSTR(account.id, 4, 1), SUBSTR(account.id, 5, 1))), " +
    			 		"SUBSTR(account.id, 8, 5)) AS control2, " +
    			 "COUNT(*) " +
    			 "FROM account " +
				 "WHERE account.level >= 4 " +
				 "AND account.entryEnabled = 1 " +
				 "AND LENGTH(account.id) IN (5, 12) " +
				 "GROUP BY prefix, control1, control2 " +
				 "HAVING COUNT(*) > 1 " +
				 "ORDER BY 1";
		query = session.createSQLQuery(select);
		iterator = query.list().iterator();
		while (iterator.hasNext()) {
			Object[] obj = (Object[])iterator.next();
        	String prefix = (String)obj[0];
        	String control1 = (String)obj[1];
        	String control2 = (String)obj[2];
        	String accPrefix1 = prefix + control1 + control2.substring(4);
        	String accPrefix2 = prefix + control2.substring(4) + control1;

        	String msg = "";
        	select = "SELECT SUBSTR(account.id, 1, LENGTH(id)) AS id, account.level AS level " +
        			 "FROM account " +
        			 "WHERE account.level >= 4 " +
    				 "AND account.entryEnabled = 1 " +
        			 "AND ((LENGTH(account.id) = 5 AND account.id IN ('" + accPrefix1 + "', '" + accPrefix2 + "')) " +
   			 			"OR (LENGTH(account.id) = 12 " +
   			 			"AND (account.id LIKE '" + accPrefix1 + "%" + control2 + "' OR account.id LIKE '" + accPrefix2 + "%" + control2 + "')))";
        	query = session.createSQLQuery(select);
    		Iterator iter = query.list().iterator();
    		while (iter.hasNext()) {
    			Object[] obj2 = (Object[])iter.next();
    			String account = (String)obj2[0];
    			int level = ((Byte)obj2[1]).intValue();
    			msg = account + " se " + ((level==4) ? "extenderá" : "traspasará") + " a la cuenta " + prefix + control1 + control2 + ".";
    			resultList.add(msg);
    		}
		}

    	select = "SELECT SUBSTR(account.id, 1, 12) AS id " +
				 "FROM account " +
				 "WHERE account.level = 5 " +
				 "AND LENGTH(account.id) = 12 " +
				 "AND SUBSTR(account.id, 6, 2) != '00' " +
				 "ORDER BY 1";
		query = session.createSQLQuery(select);
		iterator = query.list().iterator();
		while (iterator.hasNext()) {
			String account = (String)iterator.next();
			resultList.add(account + " se convertirá en la cuenta " + account.substring(0, 5) + account.substring(7) + ".");
		}

		if (resultList.size() == 0) {
			resultList.add("La validación ha sido correcta.");
		}
	}

	public void processLevelChange(ActionEvent event) {
		resultList = new LinkedList<String>();
		try {
			setProgressionCurrentValue(0L);
			changeLevel();
			setProgressionCurrentValue(-1L);
			AonUtil.addInfoMessage("La conversión de Cuentas se han realizado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido realizar la conversión de Cuentas. Causa: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void changeLevel() throws ManagerBeanException {
		AccountSummaryManager manager = new AccountSummaryManager();
		manager.deleteAccountSummary(null);

		String select = "SELECT account.id, account.description, account.alias, account.entryEnabled, account.level " +
						"FROM Account as account " +
						"ORDER BY account.id ";
        Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
        Query query = session.createQuery(select);
        List list = query.list();
        int count = list.size();
        int i = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();

        	Account account = new Account();
        	account.setId((String)obj[0]);
        	account.setDescription((String)obj[1]);
        	account.setAlias((String)obj[2]);
        	account.setEntryEnabled(((Boolean)obj[3]).booleanValue());
        	account.setLevel(((Integer)obj[4]).intValue());

        	int level = account.getLevel();
    		boolean entryEnabled = account.isEntryEnabled();
        	if (level < 4 && entryEnabled) {
    			disableEntryEnabled(account);
    			if (level == 2) {
    				createAccount(account, 3, "");
    			}
    			createAccount(account, 4, "");
    			Account newAccount = createAccount(account, 5, "");
    			if (!changeAccount(account, newAccount)) {
    				deleteAccount(newAccount);
    			}
        	} else if (level == 4 && account.getId().length() == 5) {
        		Account newAccount = createAccount(account, 4, "");
        		if (entryEnabled) {
        			String suffix = (account.getId().substring(3, 4).equals("0")) ? "0" : account.getId().substring(4, 5);
        			newAccount = createAccount(newAccount, 5, suffix);
        			if (!changeAccount(account, newAccount)) {
        				deleteAccount(newAccount);
        			}
        		}
				deleteAccount(account);
        	} else if (level == 5 && account.getId().length() == 12) {
        		Account newAccount = createAccount(account, 5, "");
    			changeAccount(account, newAccount);
				deleteAccount(account);
        	}

        	i++;    	
        	setProgressionCurrentValue((long) ( i * 100 / count));
        }

		if (resultList.size() == 0) {
			resultList.add("El proceso ha sido correcto.");
		}
	}

	private Account createAccount(Account sourceAccount, int level, String suffix) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

		String newId = "";
		String control1 = "0";
		String control2 = "0";
		int idLength = (level==5) ? 9 : level;
		if (sourceAccount.getLevel() < level) {
			newId = sourceAccount.getId() + StringUtils.repeat("0", idLength-sourceAccount.getId().length()-suffix.length()) + suffix;
		} else {
			String prefix1 = sourceAccount.getId().substring(0, 3);
			control1 = sourceAccount.getId().substring(3, 4);
			control2 = sourceAccount.getId().substring(4, 5);
			String prefix2 = (!control1.equals("0")) ? control1 : control2;
			newId = prefix1 + prefix2;
			if (sourceAccount.getId().length() == 12) {
				newId = newId + sourceAccount.getId().substring(7);
			}
		}

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), newId);
		Iterator<ITransferObject> iterator = accountBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			resultList.add(sourceAccount.getId() + " se ha convertido en la cuenta " + newId);
			Account newAccount = (Account)iterator.next();
			newAccount.setDescription(sourceAccount.getDescription());
			newAccount.setAlias(sourceAccount.getAlias());
			return newAccount;
		}
		Account newAccount = new Account();
		newAccount.setId(newId);
		newAccount.setDescription(sourceAccount.getDescription());
		newAccount.setAlias(sourceAccount.getAlias());
		newAccount.setEntryEnabled(level==5);
		newAccount.setLevel(level);
		return (Account)accountBean.insert(newAccount);
	}

	private Account disableEntryEnabled(Account account) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		account.setEntryEnabled(false);
		return (Account)accountBean.update(account);
	}

	private boolean changeAccount(Account sourceAccount, Account targetAccount) throws ManagerBeanException {
		boolean updated = false;
		updated = updateReferencedAccounts(AccountBudget.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AccountBudgetDetail.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AccountEntryDetail.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AccountEntryDetail.class.getName(), "balancingAccount", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AmortizationType.class.getName(), "fixedAssetAccount", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AmortizationType.class.getName(), "accumulatedAccount", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(AmortizationType.class.getName(), "allocationAccount", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(CreditorAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(CustomerAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(InvoiceDetailAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(InvoiceTaxAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(LoanAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(ProductAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(RegistryBankAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(SupplierAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAccounts(TaxAccount.class.getName(), "account", sourceAccount, targetAccount) || updated;
		updated = updateReferencedAppParams(ApplicationParameter.class.getName(), "value", "name", sourceAccount, targetAccount) || updated;
		return updated;
	}

	private boolean updateReferencedAccounts(String table, String field, Account sourceAccount, Account targetAccount) throws ManagerBeanException {
		int rowsUpdated = 0;
		try {
			String update = "UPDATE " + table + " SET " + field + " = :targetAccount WHERE " + field + " = :sourceAccount";
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();
			}
			Query query = session.createQuery(update);
			query.setString("sourceAccount", sourceAccount.getId());
			query.setString("targetAccount", targetAccount.getId());
			rowsUpdated = query.executeUpdate();
			session.flush();
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
		return (rowsUpdated > 0);
	}

	private boolean updateReferencedAppParams(String table, String field1, String field2, Account sourceAccount, Account targetAccount) 
		throws ManagerBeanException {
		int rowsUpdated = 0;
		try {
			String update = "UPDATE " + table + " SET " + field1 + " = :targetAccount " +
							"WHERE " + field2 + " LIKE 'ACC_%_ACC' AND " + field1 + " = :sourceAccount";
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();
			}
			Query query = session.createQuery(update);
			query.setString("sourceAccount", sourceAccount.getId());
			query.setString("targetAccount", targetAccount.getId());
			rowsUpdated = query.executeUpdate();
			session.flush();
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
		return (rowsUpdated > 0);
	}

	private boolean deleteAccount(Account account) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		return accountBean.remove(account);
	}

	@Override
	public Long getProgressionCurrentValue() {
		return progressionCurrentValue;
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
		progressionCurrentValue = currentValue;
	}

	@Override
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}

	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}

}
