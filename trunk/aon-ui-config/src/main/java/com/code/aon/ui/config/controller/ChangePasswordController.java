package com.code.aon.ui.config.controller;

import static com.code.aon.ldap.IAonObjectClasses.MAIL_ACCOUNT;
import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.ILdapConstants.PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;

import java.util.Arrays;
import java.util.Date;

import javax.naming.Name;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;

public class ChangePasswordController extends BasicChangePasswordController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChangePasswordController.class);
	
	private void changeDefaultMailAccountPassword( String newPassword ) {
		Name accountsDN = NameResolver.getUserDefaultAccount( getPrincipal().getDomain(), getPrincipal().getShortName() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(accountsDN, MAIL_ACCOUNT ) ) {
			try {
				ldap.getLdapSession().replaceAttribute(accountsDN, USER_PASSWORD_ATTRIBUTE, newPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error cambiando la contraseña de la cuenta de correo por defecto del usuario " + getPrincipal().getShortName() );
			} finally {
				ldap.closeSession();
			}
		} else {
			AonUtil.addErrorMessage("No se ha encontrado la cuenta de correo por defecto del usuario " + getPrincipal().getShortName() );
		}
	}

	protected void updatePassword( String newPassword ) {
		Name userDN = NameResolver.getUserDN( getPrincipal().getDomain(), getPrincipal().getShortName() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				Date newDate = DateUtils.addDays(new Date(), 180);
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(userDN, PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE, newDate);
				String encodedPassword = BasicLdap.encodeSHA(newPassword);
				session.replaceAttribute(userDN, USER_PASSWORD_ATTRIBUTE, encodedPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando la fecha de expiración de la contraseña" );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", getPrincipal().getShortName(), getPrincipal().getDomain() );
		}
		changeDefaultMailAccountPassword(newPassword);
	}

	protected boolean isCorrectPassword() {
		Name userDN = NameResolver.getUserDN( getPrincipal().getDomain(), getPrincipal().getShortName() );
		BasicLdap ldap = new BasicLdap();
		Entry entry = ldap.get(userDN, USER, USER_PASSWORD_ATTRIBUTE);
		if ( entry.containsKey(USER_PASSWORD_ATTRIBUTE) ) {
			byte[] value = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
			byte[] _password = BasicLdap.encodeSHA(getPassword()).getBytes();
			return Arrays.equals(value, _password);
		}
		return false;
	}
	
}
