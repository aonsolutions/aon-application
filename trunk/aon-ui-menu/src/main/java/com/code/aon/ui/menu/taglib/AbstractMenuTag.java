package com.code.aon.ui.menu.taglib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuConstants;
import com.code.aon.ui.menu.MenuParser;

/**
 * Abstract Tag that transforms an XML document (IRoot), with an XSLT page. The
 * relative path of the XML menu must be defined for this one to be loaded.
 * If the <code>IMenuConstants.MENU_ATTR</code> attribute is not found in the
 * application context, neither in the session context (in this order), the menu
 * will be loaded. Once the {@link com.code.aon.ui.menu.IMenu} is created, it will
 * be walked by an implementation of the {@link org.xml.sax.XMLReader}.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-abr-2005
 * @since 1.0
 * 
 */
public abstract class AbstractMenuTag extends TagSupport { // $codepro.audit.disable
	// abstractSpecialization

    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(AbstractMenuTag.class
			.getName());

	/**
	 * Relative path of the menu XML that it will be loaded.
	 */
	private String xml;

	/**
	 * XSL page for the document transformation.
	 */
	private String xsl;

	/**
	 * {@link ResourceBundle} needed for retreiving the menu labels
	 */
	private ResourceBundle bundle;

	/**
	 * Basename of the {@link ResourceBundle} needed for retreiving the menu labels
	 */
	private String bundleBaseName;

	/**
	 * {@link Locale} of the {@link ResourceBundle} needed for retreiving the menu labels.
	 */
	private Locale locale;

	/**
	 * Semáforo para evitar la carga por varios hilos al mismo tiempo.
	 */
	private static String FLAG = "sync"; //$NON-NLS-1$

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
	 * Sets the relative path of needed CSL for the transformation.
	 * 
	 * @param xsl
	 *			the relative path of needed CSL for the transformation.
	 */
	public void setXsl(String xsl) {
		LOGGER.finest("XSL Source " + xsl); //$NON-NLS-1$
		this.xsl = xsl;
	}

	/**
	 * Set the {@link ResourceBundle} needed for retreiving the menu labels.
	 * 
	 * @param bundle the {@link ResourceBundle} needed for retreiving the menu labels.
	 */
	public void setBundle(ResourceBundle bundle) {
		LOGGER.finest("Bundle " + bundle); //$NON-NLS-1$
		this.bundle = bundle;
	}

