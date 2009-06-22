package com.code.aon.ui.account.bridge.event;

import java.io.Serializable;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BasicAccountListener extends ControllerAdapter {

	private String pojo;
	
	private String alias;
	
	private Account account;
	
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
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		setAccount( null );
		ITransferObject to = event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(getPojo());
			Criteria criteria = new Criteria();
			String fieldName = bean.getFieldName( getAlias() );
			Serializable id = event.getController().getManagerBean().getId(to);
			criteria.addEqualExpression( fieldName, id );
			List<ITransferObject> list = bean.getList(criteria);
			if ( list.size() > 0 ) {
				IAccount iaccount = (IAccount) list.get(0);
				setAccount( iaccount.getAccount() );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}