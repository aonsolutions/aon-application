package com.code.aon.ui.menu.taglib;

import org.xml.sax.XMLReader;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.xml.TreeMenuVisitor;

/**
 * Tag that uses {@link com.code.aon.ui.menu.xml.TreeMenuVisitor}.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-abr-2005
 * @since 1.0
 */
public class TreeMenuTag extends AbstractMenuTag{

    /**
     * Gets the XML reader.
     * 
     * @param menu the menu
     * 
     * @return the XML reader
     * @see com.code.aon.ui.menu.taglib.AbstractMenuTag#getXMLReader(com.code.aon.ui.menu.IMenu)
     */
    protected XMLReader getXMLReader(IMenu menu) {
        XMLReader reader = new TreeMenuVisitor(menu, getBundle());  
        return reader;
    }
}
