/**
 * 
 */
package es.code.cdr.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.value.LongValue;

import es.code.cdr.ui.util.CDRDataModel;
import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.HierarchyManager;
import es.code.ecm.IConstants;
import es.code.ecm.SessionManager;
import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.PermissionEvent;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.ECMNode;
import es.code.ecm.nodes.User;
import es.code.ecm.util.ECMUtil;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 26/11/2008
 *
 */
public class UserList implements Widget {

	/** Application message bundle. */
	transient ResourceBundle bundle;
	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;
	/** Categories parent jcr node. */
	Node userRootNode;
	/** User node list. */
	DataModel model;
	/** DataModel selected index. */
	int selectedIndex;
	/** Selected User node. */
	User selected;
	/** New user entity. */
	private boolean isNew;

	/**
	 * Constructs a <code>UserList</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public UserList() throws WidgetLoadingException {
		HttpSession session = 
			(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
		HierarchyManager hm = SessionManager.getInstance().getHierarchyManager( session.getId() );
		String groupRelPath = hm.getWorkspace().getName() + "_" + ECMQName.AON_GROUP_SUFFIX;
        try {
    		userRootNode = hm.getRootNode().getNode( groupRelPath );
        	List<User> l = new ArrayList<User>();
			NodeIterator iter = userRootNode.getNodes();
	        while ( iter.hasNext() ) {
	        	Node node = (Node) iter.next();
        		l.add( new User( node ) );
	        }
	        model = new CDRDataModel( l );
        } catch (RepositoryException e) {
        	throw new WidgetLoadingException( e );
		}
    }

    /**
	 * @return the model
	 */
	public DataModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * @return the selected
	 */
	public User getSelected() {
		return selected;
	}

	/**
     * Sets the selected User.
     * 
	 * @param selected the user to set
	 */
	public void setSelected(User selected) {
		this.selected = selected;
    	support.fireWidgetSelected();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * 
	 * @param isNew
	 */
	public void setNew(boolean isNew) {
		if (isNew) {
			this.selectedIndex = -1;
			if (this.model != null) {
				this.model.setRowIndex(this.selectedIndex);
			}
		}
		this.isNew = isNew;
	}

	public ResourceBundle getBundle() {
		if ( bundle == null ) {
    		Locale locale = ECMUtil.getCurrentLocale( FacesContext.getCurrentInstance() );
			bundle = ResourceBundle.getBundle( IConstants.ECM_BUNDLE_NAME, locale );
		}
		return bundle;
	}

	/**
	 * Gets the allowed permissions.
	 * 
	 * @return
	 */
	public SelectItem[] getValues() {
		SelectItem[] items = new SelectItem[ 3 ];
		items[0] = new SelectItem( "" + AccessManager.READ, getBundle().getString( "aon_permission_read" ) );
		items[1] = new SelectItem( "" + AccessManager.WRITE, getBundle().getString( "aon_permission_write" ) );
		items[2] = new SelectItem( "" + AccessManager.REMOVE, getBundle().getString( "aon_permission_remove" ) );
		return items;
	}

	/**
	 * Gets the allowed access levels.
	 * 
	 * @return
	 */
	public SelectItem[] getAccessLevels() {
		SelectItem[] items = new SelectItem[ 4 ];
		items[0] = new SelectItem( new Long(0), getBundle().getString( "aon_permission_level0" ) );
		items[1] = new SelectItem( new Long(1), getBundle().getString( "aon_permission_level1" ) );
		items[2] = new SelectItem( new Long(3), getBundle().getString( "aon_permission_level3" ) );
		items[3] = new SelectItem( new Long(5), getBundle().getString( "aon_permission_level5" ) );
		return items;
	}

	/**
	 * @param event
	 * 
	 * @throws RepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public void onAccept(ActionEvent event) throws RepositoryException {
		User c = getSelected();
		try {
			PermissionEvent permissionEvent = 
				new PermissionEvent( ContentRepository.getNodeName( ECMQName.AON_USER ), c );
			if (isNew) {
				c.setNode( userRootNode.addNode( c.getName(), User.getNodeType() ) );
				c.getNode().setProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ), 
						new LongValue( c.getLevel() ) );
				userRootNode.save();
				SessionManager.getInstance().firePermissionAdded( permissionEvent );
				List l = (List) model.getWrappedData();
				int index = l.size();
				l.add( index, selected );
				model.setRowIndex( index );
			} else {
				c.getNode().setProperty( ContentRepository.getNodeName( ECMQName.AON_LEVEL ), 
						new LongValue( c.getLevel() ) );
				userRootNode.save();
				SessionManager.getInstance().firePermissionChanged( permissionEvent );
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			ECMUtil.addWarningMessage( e.getMessage() );
			userRootNode.refresh( false );
		}
		onCancel( event );
	}

	/**
	 * @param event
	 */
	public void onCancel(ActionEvent event) {
		setSelected( null );
		setNew(false);
	}
	
	/**
	 * @param event
	 * @throws RepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public void onRemove(ActionEvent event) throws RepositoryException {
		try {
			selected.getNode().remove();
			userRootNode.save();
			List l = (List) model.getWrappedData();
			l.remove( selectedIndex );
		} catch (RepositoryException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			userRootNode.refresh( false );
		}
		onCancel( event );
	}

	/**
	 * @param event
	 */
	public void onSelect(ActionEvent event) {
		selectedIndex = this.model.getRowIndex();
		setSelected( (User) this.model.getRowData() ); 
		setNew(false);
	}

//****************** Widget interface methods implementation ******************************************* 
	public void addWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support == null ) {
					support = new WidgetSupport(this);
				}
				support.addWidgetListener( l );
			}
		}
	}

	public void removeWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support != null ) {
					support.removeWidgetListener( l );
				}
			}
		}
	}

	public ECMNode getSelectedNode() {
		return selected;
	}

	public void perform(Widget dependentWidget) {
	}
//****************** End of Widget interface methods implementation ************************************ 

}
