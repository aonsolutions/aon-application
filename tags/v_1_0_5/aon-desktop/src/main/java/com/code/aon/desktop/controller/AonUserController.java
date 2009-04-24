package com.code.aon.desktop.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.FolderController;

public class AonUserController extends UserController {

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
	/*
	public boolean isFirstTime() {
		try {
			if (getTo() == null) loadUser();
			User u = (User)getTo();
			if (u.getStatus() >= 2) return false;
			else return true;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return true;
	}*/

	/*
	@SuppressWarnings("unchecked")
	public boolean isSigned() {
		try {
			if (getTo() == null) loadUser();
			User u = (User)getTo();
			if (u.getMaster() == 1) {
				//Si el usuario es master entonces miramos su estado
				if (u.getStatus() >= 1) return true;
				else return false;
			}
			else {
				//Si no es master buscamos al master
				IManagerBean userBean = BeanManager.getManagerBean(User.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(userBean.getFieldName(IConfigAlias.USER_MASTER), 1);
				List list = userBean.getList(criteria);
				for (int i = 0; i < list.size(); i++) {
					User temp = (User)list.get(i);
					if (temp.getStatus() >= 1) {
						return true;
					}
				}
				return false;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return true;
	}*/
	
	/*
	public boolean isMaster() {
		try {
			if (getTo() == null) loadUser();
			User u = (User)getTo();
			if (u.getMaster() >= 1) return true;
			else return false;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return true;
	}*/

	@SuppressWarnings("unchecked")
	private void loadUser() throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		AuthPrincipal user = null;
		Principal principal = ec.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} 
		else {
			user = new AuthPrincipal( principal.getName() );
		}
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userBean.getFieldName(IConfigAlias.USER_LOGIN), user.getShortName());
		List list = userBean.getList(criteria);
		User u = (User)list.get(0);
		getUserManager().findUser(user.getShortName());
		setUserTO(u);
	}

	public void accept(ActionEvent event) {
		if (getUserManager().getNewPassword().length() < 4) {
			AonUtil.addErrorMessage("La contraseña debe ser al menos de 4 caracteres.");
		}
		else {
			User u = (User)getTo();
			int status = u.getStatus();
			u.setStatus(status + 2);
			try {
				super.accept(event);
				FacesContext ctx = FacesContext.getCurrentInstance();
				if ( ctx.getMaximumSeverity() == null ) {
					AonUtil.addInfoMessage("Su contraseña se ha actualizado con exito.");
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error cambiando la contraseña.", e );
				u.setStatus(status);
			}
		}
	}

	/*
	public void sign(ActionEvent event) {
		if (!accepted) {
			AonUtil.addInfoMessage("Debe aceptar el contrato.");
		}
		else {
			User u = (User)getTo();
			int status = u.getStatus();
			u.setStatus(status + 1);
			try {
				super.accept(event);
				acceptAllMasterUser();
			} catch (Exception e) {
				u.setStatus(status);
			}
		}
	}*/

	/*
	@SuppressWarnings("unchecked")
	private void acceptAllMasterUser() throws ManagerBeanException {
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userBean.getFieldName(IConfigAlias.USER_MASTER), 1);
		List list = userBean.getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			User u = (User)list.get(i);
			int status = u.getStatus();
			if (u.getStatus() < 1) {
				u.setStatus(status + 1);
				userBean.update(u);
			}
		}
	}*/

}
