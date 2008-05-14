package com.code.aon.desktop.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class AonUserController extends UserController implements ILdapConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(AonUserController.class.getName());
	
	private boolean accepted = false;
	
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public void onLoadCurrentUser(ActionEvent event)  {
		try {
			loadUser();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadUser() throws ManagerBeanException {
		User u = UserUtils.getInstance().getLoggedUser();
		getUserManager().findUser(u.getLogin());
		setUserTO(u);
	}
	
	private void changeDefaultMailAccountPassword( String newPassword ) {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		DistinguishedName accountsDN = AonDN.getUserDefaultAccount(user.getDomain(), user.getShortName() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(accountsDN, MAIL_ACCOUNT ) ) {
			try {
				ldap.getLdapSession().replaceAttribute(accountsDN, USER_PASSWORD_ATTRIBUTE, newPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error cambiando la contraseña de la cuenta de correo por defecto del usuario " + user.getShortName() );
			} finally {
				ldap.closeSession();
			}
		} else {
			AonUtil.addErrorMessage("No se ha encontrado la cuenta de correo por defecto del usuario " + user.getShortName() );
		}
	}

	public void accept(ActionEvent event) {
		if (getUserManager().getNewPassword().length() < 4) {
			AonUtil.addErrorMessage("La contraseña debe ser al menos de 4 caracteres.");
		}
		else {
			User user = (User)getTo();
			int status = user.getStatus();
			user.setStatus(status + 2);
			try {
				super.accept(event);
				changeDefaultMailAccountPassword( getUserManager().getPassword() );
				FacesContext ctx = FacesContext.getCurrentInstance();
				if ( ctx.getMaximumSeverity() == null ) {
					AonUtil.addInfoMessage("Su contraseña se ha actualizado con exito.");
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error cambiando la contraseña.", e );
				user.setStatus(status);
			}
		}
	}

}
