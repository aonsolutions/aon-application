package com.code.aon.ui.webmail.controller;

import java.security.Principal;
import java.util.Iterator;

import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;

public class LoginController {

	private User mailUser;

	private boolean logged;

	private String page;

	public LoginController(){
		System.out.println("LoginController -> instantiate");
		page = startWebmail();
	}
	
	/**
	 * @return the mailUser
	 */
	public User getMailUser() {
		return mailUser;
	}
	
	private String startWebmail(){
		System.out.println("LoginController -> startWebmail");
		logged = false;
		mailUser = null;
		try{
			login();
			System.out.println("LoginController -> startWebmail -> logged");
		}catch (Exception e) {
			System.out.println("LoginController -> startWebmail -> " + e.getMessage());
			e.printStackTrace();
	    	return LOGIN_ERROR;
		}
    	if (mailUser != null){
			System.out.println("LoginController -> startWebmail -> initWebmail");
    		WebMailController webmail = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
    		webmail.initDefault(mailUser);
    		if (webmail.getServer()!=null){
    			logged = true;
    			return LOGIN_SUCCESS;
    		}
    	}
    	return LOGIN_ERROR;
    }
	
    private void login() {
    	try{
			System.out.println("LoginController -> login");
			AuthPrincipal user = null;
    		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
    		if ( principal instanceof AuthPrincipal ) {
    			user = (AuthPrincipal) principal;
    		} else {
    			user = new AuthPrincipal( principal.getName() );
    		}
			System.out.println("LoginController -> login -> getUserPrincipal");
    		IManagerBean beanUser = BeanManager.getManagerBean(User.class);
    		Criteria criteriaUser = new Criteria();
    		criteriaUser.addExpression(beanUser.getFieldName(IConfigAlias.USER_LOGIN), user.getShortName());
    		Iterator iterUser = beanUser.getList(criteriaUser).iterator();
			System.out.println("LoginController -> login -> getList");
    		if (iterUser.hasNext()){
    			mailUser = (User)iterUser.next();
    		}else{
    			mailUser = null;
    		}
    	}catch (ManagerBeanException e) {
			System.out.println("LoginController -> login exception -> " + e.getMessage());
			mailUser = null;
		} catch (ExpressionException e) {
			System.out.println("LoginController -> login exception -> " + e.getMessage());
			mailUser = null;
		}
    }

    public boolean isLogged(){
    	return logged;
    }
    
	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}
    
	private static String LOGIN_SUCCESS = AonConstants.NAVIGATION_FOLDER;
	private static String LOGIN_ERROR = AonConstants.NAVIGATION_LOGIN;

}
