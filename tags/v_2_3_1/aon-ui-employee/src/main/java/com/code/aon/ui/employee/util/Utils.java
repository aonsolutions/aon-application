package com.code.aon.ui.employee.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;

import com.code.aon.ui.util.AonUtil;

public class Utils {

	/**
	 * Returns Controller name.
	 * 
	 * @param name
	 * @return
	 */
	public static Object getController(String name) {
	    FacesContext ctx = FacesContext.getCurrentInstance();
	    ValueBinding vb = 
	    	ctx.getApplication().createValueBinding( "#{" + name + "}" );
	    return vb.getValue(ctx);
	}

	/**
	 * @return the seniority types.
	 */
	public static final List<SelectItem> getSeniorityTypes() {
		String baseName = 
			AonUtil.getConfigurationController().getApplicationBundles().get( Constants.APPLICATION_BUNDLE_BASE_NAME );
		ResourceBundle bundle = ResourceBundle.getBundle( baseName );
		List<SelectItem> seniorityTypes = new ArrayList<SelectItem>();
		seniorityTypes.add( new SelectItem( -1, Constants.EMPTY_STRING ) );
		seniorityTypes.add( new SelectItem( 0, bundle.getString( "record_seniority_3" ) ) );
		seniorityTypes.add( new SelectItem( 1, bundle.getString( "record_seniority_5" ) ) );
		return seniorityTypes;
	}

	/**
	 * Adds a message exception to GUI.
	 *  
	 * @param message
	 * @param bundleFile
	 */
	public static final void addMessage(String message, boolean bundleFile) {
		if ( bundleFile ) {
			String baseName = 
				AonUtil.getConfigurationController().getApplicationBundles().get( Constants.APPLICATION_BUNDLE_BASE_NAME );
			ResourceBundle bundle = ResourceBundle.getBundle( baseName );
			message = bundle.getString( message );
		}
		AonUtil.addFatalMessage( message );
	}
}