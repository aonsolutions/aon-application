package com.code.aon.common.tree.jsf;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.tree.TreeModelException;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2005
 * @since 1.0
 *  
 */

public abstract class AbstractComponent /*implements MenuListener*/ {

	/**
	 * Obtiene un <code>Logger</code> apropiado.
	 */
	private static Logger LOGGER = Logger.getLogger(AbstractComponent.class.getName());

    private static int counter = 0;

    /**
     * Basename del <code>java.util.ResourceBundle</code> necesario para la
     * obtención de los labels.
     */
    private String bundleBaseName;

    /**
     * <code>java.util.Locale</code> del <code>java.util.ResourceBundle</code>
     * necesario para la obtención de los labels.
     */
    private Locale locale;

    /**
     * Modelo del árbol.
     */
	protected AbstractTreeModel treeModel;

    private String nextAction;

    /**
     * Devuelve el modelo del árbol.
     * @return
     */
	public AbstractTreeModel getTreeModel() {
		if ( this.treeModel == null ) {
            try {
                loadTreeModel();
            } catch (TreeModelException e) {
                LOGGER.log( Level.SEVERE, "Error loading TreeModel", e );
            }
//			this.treeModel.addMenuListener(this);
		}
		return this.treeModel;
	}

    /**
     * Asigna el basename del <code>java.util.ResourceBundle</code> necesario
     * para la transformación de los labels.
     * 
     * @param bundleBaseName
     *            Basename del <code>java.util.ResourceBundle</code> necesario
     *            para la transformación de los labels.
     */
    public void setBundleBaseName(String bundleBaseName) {
        LOGGER.finest("BaseName " + bundleBaseName); //$NON-NLS-1$
        this.bundleBaseName = bundleBaseName;
    }

    /**
     * Asigna el <code>java.util.Locale</code> del
     * <code>java.util.ResourceBundle</code> necesario para la transformación
     * de los labels.
     * 
     * @param locale
     *            <code>java.util.Locale</code> del
     *            <code>java.util.ResourceBundle</code> necesario para la
     *            transformación de los labels.
     */
    public void setLocale(Locale locale) {
        LOGGER.finest("Locale " + locale); //$NON-NLS-1$
        this.locale = locale;
    }

    /**
     * @return Returns the nextAction.
     */
    public String getNextAction() {
        return nextAction;
    }

    /**
     * @param nextAction The nextAction to set.
     */
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public String getFolderId() {
        return "folderId" + (++counter);
    }

    public String getTreeId() {
        return "treeId" + counter;
    }

    public String getNodeId() {
        return "nodeId" + (++counter);
    }

    /**
     * Ejecuta la acción asociada al nodo seleccionado.
     * 
     * @return
     */
    public String nodeAction() {
        return getTreeModel().getAction();     
    }

    /**
     * Inicializa el modelo.
     *
     */
    public void init() {
        this.treeModel = null;
    }

    /**
     * Evento lanzado al seleccionar un nodo del árbol.
     * 
     * @param e
     */
    public abstract void pressed(ActionEvent e);    

    /**
     * Método a implementar a la hora de cargar el modelo.
     * 
     * @throws TreeModelException
     */
    protected abstract void loadTreeModel() throws TreeModelException;

    /**
     * Method obtainBundle
     * 
     * @return
     * @throws TreeModelException
     */
    protected ResourceBundle obtainBundle() throws TreeModelException {
        if (bundleBaseName == null ) {
            LOGGER.finest("No bundle set!"); //$NON-NLS-1$
//  Estamos dentro de una vista JSF, de una tag <f:view> por lo que se puede acceder a los objetos JSF.
            obtainBundleBasename();
        }
        if (locale == null) {
            LOGGER.finest("No locale set!"); //$NON-NLS-1$
//  Estamos dentro de una vista JSF, de una tag <f:view> por lo que se puede acceder a los objetos JSF.
            obtainLocale();
        }
        ResourceBundle bundle = ResourceBundle.getBundle(bundleBaseName, locale);
        LOGGER.finest("Bundle obtained! [" + bundle.getLocale() + "]"); //$NON-NLS-1$
        return bundle;
    }

    /**
     * Method obtainBundleBasename
     * 
     * @throws JspException
     */
    private void obtainBundleBasename() throws TreeModelException {
        if (bundleBaseName == null) {
            LOGGER.finest("Attemp to obtain bundle basename from FacesContext!"); //$NON-NLS-1$
//  Estamos dentro de una vista JSF, de una tag <f:view> por lo que se puede acceder a los objetos JSF.
            bundleBaseName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
        }
        if (bundleBaseName == null) {
            LOGGER.severe("No bundle specified!"); //$NON-NLS-1$
            throw new TreeModelException("No bundle specified!"); //$NON-NLS-1$
        }
        LOGGER.finest("BaseName  obtained from FacesContext [" + bundleBaseName + "]"); //$NON-NLS-1$
    }

    /**
     * Method obtainLocale
     * 
     * @throws JspException
     */
    private void obtainLocale() {
        if (locale == null) {
            LOGGER.finest("Attemp to obtain LOCALE from FacesContext!"); //$NON-NLS-1$
            // Estamos dentro de una vista JSF, de una tag <f:view>
            // por lo que se puede acceder a los objetos JSF.
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            if (locale == null) {
                LOGGER.finest("Attemp to obtain locale from HttpServletRequest.getLocale()"); //$NON-NLS-1$
                locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
                LOGGER.finest("Locale obtained from HttpServletRequest.getLocale() " + locale); //$NON-NLS-1$
            } else {
                LOGGER.finest("Locale obtained from FacesContext " + locale); //$NON-NLS-1$
            }
        }
    }

//	
//	public void valueChanged(String id) {
//		LOGGER.finest("select node: " + id );
//	}
//	
//	public TreeNode clone( TreeNode node ) {
//		if (node instanceof MenuTreeNode) {
//			MenuTreeNode menuNode = (MenuTreeNode) node;
//			return new MenuTreeNode(menuNode.getMenuItem(),node.getType(), 
//					node.getDescription(),menuNode.getLargeDescription(), 
//					node.getIdentifier(), node.isLeaf() );
//		}
//		return new TreeNodeBase(node.getType(), node.getDescription(), node.getIdentifier(), node.isLeaf() );
//	}
//	
//	

}
