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
import com.code.gbp.Offer;
import com.code.gbp.OfferSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.OfferStatus;
import com.code.ui.gbp.constants.GBPConstants;

public class OfferSignatureController extends LinesController {
	
	private final static String OFFER_CONTROLLER_NAME = "offer";
	
	public void onSign(ActionEvent event) throws ManagerBeanException{
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal(principal.getName());
			this.onReset(event);
			OfferSignature to = (OfferSignature)this.getTo();
			to.setUser(authPrincipal.getShortName());
			to.setUserName(getUserDescription(authPrincipal));
			to.setRole(obtainUserRole());
			to.setSignatureDate(new Date());
			this.accept(event);
			this.onSearch(event);
		}  
	}
	
	public String getSignatures() throws ManagerBeanException{
		IController offerController = (IController)AonUtil.getController(OFFER_CONTROLLER_NAME);
		Offer offer = (Offer)offerController.getModel().getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IGBPAlias.OFFER_SIGNATURE_OFFER_ID), offer.getId());
		List signatures = getManagerBean().getList(criteria);
		Iterator iterator = signatures.iterator();
		if (iterator.hasNext()){
			String signaturesStr = "";
			while( iterator.hasNext() ){
				signaturesStr += ((OfferSignature) iterator.next()).getUserName();
				if (iterator.hasNext()){
					signaturesStr += ", ";
				}
			}
			return signaturesStr;
		}else{
			return "-------------";
		}
	}

	public boolean isSignedByUser() throws ManagerBeanException{
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal != null ) {
			AuthPrincipal authPrincipal = new AuthPrincipal(principal.getName());
			IController offerController = (IController)AonUtil.getController(OFFER_CONTROLLER_NAME);
			Offer offer = (Offer)offerController.getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IGBPAlias.OFFER_SIGNATURE_OFFER_ID), offer.getId());
			criteria.addEqualExpression(getFieldName(IGBPAlias.OFFER_SIGNATURE_USER), authPrincipal.getShortName());
			return (getManagerBean().getCount(criteria)>0);
		}
		return false;
	}

	public boolean isPending() throws ManagerBeanException{
		IController offerController = (IController)AonUtil.getController(OFFER_CONTROLLER_NAME);
		Offer offer = (Offer)offerController.getTo();
		return offer.getStatus() == OfferStatus.PENDING;
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
}