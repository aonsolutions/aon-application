/*
 * Created on 05-sep-2006
 *
 */
package com.code.aon.common.tree.jsf;

import java.util.Collection;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.tree.CalendarTreeVisitor;
import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.TreeModelException;
import com.code.aon.company.WorkPlace;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2006
 * @since 1.0
 *
 */

public class TreeCalendarModel extends AbstractTreeModel {

    /**
     * Obtiene un <code>Logger</code> apropiado.
     */
    private static Logger LOGGER = Logger.getLogger(TreeCalendarModel.class.getName());

    public TreeCalendarModel(ITreeNode root) {
        super(root);
    }

    protected void loadModel() throws TreeModelException {
        LOGGER.info( "Start loading tree calendar model:" );
    	HibernateUtil.setCloseSession( false );
        CalendarTreeVisitor visitor = new CalendarTreeVisitor( getRoot() );
        Collection c = null;
        try {
        	IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
        	c = bean.getList(null);
			visitor.visitRoot(c);
		} catch (ManagerBeanException e) {
			throw new TreeModelException(e);
		} finally {
	    	HibernateUtil.setCloseSession( true );
			HibernateUtil.closeSession();
		}
        setNodeMap( visitor.getNodeMap() );
        LOGGER.info( "End loading tree model:");
    }

}
