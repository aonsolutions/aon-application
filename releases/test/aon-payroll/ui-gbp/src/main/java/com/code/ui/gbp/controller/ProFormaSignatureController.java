package com.code.ui.gbp.controller;

import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.core.Domain;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;
import com.code.ui.gbp.constants.GBPConstants;

public class ProFormaSignatureController extends LinesController {
	
	private final static String PRO_FORMA_INVOICE_CONTROLLER_NAME = "proForma";
	
	public void onSign(ActionEvent event) throws ManagerBeanException{
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal(principal.getName());
			this.onReset(event);
			ProFormaSignature to = (ProFormaSignature)this.getTo();
			to.setUser(authPrincipal.getShortName());
			to.setUserName(getUserDescription(authPrincipal));
			to.setRole(obtainUserRole());
			to.setSignatureDate(new Date());
			this.accept(event);
			this.onSearch(event);
		}  
	}
	
	public boolean isSignedByUser() throws ManagerBeanException{
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal(principal.getName());
			IController proFormaController = (IController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
			ProFormaInvoice proFormaInvoice = (ProFormaInvoice)proFormaController.getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID), proFormaInvoice.getId());
			criteria.addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_USER), authPrincipal.getShortName());
			return (getManagerBean().getCount(criteria)>0);
		}
		return false;
	}

	public boolean isPending() throws ManagerBeanException{
		IController controller = (IController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
		ProFormaInvoice invoice = (ProFormaInvoice)controller.getTo();
		return invoice.getStatus() == ProFormaInvoiceStatus.PENDING;
	}

	private String obtainUserRole() {
		if(FacesContext.getCurrentInstance().getExternalContext().isUserInRole(GBPConstants.GBP_MANAGER_ROLE_NAME)){
			return GBPConstants.GBP_MANAGER_ROLE_NAME;
		} else if(FacesContext.getCurrentInstance().getExternalContext().isUserInRole(GBPConstants.GBP_SUPERVISOR_ROLE_NAME)){
			return GBPConstants.GBP_SUPERVISOR_ROLE_NAME;
		}
		return null;
	}
	
	private String getUserDescription(AuthPrincipal principal){
        Object[] params = { principal.getContext(), principal.getDomain() };
        String[] sig = {String.class.getName(), String.class.getName()};
        try {
            IConsoleAdmin console = Utils.getSecurityConsole();
            if (console != null) {
                String oname = console.getAonSecurityName();
                Domain domain = (Domain) console.invoke( oname, IOperation.GET_DOMAIN, params, sig );
                return domain.getStandaloneUser(principal.getShortName()).getDescription();
            }
        } catch (DeploymentException e) {
            AonUtil.addErrorMessage(e.getMessage());
        }
        return null;
	}
	
	public String getSignatures() throws ManagerBeanException{
		IController proFormaInvoiceController = (IController)AonUtil.getController(PRO_FORMA_INVOICE_CONTROLLER_NAME);
		ProFormaInvoice proFormaInvoice = (ProFormaInvoice)proFormaInvoiceController.getModel().getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID), proFormaInvoice.getId());
		List signatures = getManagerBean().getList(criteria);
		Iterator iterator = signatures.iterator();
		if (iterator.hasNext()){
			String signaturesStr = "";
			while( iterator.hasNext() ){
				signaturesStr += ((ProFormaSignature) iterator.next()).getUserName();
				if (iterator.hasNext()){
					signaturesStr += ", ";
				}
			}
			return signaturesStr;
		}else{
			return "-------------";
		}
	}

}