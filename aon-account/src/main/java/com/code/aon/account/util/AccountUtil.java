package com.code.aon.account.util;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

public class AccountUtil {
	
	@SuppressWarnings("unchecked")
	public String obtainNextAccountId(String prefix) throws ManagerBeanException, ExpressionException{
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), fillprefix(prefix));
		criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), false);
		Iterator iter = accountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Account account = (Account)iter.next();
			return new Long(Long.parseLong(account.getId()) + 1).toString();
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