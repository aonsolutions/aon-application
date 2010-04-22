package com.code.aon.ui.menu.xml;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
 * Class that walks throguth the menu structure form the chosen node to the root node.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 *  
 */
public class TreePathVisitor extends AbstractXMLReader implements IMenuVisitor,
        IMenuConstants {

    /**
     * El elemento a procesar.
     */
    private IMenuItem menuItem;

    /**
     * Construct a new {@link IMenuVisitor} that will be used to calcula the path
     * from the root node to <code>menuItem</code>.
     * 
     * @param menuItem
     *			a {@link IMenuItem} object of the menu tree model.
     */
    public TreePathVisitor(IMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public void visitRoot(IRoot root) throws MenuVisitorException {
        getTreePath(root);
    }

    public void visitMenu(IMenu menu) throws MenuVisitorException {
        getTreePath(menu);
    }

    public void visitOption(IOption option) throws MenuVisitorException {
        getTreePath(option);
    }

    public void visitActionListener(IActionListener actionListener) throws MenuVisitorException {
	}

    /**
     * Method getTreePath
     * 
     * @param item
     *			IMenuItem
     * @throws MenuVisitorException
	 *			if an unexpected error occurs.
     */
    private void getTreePath(IMenuItem item) throws MenuVisitorException {
        try {
            LinkedList<IMenuItem> list = new LinkedList<IMenuItem>();
            IMenuItem mi = item;
            list.add(mi);
            while (mi.getParent() != null) {
                mi = mi.getParent();
                list.add(mi);
            }
            Collections.reverse(list);
            IMenuItem root = list.get(0);
            Attribute attr1 = new Attribute(KEY, root.getKey());
            startElement(ROOT, attr1);

            List subList = list.subList(1, list.size());

            Iterator iter = subList.iterator();
            while (iter.hasNext()) {
                IMenuItem m = (IMenuItem) iter.next();
                if (m instanceof IMenu) {
                    menuVisited((IMenu) m);
                } else if (m instanceof IOption) {
                    optionVisited((IOption) m);
                }
            }
            endElement(ROOT);
        } catch (SAXException e) {
            throw new MenuVisitorException(e.getMessage(), e);
        }

    }

    /**
     * Redefinir con el fin de pintar un nodo de tipo menú.
     * 
     * @param menu
     *            Nodo de tipo menú.
     * @throws MenuVisitorException
	 *			if an unexpected error occurs.
     */
    private void menuVisited(IMenu menu) throws MenuVisitorException {
        try {
            Attribute attr0 = new Attribute(ID, menu.getId());
            Attribute attr1 = new Attribute(KEY, menu.getKey());
            Attribute[] attrs = { attr0, attr1 };
            element(MENU, attrs);
        } catch (SAXException e) {
            throw new MenuVisitorException(e.getMessage(), e);
        }
    }

    /**
     * Redefinir con el fin de pintar una opción del menú.
     * 
     * @param option
     *            Nodo opción del menú.
     * @throws MenuVisitorException
	 *			if an unexpected error occurs.
     */
    private void optionVisited(IOption option) throws MenuVisitorException {
        try {
            Attribute attr0 = new Attribute(ID, option.getId());
            Attribute attr1 = new Attribute(KEY, option.getKey());
            Attribute attr2 = new Attribute(REF, option.getReference());
            Attribute[] attrs = { attr0, attr1, attr2 };
            element(OPTION, attrs);
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