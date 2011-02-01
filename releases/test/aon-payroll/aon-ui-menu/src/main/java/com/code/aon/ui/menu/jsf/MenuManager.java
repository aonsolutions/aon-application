package com.code.aon.ui.menu.jsf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.code.aon.ui.menu.MenuBeanException;

/**
 * This class controls it's responsible of load and managing all the menu model,
 * and also controls which one it's the current one.
 * 
 * @author Consulting & Development. Aimar Tellitu - 30-jan-2007
 * @since 1.0
 *
 */
public class MenuManager {

    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(MenuManager.class.getName());
	
	/** <code>java.util.Locale</code> del <code>java.util.ResourceBundle</code> necesario para la obtención de los labels. */
	private Locale locale;
	
	private Map<String,IMenuModel> menuModelMap;
	
	private IMenuModel currentMenuModel;
	
	/** The listeners. */
	private Vector<MenuChangeListener> listeners;

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 */
	public MenuManager() {
		this.listeners = new Vector<MenuChangeListener>();
	}
	
	/**
	 * Sets the {@link java.util.Locale} of the {@link ResourceBundle}
	 * needed for retreiving the menu labels.
	 * 
	 * @param locale the {@link java.util.Locale} of the {@link ResourceBundle}
	 * needed for retreiving the menu labels.
	 */
	public void setLocale(Locale locale) {
		LOGGER.finest("Locale " + locale); //$NON-NLS-1$
		this.locale = locale;
	}
	
	/**
	 * Returns the {@link java.util.Locale} of the {@link ResourceBundle}
	 * needed for retreiving the menu labels.
	 * 
	 * @return the {@link java.util.Locale} of the {@link ResourceBundle}
	 * needed for retreiving the menu labels.
	 */
	public Locale getLocale() {
		if (this.locale == null) {
			obtainLocale();
		}
		return locale;
	}

	/**
	 * Method obtainLocale.
	 * 
	 */ 	 
	private void obtainLocale() {
		if (locale == null) {
			LOGGER.finest("Attemp to obtain LOCALE from FacesContext!"); //$NON-NLS-1$
			// Estamos dentro de una vista JSF, de una tag <f:view>
			// por lo que se puede acceder a los objetos JSF.
			locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			if (locale == null) {
				LOGGER.finest("Attemp to obtain locale from HttpServletRequest.getLocale()"); //$NON-NLS-1$
				locale = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestLocale();
				LOGGER.finest("Locale obtained from HttpServletRequest.getLocale() " + locale); //$NON-NLS-1$
			} else {
				LOGGER.finest("Locale obtained from FacesContext " + locale); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Sets the menu models.
	 * 
	 * @param menuModels the menu models
	 * @throws MenuBeanException 
	 */
	public void setMenuModels(List<IMenuModel> menuModels) throws MenuBeanException {
		this.menuModelMap = new HashMap<String, IMenuModel>();
		for( IMenuModel menuModel : menuModels ) {
			menuModel.init( getLocale() );
			this.menuModelMap.put( menuModel.getMenuId(), menuModel );
		}
		if ( menuModels.size() > 0 ) {
			setCurrentMenuModel( menuModels.get(0) );
		}
	}

	/**
	 * Sets the current menu.
	 * 
	 * @param menuRoot the menu root
	 */
	public void setCurrentMenu( String menuRoot ) {
		IMenuModel menuModel = this.menuModelMap.get( menuRoot );
		if ( menuModel != null ) {
			setCurrentMenuModel( menuModel );
		} else {
			LOGGER.severe( "Menu not found: " + menuRoot );
		}
	}
	
	/**
	 * Sets the current menu model.
	 * 
	 * @param currentMenuModel the current menu model
	 */
	public void setCurrentMenuModel(IMenuModel currentMenuModel) {
		IMenuModel oldMenu = this.currentMenuModel;
		this.currentMenuModel = currentMenuModel;
		fireMenuChanged( oldMenu, this.currentMenuModel );
	}

	/**
	 * Gets the current model.
	 * 
	 * @return the current model
	 */
	public IMenuModel getCurrentMenuModel() {
		return this.currentMenuModel;
	}
	
	/**
	 * Add the specified {@link MenuChangeListener} to the set of listeners
	 * registered to receive event notifications from this {@link MenuManager}. 
	 * 
	 * @param menuChangeListener The {@link MenuChangeListener} to be registered.
	 */
	public void addMenuChangeListener(MenuChangeListener menuChangeListener) {
		this.listeners.add(menuChangeListener);
	}

	/**
	 * Remove the specified {@link MenuChangeListener} from the set of listeners
	 * registered to receive event notifications from this {@link MenuManager}.
	 * 
	 * @param menuChangeListener The {@link MenuChangeListener} to be deregistered.
	 */
	public void removeMenuChangeListener(MenuChangeListener menuChangeListener) {
		this.listeners.remove(menuChangeListener);		
	}
	
	/**
	 * Fire value changed.
	 * 
	 * @param id the id
	 */
	private void fireMenuChanged( IMenuModel oldMenu, IMenuModel newMenu ) {
		MenuChangeEvent event = new MenuChangeEvent( oldMenu, newMenu );
		Iterator i = this.listeners.iterator();
		while (i.hasNext()) {
			MenuChangeListener listener = (MenuChangeListener) i.next();
			listener.menuChanged( event );
		}
	}
	
}
