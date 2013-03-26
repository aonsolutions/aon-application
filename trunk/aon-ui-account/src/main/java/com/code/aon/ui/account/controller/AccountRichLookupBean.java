package com.code.aon.ui.account.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.ql.util.ExpressionException;

public class AccountRichLookupBean extends RichLookupBean {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccountRichLookupBean.class);	
	
	private final static String POINT = "."; 
	private final static String QUESTION = "?";
	
	private AccountUtil accountUtil;

	public AccountUtil getAccountUtil() {
		if (accountUtil == null) {
			accountUtil = new AccountUtil();
		}
		return accountUtil;
	}

	public void onIdentifierChanged( ActionEvent event) {
		Account account = (Account) getTo();
		String newCode = account.getCode();
		if ( StringUtils.contains(newCode, POINT) ) {
			int l = 9 - (newCode.length() - 1);
			newCode = StringUtils.replace(newCode, POINT, StringUtils.repeat("0", l));
			account.setCode( newCode );
		} else if ( StringUtils.contains(newCode, QUESTION) ) {
			String prefix = StringUtils.substringBefore(newCode, QUESTION);
			try {
				account.setCode( getAccountUtil().obtainNextAccountId(prefix) );
			} catch (ManagerBeanException e) {
				String msg ="Imposible recuperar el siguiente número"; 
				LOGGER.warn(msg,e);
				throw new AbortProcessingException(msg,e);
			} catch (ExpressionException e) {
				String msg ="Imposible recuperar el siguiente número"; 
				LOGGER.warn(msg,e);
				throw new AbortProcessingException(msg,e);
			}
		}
	}

}
