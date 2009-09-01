package com.code.aon.ui.ecommerce.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.EcTarget;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;



public class LoginController {
	
	private String login;
	private String password;
	
	public void onLogin(ActionEvent event){
		//System.out.println("USER LOGIN");
		try {
			Criteria criteria = new Criteria();
			String alias = IEbackofficeAlias.EC_TARGET_LOGIN;
			String identifier = AonUtil.getManagerBean(EcTarget.class).getFieldName(alias); 
			criteria.addEqualExpression(identifier, getLogin());
			alias = IEbackofficeAlias.EC_TARGET_PASSWORD;
			identifier = AonUtil.getManagerBean(EcTarget.class).getFieldName(alias);
			criteria.addEqualExpression(identifier, getPassword());
			List<ITransferObject> list = AonUtil.getManagerBean(EcTarget.class).getList(criteria);
			if(list.isEmpty()){
				System.out.println("LOGIN INCORRECTO");
			} else {
				System.out.println("LOGIADOOO");
			}
			
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e); 
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
