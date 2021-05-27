package com.code.aon.ui.account.controller;

import static com.code.aon.ui.common.ICommonMessages.ACCOUNT_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.AON_DESCRIPTION;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

	public List<SelectItem> getAvailableOrders() {
		if (availableOrders == null) {
			availableOrders = new LinkedList<SelectItem>();
			try {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel( "-" );
				availableOrders.add(selectItem);
				selectItem = new SelectItem();
				selectItem.setValue(getManagerBean().getFieldName(IEntityAlias.ACCOUNT_CODE) );
				selectItem.setLabel( AonUtil.getMessage(ACCOUNT_ACCOUNT));
				availableOrders.add(selectItem);
				selectItem = new SelectItem();
				selectItem.setValue(getManagerBean().getFieldName(IEntityAlias.ACCOUNT_DESCRIPTION) );
				selectItem.setLabel( AonUtil.getMessage(AON_DESCRIPTION));
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
