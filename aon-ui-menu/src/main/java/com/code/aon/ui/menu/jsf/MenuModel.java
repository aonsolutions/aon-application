package com.code.aon.ui.menu.jsf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.xml.sax.SAXException;

import com.code.aon.ui.menu.IRoot;
import com.code.aon.ui.menu.MenuBeanException;
import com.code.aon.ui.menu.MenuParser;
import com.code.aon.ui.menu.MenuVisitorException;

/**
 * The Managed Bean that holds the menu model.
 * 
 * @author Consulting & Development. Aimar Tellitu - 06-jun-2005
 * @since 1.0
 */
public class MenuModel extends AbstractMenuModel {

    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(MenuModel.class.getName());

	/**
	 * Relative path of the menu XML that it will be loaded.
	 */
	private String xml;

	/**
	 * Indicates whether security roles are checked.
	 */
	private boolean security;
	
	private IRoot menuRoot;
	
	/**
	 * Sets the relative path of the menu XML file.
	 * 
	 * @param xml
	 *            the relative path of the menu XML file.
	 */
	public void setXml(String xml) {
		LOGGER.finest("XML Source " + xml); //$NON-NLS-1$
		this.xml = xml;
	}

	/**
	 * Specify whether or not security roles are checked.
	 * 
	 * @param security
	 *            when <code>true</code>, security roles are checked. 
	 */
	public void setSecurity(boolean security) {
		this.security = security;
	}

	/**
	 * Sets the menu root.
	 * 
	 * @param menuRoot the menu root
	 */
	protected void setMenuRoot(IRoot menuRoot) {
		this.menuRoot = menuRoot;
	}

	/**
	 * Gets the menu root.
	 * 
	 * @return the menu root
	 */
	public IRoot getMenuRoot() {
		return this.menuRoot;
	}
	
	/**
	 * Gets the menu id.
	 * 
	 * @return the menu id
	 */
	public String getMenuId() {
		return this.menuRoot.getKey();
	}

	/**
	 * Loads the menu model form the menu XML file.
	 * 
	 * @return the loaded menu model.
	 * @throws MenuBeanException
	 *			if an unexpected error occurs.
	 */
	protected IRoot load() throws MenuBeanException {
		ExternalContext ctx = FacesContext.getCurrentInstance()
				.getExternalContext();
		InputStream is = ctx.getResourceAsStream(this.xml);
		MenuParser parser = new MenuParser();
		try {
			LOGGER.finest(" ** Begin parse"); //$NON-NLS-1$
			IRoot root = parser.parse(is);
			LOGGER.finest(" ** End parse"); //$NON-NLS-1$
			return root;
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			throw new MenuBeanException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.severe(e.getMessage());
			throw new MenuBeanException(e.getMessage(), e);
		}
	}

	public void init( Locale locale ) throws MenuBeanException {
		LOGGER.info( "Start loading menu model: " + this.xml );
		setMenuRoot( load() );

		ExternalContext ec = FacesContext.getCurrentInstance()
				.getExternalContext();
		Tree2Visitor visitor = new Tree2Visitor( getBundle(locale), ec, this.security);
		try {
			getMenuRoot().visit(visitor);
		} catch (MenuVisitorException e) {
			throw new MenuBeanException(e.getMessage(), e);
		}

		setRoot( visitor.getRoot() );
		setNodeMap( visitor.getNodeMap() );
		LOGGER.info( "End loading menu model: " + this.xml );
	}

	public Object getMenuItem(String identifier) {
		return this.menuRoot.find( identifier );
	}

}
