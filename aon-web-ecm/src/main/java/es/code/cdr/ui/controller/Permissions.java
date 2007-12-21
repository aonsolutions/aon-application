package es.code.cdr.ui.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.jackrabbit.core.security.AccessManager;

import com.code.aon.bridge.plugin.Utils;

import es.code.cdr.beans.Permission;
import es.code.cdr.security.PermissionsManager;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public class Permissions {

	PermissionsManager manager;
	/** List of users. */
	DataModel model;
	/** Selected user. */
	Permission selected;
	/** Application message bundle. */
	ResourceBundle bundle;

	public Permissions() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getExternalContext().getRequestLocale();
		bundle = ResourceBundle.getBundle( "es.code.cdr.ui.i18n.messages", locale );
		manager = new PermissionsManager( ctx.getExternalContext().getUserPrincipal() );
		model = new ListDataModel( manager.getPermissions() );
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
		items[0] = new SelectItem( "" + AccessManager.READ, bundle.getString( "aon_permission_read" ) );
		items[1] = new SelectItem( "" + AccessManager.WRITE, bundle.getString( "aon_permission_write" ) );
		items[2] = new SelectItem( "" + AccessManager.REMOVE, bundle.getString( "aon_permission_remove" ) );
		return items;
	}

	/**
	 * @param event
	 */
	public void synchronize(ActionEvent event) {
		//TODO save permissions if needed.
	}

}
