package com.code.aon.common.tree.jsf;

import java.util.ResourceBundle;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.TreeModelException;
import com.code.aon.company.Company;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2005
 * @since 1.0
 *  
 */

public class TreeCalendar extends AbstractTree2 {

	public ITreeNode getRoot() {
		return getTreeModel().getRoot();
	}

	public String getTitle() {
		AbstractTreeModel model = getTreeModel();
		return (model != null && model.getRoot() != null)? model.getRoot().getDescription(): "";
	}

    @Override
	protected void loadTreeModel() throws TreeModelException {
		if ( this.treeModel == null ) {
			ResourceBundle bundle = super.obtainBundle();
			String label = bundle.getString( "aon_menu_title" );
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(label);
			try {
				IManagerBean bean = BeanManager.getManagerBean(Company.class);
				Company company = (Company) bean.getList(null).get(0);
				root.setUserObject( company );
				root.setDescription( company.getName() );
			} catch (ManagerBeanException e) {
				throw new TreeModelException(e);
			}
			this.treeModel = new TreeCalendarModel(root);
			this.treeModel.setBundle(bundle);
			this.treeModel.init();
		}
	}

//    public String getSelectedNodeAction() {
//        return getMenuModel().getSelectedNodeAction();
//    }
//    private int getPos( IMenuItem menu ) {
//        TreeNode parent = getMenuModel().getTreeNode(menu.getParent().getId());
//        List childrens = parent.getChildren();
//        for( int i = 0; i < parent.getChildCount(); i++ ) {
//            TreeNode child = (TreeNode) childrens.get(i);
//            if ( child.getIdentifier().equals(menu.getId()) ) {
//                return i;
//            }
//        }
//        return -1;
//    }
//    
//    private String translateId( String id ) {
//        StringBuffer sb = new StringBuffer( "0" );
//        IMenuItem menu = (IMenuItem) getMenuModel().getMenuItem( id );
//        while ( menu != null ) {
//            if ( menu.getParent() != null ) {
//                int pos = getPos( menu );
//                sb.insert( 1, ":" + pos );
//            }
//            menu = menu.getParent();
//        }
//        return sb.toString();
//    }
//
//    public void valueChanged(String id) {
//        String newId = translateId(id);
//        String oldId = getHtmlTree().getNodeId();
//        if (! newId.equals(oldId) ) {
//            LOGGER.fine( "Node selected, old: " + newId + ", new: " + oldId );
//            String[] ids = getHtmlTree().getPathInformation(newId);
//            for( int i = 0; i < ids.length; i++ ) {
//                getHtmlTree().setNodeId( ids[i] );
//                if (! getHtmlTree().isNodeExpanded() ) {
//                    LOGGER.fine( "Node expanded: " + getHtmlTree().getNodeId() );
//                    getHtmlTree().toggleExpanded();
//                }
//            }
//            
//        }
//    }
}
