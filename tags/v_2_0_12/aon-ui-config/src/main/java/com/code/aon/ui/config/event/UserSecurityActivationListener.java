package com.code.aon.ui.config.event;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.session.MaximumLoginException;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class UserSecurityActivationListener extends ControllerAdapter {

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		UserController uc = (UserController) event.getController();
		User user = (User) uc.getTo();
		UserManager userManager = uc.getUserManager();
		userManager.setId( user.getLogin() );
		userManager.setName( user.getName() );
		changePassword( uc.getUserManager() );
		try {
			userManager.accept( null );
			user.setLogin( uc.getUserManager().getId() );
		} catch (MaximumLoginException e) {
			uc.onReset( null );
    		FacesContext ctx = FacesContext.getCurrentInstance();
    		ResourceBundle bundle = 
    			ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			MessageFormat mf = new MessageFormat( bundle.getString( e.getMessage() ) );
			throw new ControllerListenerException( mf.format( new Object[] {e.getArg()} ), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		UserController userController = (UserController) event.getController();
        userController.getUserManager().remove();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		UserController uc = (UserController) event.getController();
		User user = (User) uc.getTo();
		UserManager userManager = uc.getUserManager();
		String name = user.getName();
		name = ( name.equals( userManager.getUser().getName() ) )? userManager.getUser().getName(): name;
		String password = userManager.getPassword();
		if ( password == null || password.equals( "" ) || password.equals( userManager.getUser().getPasswd() ) ) {
			userManager.setChangePassword( false );
		} else {
			userManager.setChangePassword( true );
		}
		userManager.setName( name );
		try {
			userManager.accept( null );
			user.setLogin( userManager.getId() );
		} catch (MaximumLoginException e) {
    		FacesContext ctx = FacesContext.getCurrentInstance();
    		ResourceBundle bundle = 
    			ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			MessageFormat mf = new MessageFormat( bundle.getString( e.getMessage() ) );
			throw new ControllerListenerException( mf.format( new Object[] {e.getArg()} ), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		UserController userController = (UserController) event.getController();
		userController.setUserManager( new UserManager() );
    	userController.getUserManager().setRelation( new Relation( userController.getUserManager().getUser().getId() ) );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanReset(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		UserController uc = (UserController) event.getController();
		uc.setUserManager( new UserManager() );
    	uc.getUserManager().setRelation( new Relation( uc.getUserManager().getUser().getId() ) );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		UserController userController = (UserController) event.getController();
		User user = (User) userController.getTo();
		userController.getUserManager().findUser( user.getLogin() );
	}

	/**
	 * Prepares password changing proccess.
	 * 
	 * @param userManager
	 */
	private void changePassword(UserManager userManager) {
		userManager.setChangePassword( true );
		userManager.setNewPassword( userManager.getPassword() );
		userManager.setConfirmPassword( userManager.getPassword() );
		userManager.setPassword( userManager.getUser().getPasswd() );
	}
}