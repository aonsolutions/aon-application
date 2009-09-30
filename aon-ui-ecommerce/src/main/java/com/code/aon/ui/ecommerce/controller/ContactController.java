package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.SystemUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.util.EmailUtils;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;


public class ContactController extends EmailParentController{
	
	private static final String ECOMMERCE_BUNDLE = "ecommerceBundle";
	
	private String enterprise;
	private String name;
	private String telephone;
	private String email;
	private String issue;
	
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	public void onEmail(ActionEvent event) {
		if(!EmailUtils.validateEmailAddress(getEmail())){
			AonUtil.addInfoMessage("El email no es una cuenta de correo válida.");
			throw new AbortProcessingException();
		}
		
		String to=null;
		try {
			to = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getCompany().getEmail().getValue();
		} catch (ManagerBeanException e1) {
			String msg = "En los Datos de la Empresa no esta indicado el email";
			AonUtil.addErrorMessage(msg);
			new AbortProcessingException(msg,e1);
		}
		String from = getEmail();
		String subject = "AON-ECOMMERCE - formulario de contacto.";
		StringBuffer content = new StringBuffer();
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_form")).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_enterprise")).append(": ");
		content.append( getEnterprise() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_name")).append(": ");
		content.append( getName() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_telephone")).append(": ");
		content.append( getTelephone() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_email")).append(": ");
		content.append( getEmail() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_contact_issue")).append(": ");
		content.append( getIssue() ).append(SystemUtils.LINE_SEPARATOR);
		
		super.email(subject, from, to, content.toString());
		
		AonUtil.addInfoMessage("Mensaje enviado correctamente.");
		clearValues();
	}
	
	private void clearValues() {
		setEmail(null);
		setEnterprise(null);
		setIssue(null);
		setName(null);
		setTelephone(null);
	}
	
}
