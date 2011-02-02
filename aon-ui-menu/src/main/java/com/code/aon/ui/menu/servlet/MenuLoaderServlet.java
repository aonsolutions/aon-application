package com.code.aon.ui.menu.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.xml.sax.SAXException;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.MenuParser;

/**
 * Servlet that parses the content of the menu which path is defined in the
 * <code>IMenuConstants.MENU_PARAM</code> initial parameter and its content is
 * stored in the application context with the <code>IMenuConstants.MENU_ATTR</code> key.
 * The defined path in the initial paramater must be relative to the context 
 * application.
 * 
 * @see ServletContext#getResourceAsStream(String)
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 *  
 */
public class MenuLoaderServlet extends HttpServlet {

    /**
	 * Logger initialization.
     */
    private static Logger LOGGER = Logger
            .getLogger(MenuLoaderServlet.class.getName());

    /**
     * Init.
     * 
     * @param config the config
     * 
     * @throws ServletException the servlet exception
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String path = config.getInitParameter(IMenuConstants.MENU_PARAM);
        ServletContext ctx = config.getServletContext();
        InputStream is = ctx.getResourceAsStream(path);
        MenuParser parser = new MenuParser();
        try {
            IMenu menu = parser.parse(is);
            config.getServletContext().setAttribute(IMenuConstants.MENU_ATTR,
                    menu);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            throw new ServletException(e.getMessage(), e);
        } catch (SAXException e) {
            LOGGER.severe(e.getMessage());
            throw new ServletException(e.getMessage(), e);
        }
    }
}