package com.code.aon.ui.menu.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.xml.TreeMenuVisitor;

/**
 * Servlet that returns in an XML document the full menu.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 *  
 */
public class TreeMenuServlet extends HttpServlet {

    /**
     * Path del XSL que se utilizará para transformar el XML generado.
     */
    private String xslTemplatePath;

    /**
     * Fichero de mensajes.
     */
    private String baseName;

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
        xslTemplatePath = config.getInitParameter(IMenuConstants.XSL_PARAM);
        baseName = config.getInitParameter(IMenuConstants.BASENAME_PARAM);
    }

    /**
     * Do post.
     * 
     * @param hreq the hreq
     * @param hres the hres
     * 
     * @throws IOException the IO exception
     * @throws ServletException the servlet exception
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest hreq, HttpServletResponse hres)
            throws ServletException, IOException {
        process(hreq, hres);
    }

    /**
     * Do get.
     * 
     * @param hreq the hreq
     * @param hres the hres
     * 
     * @throws IOException the IO exception
     * @throws ServletException the servlet exception
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest hreq, HttpServletResponse hres)
            throws ServletException, IOException {
        process(hreq, hres);
    }

    /**
     * Se procesa el menú almacenado en el contexto de aplicación bajo la clave
     * <code>IMenuConstants.MENU_ATTR</code>. Como resultado se obtiene un
     * documento XML con el menu.
     * 
     * @param hreq
     *            Objeto que contiene la petición del cliente.
     * @param hres
     *            Objeto que contiene la respuesta del servlet.
     * @throws ServletException
     *             Si se produce algún error.
     */
    private void process(HttpServletRequest hreq, HttpServletResponse hres)
            throws ServletException {
        try {
            HttpSession session =  hreq.getSession();
            ServletContext ctx = session.getServletContext();
            IMenu menu = (IMenu) session.getAttribute(IMenuConstants.MENU_ATTR);
            if (menu == null) {
                menu = (IMenu) ctx.getAttribute(IMenuConstants.MENU_ATTR);
            }
            if (menu == null) {
                throw new ServletException(
                        "No menu structure found in application scope!"); //$NON-NLS-1$
            }
            Locale locale = getLocale(hreq);
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            TreeMenuVisitor visitor = new TreeMenuVisitor(menu, bundle);

            SAXSource source = new SAXSource(visitor, new InputSource());
            hres.setContentType(IMenuConstants.HTML_CONTENT_TYPE);
            StreamResult result = new StreamResult(hres.getOutputStream());
            TransformerFactory f = TransformerFactory.newInstance();
            Transformer t;
            InputStream xslInputStream = null;
            if (xslTemplatePath != null) {
                xslInputStream = ctx.getResourceAsStream(xslTemplatePath);
            }
            if (xslInputStream != null) {
                StreamSource xslSource = new StreamSource(xslInputStream);
                t = f.newTransformer(xslSource);
            } else {
                t = f.newTransformer();
            }
            t.transform(source, result);
            hres.flushBuffer();
        } catch (TransformerConfigurationException e) {
            throw new ServletException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ServletException(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new ServletException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Obtiene el <code>Locale</code> adecuado. Primero busca el objeto en el
     * objeto <code>HttpSession</code> bajo el nombre de atributo indicado por
     * la constante <code>org.apache.struts.Globals.LOCALE_KEY</code>. Si no
     * lo encuentra utiliza el <code>Locale</code> proporcionado por el método
     * <code>HttpServletRequest.getLocale()</code>. En caso de ser null
     * devuelve <code>Locale.getDefault()</code>.
     * 
     * @param hreq
     *            Objeto que contiene la petición del cliente.
     * @return El locale en uso.
     */
    private Locale getLocale(HttpServletRequest hreq) {
        Locale locale = null;
        locale = hreq.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

}