package es.code.cdr.ui.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.jackrabbit.core.security.AccessManager;

import es.code.cdr.IConstants;
import es.code.cdr.beans.Permission;
import es.code.cdr.security.PermissionsManager;
import es.code.cdr.ui.util.CDRDataModel;
import es.code.cdr.ui.util.CDRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public class Permissions implements Serializable {

	private static final long serialVersionUID = 7763398600528638509L;

	PermissionsManager manager;
	/** List of users. */
	DataModel model;
	/** Selected user. */
	Permission selected;
	/** Application message bundle. */
	transient ResourceBundle bundle;

	public Permissions() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		manager = new PermissionsManager( ctx.getExternalContext().getUserPrincipal() );
		model = new CDRDataModel( manager.getPermissions() );
	}

	public ResourceBundle getBundle() {
		if ( bundle == null ) {
    		Locale locale = CDRUtils.getCurrentLocale( FacesContext.getCurrentInstance() );
			bundle = ResourceBundle.getBundle( IConstants.CDR_BUNDLE_NAME, locale );
		}
		return bundle;
	}

	/**
	 * @return the model
	 */
	public DataModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * Gets the allowed permissions.
	 * 
	 * @return
	 */
	public SelectItem[] getValues() {
		SelectItem[] items = new SelectItem[ 3 ];
		items[0] = new SelectItem( "" + AccessManager.READ, getBundle().getString( "aon_permission_read" ) );
		items[1] = new SelectItem( "" + AccessManager.WRITE, getBundle().getString( "aon_permission_write" ) );
		items[2] = new SelectItem( "" + AccessManager.REMOVE, getBundle().getString( "aon_permission_remove" ) );
		return items;
	}

	/**
	 * @param event
	 */
	public void synchronize(ActionEvent event) {
		//TODO save permissions if needed.
	}

}