	/**
	 * Sets the basename of the {@link ResourceBundle} needed
	 * for retreiving the menu labels.
	 * 
	 * @param bundleBaseName the basename of the {@link ResourceBundle} needed
	 * for retreiving the menu labels.
	 */
	public void setBundleBaseName(String bundleBaseName) {
		LOGGER.finest("BaseName " + bundleBaseName); //$NON-NLS-1$
		this.bundleBaseName = bundleBaseName;
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
	 * As initial step an {@link IMenu} object must be obtained to be walked throught.
	 * The attribute {@link IMenuConstants#MENU_ATTR} is searched in the session and
	 * then in the application context. If it isn't found, the menu will be loaded
	 * using the path defined by the <code>xml</code> property. To transform the menu
	 * labels is {@link IMenuConstants#MENU_ATTR} a {@link ResourceBundle}, which is
	 * obtained directly from the <code>bundle</code> property. If thie property is
	 * undefined, it will be tried to create a new instance of {@link ResourceBundle}
	 * using the <code>basename</code> and <code>locale</code> properties.
	 * If none of these properties is defined, the tag will try to find a parent
	 * tag that will be a {javax.faces.webapp.UIComponentTag}, in which case
	 * it will use the basename returned by the method
	 * <code>FacesContext.getCurrentInstance().getApplication().getMessageBundle()</code>
	 * and the locale returned by the method
	 * <code>FacesContext.getCurrentInstance().getViewRoot().getLocale()</code>.
	 * If it isn't running in a JSF context, it will be tried to search in 
	 * <code>page</code> context, both properties stored with 
	 * {@link IMenuConstants#BASENAME_ATTR} and
	 * {@link IMenuConstants#LOCALE_ATTR} keys.
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public synchronized int doStartTag() throws JspException {
		int ret = super.doStartTag();
		try {
			IMenu menu = null;
			synchronized (FLAG) {
				menu = (IMenu) pageContext.getAttribute(
						IMenuConstants.MENU_ATTR, PageContext.SESSION_SCOPE);
				if (menu == null) {
					LOGGER.finest(IMenuConstants.MENU_ATTR
							+ " not found in session scope!"); //$NON-NLS-1$
					menu = (IMenu) pageContext.getAttribute(
							IMenuConstants.MENU_ATTR,
							PageContext.APPLICATION_SCOPE);
					if (menu == null) {
						LOGGER.finest(IMenuConstants.MENU_ATTR
								+ " not found in application scope!"); //$NON-NLS-1$
						LOGGER
								.finest(" Attemp to load menu from (" + xml + ")"); //$NON-NLS-2$ //$NON-NLS-1$
						menu = load();
					} else {
						LOGGER.finest(IMenuConstants.MENU_ATTR
								+ " found in application scope!"); //$NON-NLS-1$
					}
				} else {
					LOGGER.finest(IMenuConstants.MENU_ATTR
							+ " found in session scope!"); //$NON-NLS-1$
				}
			}

			if (bundle == null) {
				LOGGER.finest("No bundle set!"); //$NON-NLS-1$
				obtainBundle();
			}

			XMLReader reader = getXMLReader(menu);

			SAXSource source = new SAXSource(reader, new InputSource());
			ServletContext ctx = pageContext.getServletContext();
			ServletResponse hres = pageContext.getResponse();
			StreamResult result = new StreamResult(hres.getOutputStream());
			TransformerFactory f = TransformerFactory.newInstance();
			Transformer t;
			InputStream xslInputStream = null;
			if (xsl != null) {
				xslInputStream = ctx.getResourceAsStream(xsl);
			}
			if (xslInputStream != null) {
				StreamSource xslSource = new StreamSource(xslInputStream);
				t = f.newTransformer(xslSource);
			} else {
				t = f.newTransformer();
			}
			HttpServletRequest hreq = (HttpServletRequest) pageContext
					.getRequest();
			String contextPath = hreq.getContextPath();
			t.setParameter("contextPath", contextPath); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			t.transform(source, result);
			hres.flushBuffer();
		} catch (TransformerConfigurationException e) {
			throw new JspException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new JspException(e.getMessage(), e);
		} catch (TransformerException e) {
			throw new JspException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * Gets the bundle.
	 * 
	 * @return the bundle
	 */
	protected ResourceBundle getBundle() {
		return this.bundle;
	}
	
	private void obtainBundle() throws JspException {
		if (bundleBaseName == null && locale == null) {
			LOGGER.finest("No bundle nor locale set!"); //$NON-NLS-1$
			Tag tag = TagSupport.findAncestorWithClass(this,
					UIComponentTag.class);
			if (tag != null) {
				// Estamos dentro de una vista JSF, de una tag <f:view>
				// por lo que se puede acceder a los objetos JSF.
				LOGGER.finest("Attemp to obtain BaseName from FacesContext!"); //$NON-NLS-1$
				bundleBaseName = FacesContext.getCurrentInstance()
						.getApplication().getMessageBundle();
				LOGGER
						.finest("BaseName  obtained from FacesContext [" + bundleBaseName + "]"); //$NON-NLS-1$
				locale = FacesContext.getCurrentInstance().getViewRoot()
						.getLocale();
				LOGGER
						.finest("Locale obtained from FacesContext [" + locale + "]"); //$NON-NLS-1$
			} else {
				obtainBundleBasename();
				obtainLocale();
			}
		}
		bundle = ResourceBundle.getBundle(bundleBaseName, locale);
		LOGGER.finest("Bundle obtained! [" + bundle.getLocale() + "]"); //$NON-NLS-1$        

	}

	private void obtainBundleBasename() throws JspException {
		if (bundleBaseName == null) {
			Tag tag = TagSupport.findAncestorWithClass(this,
					UIComponentTag.class);
			if (tag != null) {
				LOGGER
						.finest("Attemp to obtain bundle basename from FacesContext!"); //$NON-NLS-1$
				// Estamos dentro de una vista JSF, de una tag <f:view>
				// por lo que se puede acceder a los objetos JSF.
				bundleBaseName = FacesContext.getCurrentInstance()
						.getApplication().getMessageBundle();
			}
		}
		if (bundleBaseName == null) {
			LOGGER.severe("No bundle specified!"); //$NON-NLS-1$
			throw new JspException("No bundle specified!"); //$NON-NLS-1$
		}
	}

	private void obtainLocale() {
		if (bundleBaseName == null) {
			Tag tag = TagSupport.findAncestorWithClass(this,
					UIComponentTag.class);
			if (tag != null) {
				LOGGER.finest("Attemp to obtain LOCALE from FacesContext!"); //$NON-NLS-1$
				// Estamos dentro de una vista JSF, de una tag <f:view>
				// por lo que se puede acceder a los objetos JSF.
				locale = FacesContext.getCurrentInstance().getViewRoot()
						.getLocale();
				if (locale != null) {
					LOGGER
							.finest("Locale obtained from FacesContext " + locale); //$NON-NLS-1$
				}
			} else {
				if (locale == null) {
					LOGGER
							.finest("Attemp to obtain locale from pageContext (" + IMenuConstants.LOCALE_ATTR + ")!"); //$NON-NLS-1$ //$NON-NLS-2$
					locale = (Locale) pageContext.getAttribute(
							IMenuConstants.LOCALE_ATTR, PageContext.PAGE_SCOPE);
					LOGGER
							.finest("Locale obtained from pageContext (" + IMenuConstants.LOCALE_ATTR + ") " + locale); //$NON-NLS-1$
				}
			}
		}
		if (locale == null) {
			LOGGER
					.finest("Attemp to obtain locale from HttpServletRequest.getLocale()"); //$NON-NLS-1$
			HttpServletRequest hreq = (HttpServletRequest) pageContext
					.getRequest();
			locale = hreq.getLocale();
			LOGGER
					.finest("Locale obtained from HttpServletRequest.getLocale() " + locale); //$NON-NLS-1$
		}
	}

	/**
	 * Loads the menu model.
	 * 
	 * @return the loaded menu model.
	 * @throws JspException
	 * 			if an unexpected error occurs.
	 */
	private IMenu load() throws JspException {
		ServletContext ctx = pageContext.getServletContext();
		InputStream is = ctx.getResourceAsStream(xml);
		MenuParser parser = new MenuParser();
		try {
			LOGGER.finest(" ** Begin parse"); //$NON-NLS-1$
			IMenu menu = parser.parse(is);
			LOGGER.finest(" ** End parse"); //$NON-NLS-1$
			pageContext.setAttribute(IMenuConstants.MENU_ATTR, menu,
					PageContext.SESSION_SCOPE);
			LOGGER.finest(IMenuConstants.MENU_ATTR + " set in session scope!"); //$NON-NLS-1$
			return menu;
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			throw new JspException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.severe(e.getMessage());
			throw new JspException(e.getMessage(), e);
		}
	}

	/**
	 * Method to obtain a {@link org.xml.sax.XMLReader} that will be used
	 * as source for the transformation.
	 * 
	 * @param menu
	 *			the object that will be walked.
	 * @return A {@link org.xml.sax.XMLReader} that will be used
	 * as source for the transformation.
	 */
	protected abstract XMLReader getXMLReader(IMenu menu);

}
