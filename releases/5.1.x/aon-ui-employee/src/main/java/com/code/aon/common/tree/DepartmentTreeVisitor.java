package com.code.aon.common.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.tree.jsf.DefaultMutableTreeNode;
import com.code.aon.company.Company;
import com.code.aon.department.Department;
import com.code.aon.department.ast.INodeVisitor;
import com.code.aon.department.dao.IDepartmentAlias;
import com.code.aon.ql.Criteria;

public class DepartmentTreeVisitor implements INodeVisitor, IEmployeeConstants {
	
	private static final Logger LOGGER = Logger.getLogger(DepartmentTreeVisitor.class.getName());
	
	private class Counter {
        /**
         * Valor de este contador.
         */
        private int i = 0;

        /**
         * Suma uno al contador y lo devuelve.
         * 
         * @return El vaor del contador después de sumar.
         */
        public int add() {
            return i++;
        }
    }
	
	private Stack<Counter> stack = new Stack<Counter>();
	
	private ITreeNode root;
	
	private ITreeNode currentParent;
	
	private Map<String, ITreeNode> nodeMap;
	
	public Map<String, ITreeNode> getNodeMap() {
		return nodeMap;
	}

	public DepartmentTreeVisitor(ITreeNode root) {
        this.root = root;
        this.nodeMap = new HashMap<String, ITreeNode>();
    }
	
	public void visitRoot(Collection departments) {
        stack.push(new Counter());
        ( (DefaultMutableTreeNode)this.root ).setUserObject( obtainCompanyName() );
        ( (DefaultMutableTreeNode)this.root ).setDescription( obtainCompanyName().toString() );
        this.nodeMap.put( this.root.getIdentifier(), this.root );
        this.currentParent = this.root;
        Iterator iter = departments.iterator();
        while (iter.hasNext()) {
            ((Department) iter.next()).accept(this);
        }
        stack.pop();
    }

	@SuppressWarnings("unchecked")
	public void visitDepartment(Department department) {
        ITreeNode node = getBranchTreeNode( department, BRANCH_TYPE, department.getDescription().toString(), 
        		DEPARTMENT_ICON_OPEN, DEPARTMENT_ICON_CLOSED , "" );
        ITreeNode oldParent = this.currentParent;
        this.currentParent = node;
        stack.push(new Counter());
        Iterator iter = obtainChildDepartments(department).iterator();
        while (iter.hasNext()) {
            ((Department) iter.next()).accept(this);
        }
        stack.pop();
        this.currentParent = oldParent;
        this.currentParent.getChildren().add(node);   
	}
	
	private ITreeNode getBranchTreeNode(Object node, String type, String label, 
	        String iOpen, String iClose, String reference) {
			String parentId = this.currentParent.getIdentifier();
			String id = parentId + ":";
			Counter counter = stack.peek();
			int i = counter.add();
			ITreeNode treeNode = new DefaultMutableTreeNode( node, type, label, id + i, false, iOpen, iClose, reference );
			this.nodeMap.put( treeNode.getIdentifier(), treeNode );
			return treeNode;
		}
	
	private Object obtainCompanyName() {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator iter = companyBean.getList(null).iterator();
			if(iter.hasNext()){
				return ((Company)iter.next()).getName();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining company name", e);
		}
		return "";
	}
	
	private Collection obtainChildDepartments(Department department) {
		try {
			IManagerBean departmentBean = BeanManager.getManagerBean(Department.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(departmentBean.getFieldName(IDepartmentAlias.DEPARTMENT_PARENT_ID), department.getId());
			return departmentBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining child departments for department with id= " + department.getId(), e);
		}
		return Collections.emptyList();
	}
	
}
