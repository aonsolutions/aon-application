package com.code.aon.ui.webmail.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;
import com.code.aon.webmail.enumeration.SignatureType;

public class SignatureControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
       	MailAccount account = webMailController.getServer().getAccount();
		IController signatureController = event.getController();
		Criteria criteria = new Criteria();
		try {
			criteria.addEqualExpression(signatureController.getFieldName(IWebMailAlias.SIGNATURE_MAIL_ACCOUNT_ID), account.getId());
			criteria.addOrder(signatureController.getFieldName(IWebMailAlias.SIGNATURE_NAME));
			signatureController.setCriteria(criteria);
		} catch (ManagerBeanException e) {
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
       	MailAccount account = webMailController.getServer().getAccount();
		IController signatureController = event.getController();
		Signature signature = (Signature) signatureController.getTo();
		signature.setMailAccount(account);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			deselectOthers(event);
		} catch (ManagerBeanException e) {
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			deselectOthers(event);
		} catch (ManagerBeanException e) {
		}
	}

	private void deselectOthers(ControllerEvent event) throws ManagerBeanException{
		IController controller = event.getController();
		Signature signature = (Signature) controller.getTo();
		if (signature.getActive().compareTo(SignatureType.ACTIVE)==0){
			IManagerBean beanSignature = BeanManager.getManagerBean(Signature.class);
    		Criteria criteriaUser = new Criteria();
           	criteriaUser.addExpression(ExpressionUtilities.getNotEqualExpression(beanSignature.getFieldName(IWebMailAlias.SIGNATURE_ID), signature.getId()));
           	criteriaUser.addExpression(ExpressionUtilities.getEqualExpression(beanSignature.getFieldName(IWebMailAlias.SIGNATURE_MAIL_ACCOUNT_ID), signature.getMailAccount().getId()));
           	criteriaUser.addEqualExpression(beanSignature.getFieldName(IWebMailAlias.SIGNATURE_ACTIVE), SignatureType.ACTIVE);
    		Iterator iter = beanSignature.getList(criteriaUser).iterator();
    		while (iter.hasNext()){
    			Signature nextSignature = (Signature)iter.next();
    			nextSignature.setActive(SignatureType.INACTIVE);
    			beanSignature.update(nextSignature);
    		}
    		controller.initializeModel();
		}
	}
}
