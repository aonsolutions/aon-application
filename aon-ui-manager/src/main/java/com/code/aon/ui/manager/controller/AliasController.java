package com.code.aon.ui.manager.controller;

import static com.code.aon.ui.manager.controller.IManagerConstants.BUNDLE_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.WRONG_EMAIL;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.naming.Name;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;

import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Alias;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class AliasController extends LdapBasicController {

	@Override
	public boolean updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getAliasesDN(domain);
		getLdapDAO().setBaseDN(baseDN);
		return true;
	}
	
	private void resetAliases() {
		Alias alias = (Alias) getTo();
		if ( (alias.getAliases() == null) ) {
			alias.setAliases(new LinkedList<String>());
		}
		if ( alias.getAliases().isEmpty() ) {
			alias.getAliases().add(null);
		}
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		resetAliases();
	}
	
	@Override
	public void accept(ActionEvent event) {
		if ( isNew() ) {
			DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			Alias alias = (Alias) getTo();
			String name = alias.getCommonName() + "@" + dc.getDomain().getCommonName();
			alias.setCommonName(name);
		}
		super.accept(event);
	}

	private List<String> getAliases() {
		Alias alias = (Alias) getTo();
		return alias.getAliases();		
	}
	
	public int getAliasesSize() {
		return getAliases().size();
	}
	
	public void onAddAlias(ActionEvent event) {
		getAliases().add(null);
	}
	
	public void onRemoveAlias(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));
		List<String> aliases = getAliases();
		aliases.remove(index);
		if (aliases.isEmpty()) {
			aliases.add(null);
		}
	}	

	public void emailCheck(FacesContext context, UIComponent component, Object value) {
		String email = ObjectUtils.toString(value);
		if (! StringUtils.isEmpty(email) ) {
			if (! EmailValidator.getInstance().isValid(email) ) {
				String message = AonUtil.getMessage(BUNDLE_NAME, WRONG_EMAIL, email);
				throw new ValidatorException(new FacesMessage(message));
			}			
		}
	}	
	
}
