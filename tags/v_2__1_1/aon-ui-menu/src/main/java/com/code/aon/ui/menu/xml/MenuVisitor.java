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
 * Class that walks throught the menu structure, but only in its first level.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 *  
 */
public class MenuVisitor extends AbstractXMLReader implements IMenuVisitor,
        IMenuConstants {

    /**
     * Menu a visitar.
     */
    private IMenu menu;

    /**
     * Bundle properties file.
     */
    private ResourceBundle bundle;

    /**
     * Construct a new {@link IMenuVisitor} that will walk the tree defined by
     * <code>menu</code>.
     * 
     * @param menu
     *            the {@link com.code.aon.ui.menu.IMenu} to be walked.
     * @param bundle
     *            the resource bundle with the i18n messages.
     */

    public MenuVisitor(IMenu menu, ResourceBundle bundle) {
        this.menu = menu;
        this.bundle = bundle;
    }

    public void visitRoot(IRoot root) throws MenuVisitorException {
    }

    public void visitMenu(IMenu menu) throws MenuVisitorException {
        try {
            Attribute attr0 = new Attribute(ID, menu.getId());
            Attribute attr1 = new Attribute(LABEL, bundle.getString(menu
                    .getKey()));
            Attribute[] attrs = { attr0, attr1 };
            element(MENU, attrs);
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
            Attribute[] attrs = { attr0, attr1, attr2 };
            element(OPTION, attrs);
        } catch (SAXException e) {
            throw new MenuVisitorException(e.getMessage(), e);
        }
    }

    public void visitActionListener(IActionListener actionListener) throws MenuVisitorException {
        try {
        	Attribute attr0 = null;
        	if ( actionListener.getAction() != null ) {
        		attr0 = new Attribute(ACTION, actionListener.getAction() );
        	} else if ( actionListener.getType() != null ) {
        		attr0 = new Attribute(ACTION, actionListener.getType() );
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
            //startElement(ROOT);
            Attribute attr = new Attribute(LABEL, bundle.getString(menu
                    .getKey()));
            startElement(ROOT, attr);
            scanMenu(menu);
            endElement(ROOT);
            endDocument();
        } catch (MenuVisitorException e) {
            throw new SAXException(e.getMessage(), e);
        }
    }

}