package com.code.aon.ui.account.bridge.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IFinderBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public abstract class RegistryAccountChecker implements Serializable {

	private static final long serialVersionUID = 4264543200475703901L;

	private String companyName;	
	private boolean showErrors;
	private Criteria criteria;
	private DataModel model;
	private String beanName;
	private IManagerBean accountBean;
	private AccountBridgeUtil accountBridgeUtil;

	public void setBeanName(String beanName) {
		this.beanName = beanName;	
	}
	public String getBeanName() {
		return beanName;
	}

	protected IManagerBean getAccountBean() throws ManagerBeanException {
		if (accountBean == null) {
			accountBean = BeanManager.getManagerBean(Account.class);
		}
		return accountBean;
	}

	protected AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public Criteria getCriteria() throws ManagerBeanException {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public boolean isShowErrors() {
		return showErrors;
	}

	public void setShowErrors(boolean showErrors) {
		this.showErrors = showErrors;
	}

	public DataModel getModel() {
		try {
			if (model == null) {
				initializeModel();
			}
			return model;
		} catch (ManagerBeanException e) {
			String msg = "Error al realizar la búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public void onEditSearch(ActionEvent event) {
		setCompanyName(null);
		setCriteria(null);
		setShowErrors(true);
		setModel(null);
	}

	private void initializeModel() throws ManagerBeanException {
		List<RegistryAccountCheckerTo> list = search();
		DataModel model = new ListDataModel(list);
		setModel(model);
	}

	public void onSearch(ActionEvent event) {
		try {
			setCriteria(new Criteria());
			if (StringUtils.isBlank(getCompanyName())) {
				setCompanyName("*");
			}
			String alias = getCompanyNameAlias();
			getCriteria().addExpression(alias, getCompanyName());
		} catch (ManagerBeanException e) {
			String msg = "Error al realizar la búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Error al realizar la búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onNewAccount(ActionEvent event) {
		try {
			RegistryAccountCheckerTo to = (RegistryAccountCheckerTo) getModel().getRowData();
			IAccount ca = getAccountBridgeUtil().obtainIRegistryAccount(to.getRegistry());
			to.getAccounts().add(ca);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo crear la cuenta contable. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onAccountSynchronize(ActionEvent event) {
		try {
			RegistryAccountCheckerTo to = (RegistryAccountCheckerTo) getModel().getRowData();
			IAccount ca = to.getAccounts().get(0);
			Account account = ca.getAccount();
			account.setDescription(ca.getAccountDescription());
			account.setAlias(to.getRegistry().getRegistry().getAlias());
			account = (Account) getAccountBean().update(account);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo sincronizar la cuenta contable. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onNavigate(ActionEvent event) {
		try {
			RegistryAccountCheckerTo to = (RegistryAccountCheckerTo) getModel().getRowData();
			AccountManager c = (AccountManager) FormUtil.getController( "accountManager" );
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			Expression exp1 = null;
			for (IAccount ca : to.getAccounts()) {
				Expression exp2 = ExpressionUtilities.getEqualExpression(alias, ca.getAccount().getId());
				if (exp1 != null) {
					exp1 = ExpressionUtilities.getOrExpression(exp1, exp2);
				} else {
					exp1 = exp2;
				}
			}
			criteria.addExpression(exp1);
			c.onSearch(event);
			c.getModel().setRowIndex(0);
			c.onSelect(null);
			c.setBackAction( getBackAction() );
		} catch (ManagerBeanException e) {
			String msg = "No se pudo navegar al mantenimiento de cuentas. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}

	}

	@SuppressWarnings("unchecked")
	protected List<RegistryAccountCheckerTo> search() throws ManagerBeanException {
		String sessionFatoryName = HibernateUtil.getSessionFactoryName();
		Session session = HibernateUtil.getSession(sessionFatoryName);
		String pojo = getPojoName();
		String stmt = "SELECT new com.code.aon.ui.account.bridge.controller.RegistryAccountCheckerTo(c) "
				+ " FROM " + pojo + " as c ";
		if (getCompanyName() != null) {
			String c = StringUtils.replaceChars(getCompanyName(), "*", "%");
			stmt += " WHERE registry.name LIKE '" + c + "' ";
		}
		OrderByList l = getCriteria().getOrderByList();
		if (l != null && l.getOrders().size() > 0) {
			stmt += " ORDER BY ";
			String sep = "";
			for (Order o : l.getOrders()) {
				String field = o.getExpression().getName();
				field = StringUtils.substringAfter(field, pojo + ".");
				stmt += sep + field;
				sep = ",";
			}
		}
		List<RegistryAccountCheckerTo> errors = new LinkedList<RegistryAccountCheckerTo>();
		Query query = session.createQuery(stmt);
		List<?> list = query.list();
		for (RegistryAccountCheckerTo reg : ((List<RegistryAccountCheckerTo>) list)) {
			String a = getRegistryAccountIDAlias();
			Criteria c = new Criteria();
			c.addEqualExpression(a, reg.getRegistry().getRegistry().getId());
			List<?> cas = getIAccountBean().getList(c);
			reg.setAccounts((List<IAccount>) cas);
			if (isShowErrors() && reg.isError()) {
				errors.add(reg);
			}
		}
		return isShowErrors() ? errors : (List<RegistryAccountCheckerTo>) list;
	}

	protected abstract String getPojoName();
	protected abstract IFinderBean getIAccountBean() throws ManagerBeanException;
	protected abstract String getCompanyNameAlias() throws ManagerBeanException;
	protected abstract String getRegistryAccountIDAlias() throws ManagerBeanException;
	protected abstract String getBackAction();
}
