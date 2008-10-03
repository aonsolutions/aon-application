package com.code.aon.ui.menu.xml;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.code.aon.common.xml.AbstractXMLReader;
import com.code.aon.common.xml.Attribute;
import com.code.aon.ui.menu.IActionListener;
import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IMenuVisitor;
import com.code.aon.ui.menu.IOption;
import com.code.aon.ui.menu.IRoot;
import com.code.aon.ui.menu.MenuVisitorException;

/**
 * 
 * Class that allows to travel the menu structura like if it was going
 * to paint the tree.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 * 
 */
public class TreeMenuVisitor extends AbstractXMLReader implements IMenuVisitor,
		IMenuConstants {

	/**
	 * El elemento a procesar.
	 */
	private IMenuItem menuItem;

	/**
	 * Fichero de mensajes.
	 */
	private ResourceBundle bundle;

	/**
	 * Contruye un objeto TreeMenuVisitor que recorrerá el IMenuItem indicado
	 * por parámetro.
	 * 
	 * @param menuItem
	 *            El IMenuItem a recorrer.
	 * @param bundle
	 */
	public TreeMenuVisitor(IMenuItem menuItem, ResourceBundle bundle) {
		this.menuItem = menuItem;
		this.bundle = bundle;
	}

	public void visitRoot(IRoot menu) throws MenuVisitorException {
		try {
			Attribute attr0 = new Attribute(LABEL, bundle.getString(menu
					.getKey()));
			Attribute attr1 = new Attribute(DESCRIPTION, bundle.getString(menu
					.getDescriptionKey()));
			Attribute attr2 = new Attribute(ICON, menu.getIcon() == null ? ""
					: menu.getIcon());
			startElement(ROOT, new Attribute[] { attr0, attr1, attr2 });
			scanMenu(menu);
			endElement(ROOT);
		} catch (SAXException e) {
			throw new MenuVisitorException(e.getMessage(), e);
		}
	}

	public void visitMenu(IMenu menu) throws MenuVisitorException {
		try {
			Attribute attr0 = new Attribute(ID, menu.getId());
			Attribute attr1 = new Attribute(LABEL, bundle.getString(menu
					.getKey()));
			Attribute attr2 = new Attribute(DESCRIPTION, bundle.getString(menu
					.getDescriptionKey()));
			Attribute attr3 = new Attribute(ICON, menu.getIcon() == null ? ""
					: menu.getIcon());
			Attribute[] attrs = { attr0, attr1, attr2, attr3 };
			startElement(MENU, attrs);
			scanMenu(menu);
			endElement(MENU);
		} catch (SAXException e) {
			throw new MenuVisitorException(e.getMessage(), e);
		}
	}

	public void visitOption(IOption option) throws MenuVisitorException {
		try {
			Attribute attr0 = new Attribute(ID, option.getId());
			Attribute attr1 = new Attribute(LABEL, bundle.getString(option
					.getKey()));
			Attribute attr2 = new Attribute(REF, option.getReference());
			Attribute attr3 = new Attribute(DESCRIPTION, bundle
					.getString(option.getDescriptionKey()));
			Attribute attr4 = new Attribute(EXTERNAL, Boolean.toString(option
					.isExternal()));
			Attribute attr5 = new Attribute(TARGET,
					option.getTarget() == null ? "" : option.getTarget());
			Attribute attr6 = new Attribute(ICON, option.getIcon() == null ? ""
					: option.getIcon());
			Attribute[] attrs = { attr0, attr1, attr2, attr3, attr4, attr5, attr6 };
			element(OPTION, attrs);
		} catch (SAXException e) {
			throw new MenuVisitorException(e.getMessage(), e);
		}
	}

	public void visitActionListener(IActionListener actionListener)
			throws MenuVisitorException {
		try {
			Attribute attr0 = null;
			if (actionListener.getAction() != null) {
				attr0 = new Attribute(ACTION, actionListener.getAction());
			} else if (actionListener.getType() != null) {
				attr0 = new Attribute(ACTION, actionListener.getType());
			}
			Attribute[] attrs = { attr0 };
			element(ACTION_LISTENER, attrs);
		} catch (SAXException e) {
			throw new MenuVisitorException(e.getMessage(), e);
		}
	}

	public void parse(String systemId) throws IOException, SAXException {
		parse();
	}

	public void parse(InputSource input) throws IOException, SAXException {
		parse();
	}

	/**
	 * Visits all the children of <code>menu</code>.
	 * 
	 * @param menu
	 *			the {@link com.code.aon.ui.menu.IMenu} which children will be visited.
	 * @throws MenuVisitorException
	 *             if an unexpected error occurs.
	 */
	private void scanMenu(IMenu menu) throws MenuVisitorException {
    	for( IMenuItem menuItem : menu.getItems() ) {
            menuItem.visit(this);
        }
	}

    /**
     * Generates an XML document.
     * 
     * @throws SAXException
     *             if an unexpected SAX error occurs.
     */
	public void parse() throws SAXException {
		try {
			startDocument();
			menuItem.visit(this);
			endDocument();
		} catch (MenuVisitorException e) {
			throw new SAXException(e.getMessage(), e);
		}
	}

}