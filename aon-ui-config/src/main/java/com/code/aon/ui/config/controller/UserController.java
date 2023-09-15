package com.code.aon.ui.config.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class UserController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
    private boolean firstSearch = true;
    
	@Override
	public void onSearch(ActionEvent event) {
		if ( firstSearch ) {
			try {
				Criteria criteria = getCriteria(); 
				criteria.addEqualExpression(getFieldName(IEntityAlias.USER_ACTIVE), Boolean.TRUE);
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e);
			}
			this.firstSearch = false;
		}
		super.onSearch(event);
	}


	public void setUserTO(User user) {
		super.setTo( user );
	}

	/**
	 * Logout user from application invalidating current session.
	 * 
	 * @param event
	 */
	public void onLogout(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ( (HttpSession) ctx.getExternalContext().getSession( false ) ).invalidate();
	}
}
