package com.code.aon.account.util;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountUtil implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String NO_COST_CENTER_ACCOUNT = "Cuentas sin centro de costo";

	public String obtainNextAccountId(String prefix) throws ManagerBeanException, ExpressionException{
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), fillprefix(prefix));
		criteria.addOrder(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), false);
		Iterator<ITransferObject> iter = accountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Account account = (Account)iter.next();
			String next = new Long(Long.parseLong(account.getCode()) + 1).toString();
			if ( StringUtils.equals(prefix , "4300") && !StringUtils.startsWith(next, prefix)) {
				next = obtainNextAccountId("430");
			}
			return next;
		}
		return zerofill(prefix); 
	}
	
	private String zerofill(String prefix) {
		String string = "1";
		for(int i=0;i< 9 - (prefix.length() + 1); i++){
			string = "0" + string;
		}
		return prefix + string;
	}
	
	private String fillprefix(String prefix) {
		String string = "";
		for(int i=0; i< 9 - (prefix.length());i++){
			string = string + "?";
		}
		return prefix + string;
	}
	
}