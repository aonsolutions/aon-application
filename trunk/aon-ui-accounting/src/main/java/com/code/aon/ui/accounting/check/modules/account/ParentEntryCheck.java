package com.code.aon.ui.accounting.check.modules.account;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class ParentEntryCheck implements ICheckModule {

	private String label = "Chequeo de cuentas contables sin niveles inferiores.";
	private boolean enabled;
	private List<ICheckEntry> list;
	private String parentCheckEntryMsg = "Cuenta sin niveles inferiores.";
	
	Stack<String> stack; 

	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		list = new LinkedList<ICheckEntry>();
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
			String select = 
				"SELECT a.code,a.level,a.id"
				+" FROM account a "
				+" WHERE " + DomainManager.getSQLWhereClause("a.domain", Account.class)
				+" ORDER BY a.code";
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
			List<?> queryList = query
					.addScalar("code", Hibernate.STRING)
					.addScalar("level", Hibernate.INTEGER)
					.addScalar("id", Hibernate.INTEGER)
					.list();
			Iterator<?> iterator = queryList.iterator();
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			while (iterator.hasNext()) {
				Object[] obj = (Object[]) iterator.next();
				String code = (String) obj[0];
				Integer level = (Integer) obj[1];
				Integer id = (Integer) obj[2];
				if (level == 1) {
					stack = new Stack<String>();
					stack.push(code);
				} else {
					String parent = stack.peek();
					while (parent.length() >= code.length()) {
						stack.pop();
						parent = stack.peek();
					}
					if (!StringUtils.startsWith(code, parent)) {
						Account account  = (Account) bean.get(id);
						ParentCheckEntry e = new ParentCheckEntry();
						e.setMessage( parentCheckEntryMsg );
						e.setTo(account);
						list.add(e);
					} 
					if (level < 5 ) {
						stack.push(code);		
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(), e);
		}
	}

	@Override
	public List<ICheckEntry> getCheckList() {
		return list;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public CheckCategory getCategory() {
		return CheckCategory.ACCOUNT;
	}

}