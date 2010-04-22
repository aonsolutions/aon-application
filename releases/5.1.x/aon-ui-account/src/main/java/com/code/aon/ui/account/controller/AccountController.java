package com.code.aon.ui.account.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccountController.class);	
	
	private final static String POINT = "."; 
	private final static String QUESTION = "?";
	
	private AccountUtil accountUtil;
	private String orderAlias;
	private List<SelectItem> availableOrders;

	public AccountUtil getAccountUtil() {
		if (accountUtil == null) {
			accountUtil = new AccountUtil();
		}
		return accountUtil;
	}

	public String getOrderAlias() {
		return orderAlias;
	}
	public void setOrderAlias(String orderAlias) {
		this.orderAlias = orderAlias;
	}

	public void onIdentifierChanged( ActionEvent event) {
		Account account = (Account) getTo();
		String newId = account.getId();
		if ( StringUtils.contains(newId, POINT) ) {
			int l = 9 - (newId.length() - 1);
			newId = StringUtils.replace(newId, POINT, StringUtils.repeat("0", l));
			account.setId( newId );
		} else if ( StringUtils.contains(newId, QUESTION) ) {
			String prefix = StringUtils.substringBefore(newId, QUESTION);
			try {
				account.setId( getAccountUtil().obtainNextAccountId(prefix) );
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

	public List<SelectItem> getAvailableOrders() {
		if (availableOrders == null) {
			availableOrders = new LinkedList<SelectItem>();
			try {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel( "-" );
				availableOrders.add(selectItem);
				selectItem = new SelectItem();
				selectItem.setValue(getManagerBean().getFieldName(IAccountAlias.ACCOUNT_ID) );
				selectItem.setLabel( AonUtil.getMessage("accountBundle","account_account" ));
				availableOrders.add(selectItem);
				selectItem = new SelectItem();
				selectItem.setValue(getManagerBean().getFieldName(IAccountAlias.ACCOUNT_DESCRIPTION) );
				selectItem.setLabel( AonUtil.getMessage("bundle","aon_description" ));
				availableOrders.add(selectItem);
			} catch (ManagerBeanException e) {
				String msg ="Imposible formar la lista de posibles ordenes."; 
				LOGGER.warn(msg,e);
				throw new AbortProcessingException(msg,e);
			}
		}
		return availableOrders;
	}
}
