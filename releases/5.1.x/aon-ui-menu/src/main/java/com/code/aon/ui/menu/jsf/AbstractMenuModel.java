package com.code.aon.ui.menu.jsf;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.tree2.TreeNode;

import com.code.aon.ui.menu.MenuBeanException;

/**
 * This class provides a skeletal implementation of the {@link IMenuModel} interface,
 * to minimize the effort required to implement this interface.
 * 
 * @author Consulting & Development. Aimar Tellitu - 12-dic-2005
 * @since 1.0
 */
public abstract class AbstractMenuModel implements IMenuModel {

    /**
	 * Logger initialization.
     */
	private static Logger LOGGER = Logger.getLogger(AbstractMenuModel.class.getName());

	/** <code>java.util.ResourceBundle</code> necesario para la obtención de los labels. */
	private ResourceBundle bundle;

	/** Basename del <code>java.util.ResourceBundle</code> necesario para la obtención de los labels. */
	private String bundleBaseName;

	/** The root. */
	private TreeNode root;

	/** The selected node id. */
	private String selectedNodeId;

	/** The node map. */
	private Map<String,TreeNode> nodeMap;

	/** The listeners. */
	private Vector<MenuListener> listeners;

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 */
	public AbstractMenuModel() {
		this.listeners = new Vector<MenuListener>();
	}

	public void addMenuListener(MenuListener menuListener) {
		this.listeners.add(menuListener);
	}

	public void removeMenuListener(MenuListener menuListener) {
		this.listeners.remove(menuListener);
	}

	/**
	 * Fire value changed.
	 * 
	 * @param id the id
	 */
	private void fireValueChanged(String oldId, String newId) {
		Iterator i = this.listeners.iterator();
		while (i.hasNext()) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuItemSelected(oldId, newId);
		}
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
	 * Method obtainBundle.
	 * 
	 * @param locale the locale
	 * 
	 * @throws MenuBeanException if an unexpected error occurs.
	 */
	private void obtainBundle( Locale locale ) throws MenuBeanException {
		if (bundleBaseName == null ) {
			LOGGER.finest("No bundle set!"); //$NON-NLS-1$
			// Estamos dentro de una vista JSF, de una tag <f:view>
			// por lo que se puede acceder a los objetos JSF.
			obtainBundleBasename();
		}
		bundle = ResourceBundle.getBundle(bundleBaseName, locale);
		LOGGER.finest("Bundle obtained! [" + bundle.getLocale() + "]"); //$NON-NLS-1$        
	}

	/**
	 * Method obtainBundleBasename.
	 * 
	 * @throws MenuBeanException
	 * 			if an unexpected error occurs. 
	 */
	private void obtainBundleBasename() throws MenuBeanException {
		if (bundleBaseName == null) {
			LOGGER.finest("Attemp to obtain bundle basename from FacesContext!"); //$NON-NLS-1$
			// Estamos dentro de una vista JSF, de una tag <f:view>
			// por lo que se puede acceder a los objetos JSF.
			bundleBaseName = FacesContext.getCurrentInstance().getApplication()
					.getMessageBundle();
		}
		if (bundleBaseName == null) {
			LOGGER.severe("No bundle specified!"); //$NON-NLS-1$
			throw new MenuBeanException("No bundle specified!"); //$NON-NLS-1$
		}
		LOGGER.finest("BaseName  obtained from FacesContext [" + bundleBaseName + "]"); //$NON-NLS-1$
	}

	/**
	 * Gets the bundle specified by bundle base name. If the bundle base name is null,
	 * it will return the bundle from the {@link javax.faces.application.Application}.
	 * @param locale the locale 
	 * @return the bundle
	 * 			The {@link ResourceBundle} used for retreiving the menu labels.
	 * @throws MenuBeanException
	 * 			if an unexpected error occurs. 
	 */
	public ResourceBundle getBundle( Locale locale ) throws MenuBeanException {
		if (this.bundle == null) {
			obtainBundle( locale );
		}
		return this.bundle;
	}

	public String getSelectedNodeId() {
		return (this.selectedNodeId != null) ? this.selectedNodeId : getRoot()
				.getIdentifier();
	}

	public TreeNode getSelectedNode() {
		return this.nodeMap.get(getSelectedNodeId());
	}

	public void setSelectedNode(String id) {
		String oldSelectedNodeId = this.selectedNodeId;
		this.selectedNodeId = id;
		LOGGER.info("selected node: " + this.selectedNodeId);
		fireValueChanged(oldSelectedNodeId, this.selectedNodeId);			
	}

	public synchronized TreeNode getRoot() {
		return this.root;
	}
	
	/**
	 * Sets the root node of the menu.
	 * 
	 * @param root the root node of the menu.
	 */
	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getTreeNode(String identifier) {
		return this.nodeMap.get(identifier);
	}

	/**
	 * Sets the node map.
	 * 
	 * @param nodeMap the node map
	 */
	protected void setNodeMap(Map<String,TreeNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	/**
	 * Gets the node map.
	 * 
	 * @return the node map
	 */
	protected Map<String,TreeNode> getNodeMap() {
		return this.nodeMap;
	}

	/**
	 * Gets the action of the menu item specified by <code>identifier</code>.
	 * 
	 * @param identifier the identifier of the menu item.
	 * 
	 * @return the action
	 */
	public String getAction(String identifier) {
		return null;
	}
	
}
