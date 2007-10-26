package com.code.aon.ui.menu.event;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.xml.sax.SAXException;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.MenuParser;

/**
 * {@link javax.servlet.http.HttpSessionListener} that loads the menu.
 * Listener of the session, that stores in one {@link com.code.aon.ui.menu.IMenu} object the content
 * of the menu which path has been defined in the <code>IMenuConstants.MENU_PARAM</code> 
 * context parameter, and stores in the session context with 
 * the <code>IMenuConstants.MENU_ATTR</code> key. The defined path
 * in the initial parameter must be relative to the application context.
 * 
 * @see ServletContext#getResourceAsStream(String)
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 *  
 */
public class MenuLoaderListener implements HttpSessionListener {

    /**
	 * Logger initialization.
     */
    private static Logger LOGGER = Logger.getLogger(MenuLoaderListener.class
            .getName());

    /**
     * Session created.
     * 
     * @param event the event
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext ctx = session.getServletContext();
        String path = ctx.getInitParameter(IMenuConstants.MENU_PARAM);
        InputStream is = ctx.getResourceAsStream(path);
        MenuParser parser = new MenuParser();
        try {
            IMenu menu = parser.parse(is);
            session.setAttribute(IMenuConstants.MENU_ATTR, menu);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } catch (SAXException e) {
            LOGGER.severe(e.getMessage());
        }

    }

    /**
     * Session destroyed.
     * 
     * @param arg0 the arg0
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
    }
}