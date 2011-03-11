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
		if (! StringUtils.isEmpty(outcome) ) {
			process(fc, fromAction, outcome);
			outcome = strip(outcome);
			AonUtil.getConfigurationController().setCurrentAction(outcome);
		}
		if (! StringUtils.isEmpty(fromAction) ) {
			fromAction = strip(fromAction);
		}
		_base.handleNavigation(fc, fromAction, outcome);
	}

}
