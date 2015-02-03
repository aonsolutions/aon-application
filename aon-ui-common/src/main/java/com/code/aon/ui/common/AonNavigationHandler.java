package com.code.aon.ui.common;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;

/**
 * The Class AonNavigationHandler.
 */
public class AonNavigationHandler extends NavigationHandler {

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
		
		if (! (StringUtils.isEmpty(outcome) || StringUtils.startsWith(outcome, ACTION_SKIP_PREFFIX)) ) {
			process(fc, fromAction, outcome);
			AonUtil.getConfigurationController().setCurrentAction(outcome);			
			outcome = strip(outcome);
		}
		if (! StringUtils.isEmpty(fromAction) ) {
			fromAction = strip(fromAction);
		}
		_base.handleNavigation(fc, fromAction, outcome);
	}

}
