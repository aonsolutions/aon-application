package com.code.aon.ui.accounting.event;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountAppParamsControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account)event.getController().getTo();
		try {
			updateAppParams(account);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error updating AppParams for Account: " + account.getId());
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account)event.getController().getTo();
		try {
			updateAppParams(account);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error updating AppParams for Account: " + account.getId());
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account)event.getController().getTo();
		Iterator iter;
		try {
			IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
			iter = appParamBean.getList(null).iterator();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining AccountAppParams",e);
		}
		while(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			if(param.getValue().equals(account.getId())){
				throw new ControllerListenerException("Account used as default account in config");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateAppParams(Account account) throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Iterator iter = appParamBean.getList(null).iterator();
		while(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			if(account.getId().startsWith(param.getValue())){
				param.setValue(account.getId());
				appParamBean.update(param);
				break;
			}
		}
	}
}