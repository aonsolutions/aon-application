package com.code.ui.gbp.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.Requirement;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.DocumentType;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;
import com.code.ui.gbp.constants.GBPConstants;

public class ProFormaSignatureControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(ProFormaSignatureControllerListener.class.getName());
	
	private static final String PRO_FORMA_INVOICE_CONTROLLER_NAME = "proForma";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_SIGNATURE_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BasicController proFormaController = (BasicController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
		ProFormaInvoice proFormaInvoice = (ProFormaInvoice)proFormaController.getTo();
		Requirement requirement = obtainRequirement(proFormaInvoice);
		if(requirement != null){
			if(enoughSignatures(requirement, proFormaInvoice)){
				proFormaInvoice.setStatus(ProFormaInvoiceStatus.ACCEPTED);
				proFormaInvoice.setPaymentDate(obtainPaymentDate(proFormaInvoice));
				proFormaController.accept(null);
			}
		}else{
			AonUtil.addErrorMessage("Unable to obtain the requeriment to apply");
		}
	}

	private Date obtainPaymentDate(ProFormaInvoice proFormaInvoice) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, proFormaInvoice.getPaymentTerm());
		return calendar.getTime();
	}

	@SuppressWarnings("unchecked")
	private Requirement obtainRequirement(ProFormaInvoice proFormaInvoice) {
		try {
			IManagerBean requirementBean = BeanManager.getManagerBean(Requirement.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_DOCUMENT_TYPE), DocumentType.PRO_FORMA);
			criteria.addLessThanOrEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_APPLICATION_DATE), proFormaInvoice.getInvoiceDate());
			criteria.addLessThanOrEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_AMOUNT), proFormaInvoice.getAmount());
			criteria.addOrder(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_AMOUNT), false);
			criteria.addOrder(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_APPLICATION_DATE), false);
			Iterator iter = requirementBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (Requirement)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage("Unable to obtain the requeriment to apply");
			throw new AbortProcessingException(e);
		}
		return null;
	}
	
	private boolean enoughSignatures(Requirement requirement, ProFormaInvoice proFormaInvoice){
		try {
			IManagerBean proFormaSignatureBean = BeanManager.getManagerBean(ProFormaSignature.class);
			Criteria criteria = new Criteria();
			Expression proFormaExp = ExpressionUtilities.getEqualExpression(proFormaSignatureBean.getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID), proFormaInvoice.getId());
			Expression managerExp = ExpressionUtilities.getEqualExpression(proFormaSignatureBean.getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_ROLE), GBPConstants.GBP_MANAGER_ROLE_NAME); 
			Expression supervisorExp = ExpressionUtilities.getEqualExpression(proFormaSignatureBean.getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_ROLE), GBPConstants.GBP_SUPERVISOR_ROLE_NAME); 
			criteria.addExpression(ExpressionUtilities.getAndExpression(proFormaExp, managerExp));
			if(proFormaSignatureBean.getCount(criteria) >= requirement.getManagerNumber()){
				criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getAndExpression(proFormaExp, supervisorExp));
				if(proFormaSignatureBean.getCount(criteria) >= requirement.getSupervisorNumber()){
					return true;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage("Error validating signature requeriments");
			throw new AbortProcessingException(e);
		}
		return false;
	}
}