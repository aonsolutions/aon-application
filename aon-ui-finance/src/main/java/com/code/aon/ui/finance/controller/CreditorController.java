package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.CREDITOR_REPORT;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CreditorController extends RegistryController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditorController.class);
	
	private String smartFilter = "";

	private boolean showAuditInfoWindow;
	
	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Creditor)getTo());
	}
	

	
	public String getSmartFilter() {
		return smartFilter;
	}
	
	public void setSmartFilter(String smartFilter) {
		this.smartFilter = smartFilter;
	}
	
	public void setOnSmartFilter(boolean filter ) {
		try {
			clearCriteria();
			if ( AonStringUtils.isNotBlank(smartFilter) ) {
				addOrExpression(getCriteria(), IEntityAlias.CREDITOR_REGISTRY_NAME, smartFilter);
				addOrExpression(getCriteria(), IEntityAlias.CREDITOR_REGISTRY_ALIAS, smartFilter);
				addOrExpression(getCriteria(), IEntityAlias.CREDITOR_REGISTRY_DOCUMENT, smartFilter);
			}
			onSearch( new ActionEvent(FacesContext.getCurrentInstance().getViewRoot()) );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSmartFilter: ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onEditSearch(ValueChangeEvent event){
    	super.onEditSearch(new ActionEvent(event.getComponent()));
    }
	
	protected void addOrExpression( Criteria criteria, String id, String value ) throws ManagerBeanException {
		Expression expression = FormUtil.getExpression(criteria, getPojo(), resolveAlias(id), value);
		if ( expression != null ) {
			criteria.addOrExpression(expression);
		}
	}

	protected boolean isAccountSynchronizable(Creditor creditor) {
		Account account = creditor.getAccount();
		System.out.println(account.getDomain() +" --- "+ creditor.getDomain());
		return (account != null 
			&& account.getId() != null 
			&& account.getDomain() == creditor.getDomain()
			&& !creditor.getRegistry().getFullName().equals(account.getDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Creditor)getTo());
	}

	protected void onAccountSynchronize(Creditor creditor) {
		try {
			creditor.getAccount().setDescription(creditor.getRegistry().getFullName());
			creditor.getAccount().setAlias(creditor.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(creditor.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Creditor)getTo());
	}

	protected void onNewAccount(Creditor creditor) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			creditor.setAccount(accountBridgeUtil.obtainNewCreditorAccount(creditor));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

    public String getReportTitle(){
    	return AonUtil.getMessage(CREDITOR_REPORT);
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
    
}