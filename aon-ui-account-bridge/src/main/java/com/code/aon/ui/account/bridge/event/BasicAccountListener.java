package com.code.aon.ui.account.bridge.event;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BasicAccountListener extends ControllerAdapter {

	private String pojo;
	private String alias;
	private IAccount to;
	private Account account;
	
	private AccountBridgeUtil accountBridgeUtil;
	
	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public String getPojo() {
		return pojo;
	}
	public void setPojo(String bean) {
		this.pojo = bean;
	}

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public IAccount getTo() {
		return to;
	}
	public void setTo(IAccount to) {
		this.to = to;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isEmptyAccount() {
		return isEmptyAccount(getAccount());
	}

	private boolean isEmptyAccount(Account account) {
		return (account == null || StringUtils.isEmpty(account.getCode()));
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		setAccount(new Account());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			loadAccount(event.getController().getManagerBean().getId(event.getController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			loadAccount(event.getController().getManagerBean().getId(event.getController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(getPojo());
			Account newValue = !isEmptyAccount(getAccount()) ? getAccount() : null;
			Account oldValue = (getTo() != null && !isEmptyAccount(getTo().getAccount())) ? getTo().getAccount() : null;
			if (newValue == null) {
				if (oldValue != null) {
					bean.remove((ITransferObject)getTo());
				}
			} else {
				if (oldValue == null) {
					ITransferObject newTo = (ITransferObject) Class.forName(getPojo()).newInstance();
					IAccount toAccount = (IAccount)newTo;
					toAccount.setLinkedTo(event.getController().getTo());
					toAccount.setAccount(newValue);
					bean.insert(newTo);
				} else {
					if (!newValue.equals(oldValue)) {
						getTo().setAccount(newValue);
						bean.update((ITransferObject)getTo());
					}
				}
			}
			loadAccount(event.getController().getManagerBean().getId(event.getController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void loadAccount(Serializable id) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(getPojo());
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(getAlias()), id);
		List<ITransferObject> list = bean.getList(criteria);
		if (list.size() > 0) {
			IAccount iAccount = (IAccount)list.get(0);
			setAccount(iAccount.getAccount());
			setTo(iAccount);
		} else {
			setAccount(new Account());
			setTo(null);
		}
	}

	public boolean isAccountSynchronizable() {
		return (getTo() != null && !isEmptyAccount(getAccount()) && !getAccount().getDescription().equals(getTo().getAccountDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			getAccount().setDescription(getTo().getAccountDescription());
			if (getTo() instanceof IRegistry) {
				getAccount().setAlias(((IRegistry)getTo()).getRegistry().getAlias());
			}
			accountBean.update(getAccount());
		} catch (ManagerBeanException e) {
			String msg = "No se pudo sincronizar la cuenta contable. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onNewAccount(ActionEvent event) {
		try {
			IController controller = FormUtil.getController(getMasterController());
			IRegistry registry = (IRegistry)controller.getTo();
			IAccount iAccount = getAccountBridgeUtil().obtainIRegistryAccount(registry);
			setAccount((iAccount==null) ? new Account() : iAccount.getAccount());
			setTo(iAccount);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo crear la cuenta contable. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public String getMasterController() {
		if (CustomerAccount.class.getName().equals(getPojo())) {
			return "customer";
		} else if (SupplierAccount.class.getName().equals(getPojo())) {
			return "supplier";
		} else if (CreditorAccount.class.getName().equals(getPojo())) {
			return "creditor";
		}
		return null;
	}

}