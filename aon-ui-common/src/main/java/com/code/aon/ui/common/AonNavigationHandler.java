package com.code.aon.ui.common;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class AonNavigationHandler.
 */
public class AonNavigationHandler extends NavigationHandler {

	private static final String CONCURRENT_ACCESS_ERROR_ACTION = "concurrentAccessError";

	private NavigationHandler _base;
	
	public static final String ACTION_SKIP_PREFFIX = "skipME-";
	
	/**
	 * Instantiates a new aon navigation handler.
	 *
	 * @param base the base
	 */
	public AonNavigationHandler(NavigationHandler base) {
		_base = base;
	}	
	
	private String strip( String action ) {
		return StringUtils.substringBefore(action, "-");
	}
	
	/**
	 * Process.
	 *
	 * @param fc the fc
	 * @param fromAction the from action
	 * @param outcome the outcome
	 */
	protected void process( FacesContext fc, String fromAction, String outcome ) {		
	}
	
	@Override
	public void handleNavigation(FacesContext fc, String fromAction, String outcome) {
		String _fromAction = fromAction;
		String _outcome = outcome;
		if (! (StringUtils.isEmpty(_outcome) || StringUtils.startsWith(_outcome, ACTION_SKIP_PREFFIX)) ) {
			process(fc, _fromAction, _outcome);
			AonUtil.getConfigurationController().setCurrentAction(_outcome);			
			_outcome = strip(_outcome);
		}
		if (! StringUtils.isEmpty(_fromAction) ) {
			_fromAction = strip(_fromAction);
		}			
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
		if ( loggedUser.isConcurrentSession() ) {
	    	HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
	    	session.invalidate();    	
			_fromAction = CONCURRENT_ACCESS_ERROR_ACTION;
			_outcome = CONCURRENT_ACCESS_ERROR_ACTION;
		}
		_base.handleNavigation(fc, _fromAction, _outcome);
	}

}
