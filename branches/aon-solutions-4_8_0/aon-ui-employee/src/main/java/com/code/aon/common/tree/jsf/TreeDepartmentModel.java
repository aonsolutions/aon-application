/*
 * Created on 05-sep-2006
 *
 */
package com.code.aon.common.tree.jsf;

import java.util.Collection;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.tree.DepartmentTreeVisitor;
import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.TreeModelException;
import com.code.aon.department.Department;
import com.code.aon.department.dao.IDepartmentAlias;
import com.code.aon.ql.Criteria;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2006
 * @since 1.0
 *
 */

public class TreeDepartmentModel extends AbstractTreeModel {

    /**
     * Obtiene un <code>Logger</code> apropiado.
     */
    private static Logger LOGGER = Logger.getLogger(TreeDepartmentModel.class.getName());

    public TreeDepartmentModel(ITreeNode root) {
        super(root);
    }

    protected void loadModel() throws TreeModelException {
        LOGGER.info( "Start loading tree model:" );
        DepartmentTreeVisitor visitor = new DepartmentTreeVisitor( getRoot() );
        Collection c = null;
        try {
        	IManagerBean departmentBean = BeanManager.getManagerBean(Department.class);
        	Criteria criteria = new Criteria();
        	criteria.addNullExpression(departmentBean.getFieldName(IDepartmentAlias.DEPARTMENT_PARENT_ID));
        	c = departmentBean.getList(criteria);
        } catch (Exception e) {
            LOGGER.severe( e.getMessage() );
        }
        visitor.visitRoot(c);
        setNodeMap( visitor.getNodeMap() );
        LOGGER.info( "End loading tree model:");
    }

}
