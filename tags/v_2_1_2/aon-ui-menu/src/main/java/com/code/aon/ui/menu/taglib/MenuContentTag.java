package com.code.aon.ui.menu.taglib;

import javax.servlet.ServletRequest;

import org.xml.sax.XMLReader;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.xml.MenuVisitor;

/**
 * Tag that uses {@link com.code.aon.ui.menu.xml.MenuVisitor}.  
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-abr-2005
 * @since 1.0
 *  
 */
public class MenuContentTag extends AbstractMenuTag {

    /**
     * Gets the XML reader.
     * 
     * @param menu the menu
     * 
     * @return the XML reader
     * @see com.code.aon.ui.menu.taglib.AbstractMenuTag#getXMLReader(com.code.aon.ui.menu.IMenu)
     */
    protected XMLReader getXMLReader(IMenu menu) {
        ServletRequest hreq = pageContext.getRequest();
        String id = hreq.getParameter( IMenuConstants.MENU_ID_PARAM );
        IMenu subMenu = (id != null)?(IMenu) menu.find( id ):menu;
        XMLReader reader = new MenuVisitor(subMenu, getBundle());  
        return reader;
    }

}
