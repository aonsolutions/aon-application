/**
 * 
 */
package es.code.cdr.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;

import org.apache.jackrabbit.value.StringValue;

import es.code.cdr.ui.util.CDRDataModel;
import es.code.ecm.ContentRepository;
import es.code.ecm.ECMQName;
import es.code.ecm.HierarchyManager;
import es.code.ecm.IConstants;
import es.code.ecm.SessionManager;
import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.Category;
import es.code.ecm.nodes.ECMNode;
import es.code.ecm.util.ECMUtil;
import es.code.ecm.util.JCRUtils;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18/09/2008
 *
 */
@SuppressWarnings("serial")
public class CategoryList implements Widget {

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;
	/** Categories parent jcr node. */
	Node categoryRootNode;
	/** Category node list. */
	DataModel model;
	/** DataModel selected index. */
	int selectedIndex;
	/** Selected Category node. */
	Category selected;
	/** */
	private boolean isNew;

	/**
	 * Constructs a <code>CategoryList</code> object.
	 * 
	 * @throws WidgetLoadingException 
	 */
	@SuppressWarnings("unchecked")
	public CategoryList() throws WidgetLoadingException {
		HttpSession session = 
			(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession( false );
		HierarchyManager hm = SessionManager.getInstance().getHierarchyManager( session.getId() );
		String categoryRelPath = hm.getWorkspace().getName() + "_" + ECMQName.AON_CATEGORY_SUFFIX;
        try {
    		categoryRootNode = hm.getRootNode().getNode( categoryRelPath );
        	List<Category> l = new ArrayList<Category>();
			NodeIterator iter = categoryRootNode.getNodes();
	        while ( iter.hasNext() ) {
	        	Node node = (Node) iter.next();
	        	Category c = new Category( node );
	        	c.getName();// Restores category name from node definition.
        		l.add( c );
	        }
	        model = new CDRDataModel( l );
        } catch (RepositoryException e) {
        	throw new WidgetLoadingException( e );
		}
    }

    /**
     * Gets the option categories.
     *
     * @return array of categories
     */
    @SuppressWarnings("unchecked")
	public SelectItem[] getCategoryItems() {
    	SelectItem[] items = new SelectItem[ getModel().getRowCount() + 1 ];
    	int i = 0;
    	items[ i ] = new SelectItem( IConstants.UNKNOWN, IConstants.EMPTY_STRING );
    	Iterator iter =( (List) getModel().getWrappedData() ).iterator();
		while (iter.hasNext()) {
			Category c = (Category) iter.next();
			try {
				i++;
				items[ i ] = new SelectItem( c.getName() );
			} catch (RepositoryException e) {
			}
		}
		return items;
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
	public Category getSelected() {
		return selected;
	}

	/**
     * Sets the selected Category.
     * 
	 * @param selected the category to set
	 */
	public void setSelected(Category selected) {
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

	/**
	 * @param event
	 * 
	 * @throws RepositoryException 
	 */
	@SuppressWarnings("unchecked")
	public void onAccept(ActionEvent event) throws RepositoryException {
		Category c = getSelected();
		try {
			String primaryNodeTypeName = ContentRepository.getNodeName( ECMQName.AON_CATEGORY );
			if (isNew) {
				c.setNode( categoryRootNode.addNode( c.getName(), primaryNodeTypeName ) );			
				c.getNode().setProperty( ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME ), 
						new StringValue( c.getName() ) );
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				String userUUID = JCRUtils.getUserUUID( ec.getUserPrincipal(), c.getNode().getSession() );
				c.setUser( userUUID );
				categoryRootNode.save();
				List l = (List) model.getWrappedData();
				int index = l.size();
				l.add( index, selected );
				model.setRowIndex( index );
			} else {
				c.getNode().setProperty( ContentRepository.getNodeName( ECMQName.AON_CATEGORYNAME ), 
						new StringValue( c.getName() ) );
				categoryRootNode.save();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			ECMUtil.addWarningMessage( e.getMessage() );
			categoryRootNode.refresh( false );
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
			categoryRootNode.save();
			List l = (List) model.getWrappedData();
			l.remove( selectedIndex );
		} catch (RepositoryException e) {
			ECMUtil.addWarningMessage( e.getMessage() );
			categoryRootNode.refresh( false );
		}
		onCancel( event );
	}

	/**
	 * @param event
	 */
	public void onReset(ActionEvent event) {
		setSelected( new Category() );
		setNew(true);
	}

	/**
	 * @param event
	 */
	public void onSelect(ActionEvent event) {
		selectedIndex = this.model.getRowIndex();
		setSelected( (Category) this.model.getRowData() ); 
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
