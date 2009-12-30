package com.code.aon.ui.webmail.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonServer;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.dao.IWebMailAlias;
import com.code.aon.webmail.enumeration.MailAccountStatus;

public class WebMailController {

	private static final Logger LOGGER = Logger.getLogger(WebMailController.class.getName());
	
	private AonServer server;
	
	/**
	 * @return the server
	 */
	public AonServer getServer() {
		return server;
	}

	public void initDefault(User mailUser){
		
		try {
			MailAccount mailAccount = getAccount(mailUser);
			if (mailAccount!=null){
				this.init(mailAccount);
			}else{
	    		AonUtil.addErrorMessage("NOT VALID ACCOUNT");
			}
    	}catch (ManagerBeanException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}

	public void initDesktop(User mailUser){
		try {
			MailAccount mailAccount = getAccount(mailUser);
			if (mailAccount!=null){
				server = new AonServer(mailAccount);
				server.createBasicFolders();
			}else{
	    		AonUtil.addErrorMessage("NOT VALID ACCOUNT");
			}
    	}catch (ManagerBeanException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}

	public void init(MailAccount mailAccount){
		server = new AonServer(mailAccount);
		server.createBasicFolders();
    	TreeController treeController = (TreeController)AonUtil.getRegisteredBean(AonConstants.BEAN_TREE);
		try {
			treeController.loadTree();
	    	treeController.initTree();
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
		}
	}

    private MailAccount getAccount(User mailUser) throws ManagerBeanException {
		IManagerBean beanAccount = BeanManager.getManagerBean(MailAccount.class);
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_USER_ID), mailUser.getId());
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_STATUS), MailAccountStatus.ACTIVE);
		Iterator iterAccount = beanAccount.getList(criteriaAccount).iterator();
		if (iterAccount.hasNext()){
			MailAccount mailAccount = (MailAccount)iterAccount.next();
			return mailAccount;
		}
		return null;
    }

    @SuppressWarnings("unchecked")
    public String getCompanyName() throws ManagerBeanException {
        IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
        List companyList = companyBean.getList(null);
        if (companyList.size() > 0) {
            Company company = (Company)companyList.get(0);
            return company.getName();
        }
        return null;
    }

    public String getLoggedUserName() {
    	if (server==null)
    		return "";
        User user = server.getAccount().getUser();
        return user.getName();
    }

    
    public String getCurrentDate() {
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        return formatter.format(new Date()).toUpperCase();
    }

    public String getMillis() {
        return ""+new GregorianCalendar().getTimeInMillis();
    }

}
