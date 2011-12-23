package com.code.aon.account.util;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountUtil {
	
	public String obtainNextAccountId(String prefix) throws ManagerBeanException, ExpressionException{
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), fillprefix(prefix));
		criteria.addOrder(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), false);
		Iterator<ITransferObject> iter = accountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Account account = (Account)iter.next();
			return new Long(Long.parseLong(account.getCode()) + 1).toString();
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