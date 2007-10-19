package com.code.aon.common.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.tree.jsf.DefaultMutableTreeNode;
import com.code.aon.company.IEntityVisitor;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;

public class CalendarTreeVisitor implements IEntityVisitor, IEmployeeConstants {
	
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

	public CalendarTreeVisitor(ITreeNode root) {
        this.root = root;
        this.nodeMap = new HashMap<String, ITreeNode>();
    }
	
	public void visitRoot(Collection workPlaces) throws ManagerBeanException {
        stack.push(new Counter());
        this.nodeMap.put( this.root.getIdentifier(), this.root );
        this.currentParent = this.root;
        Iterator iter = workPlaces.iterator();
        while (iter.hasNext()) {
            ((WorkPlace) iter.next()).accept(this);
        }
        stack.pop();
    }

	@SuppressWarnings("unchecked")
	public void visitWorkPlace(WorkPlace workPlace) {
        ITreeNode node = 
        	getBranchTreeNode( workPlace, BRANCH_TYPE, workPlace.getDescription(), WORKPLACE_ICON, WORKPLACE_ICON , "" );
        ITreeNode oldParent = this.currentParent;
        this.currentParent = node;
        stack.push(new Counter());
		Iterator iter = workPlace.getActivities().iterator();
        while (iter.hasNext()) {
        	((WorkActivity) iter.next()).accept(this);
        }
		iter = workPlace.getEmployees().iterator();
        while (iter.hasNext()) {
        	((Employee) iter.next()).accept(this);
        }
        stack.pop();
        this.currentParent = oldParent;
        this.currentParent.getChildren().add(node);
	}

	@SuppressWarnings("unchecked")
	public void visitWorkActivity(WorkActivity activity) {
        ITreeNode node = 
        	getBranchTreeNode( activity, BRANCH_TYPE, activity.getDescription(), WORKACTIVITY_ICON, WORKACTIVITY_ICON , "" );
        ITreeNode oldParent = this.currentParent;
        this.currentParent = node;
        stack.push(new Counter());
		Iterator iter = activity.getEmployees().iterator();
        while (iter.hasNext()) {
        	((Employee) iter.next()).accept(this);
        }
        stack.pop();
        this.currentParent = oldParent;
        this.currentParent.getChildren().add(node);   
	}

	@SuppressWarnings("unchecked")
	public void visitEmployee(Employee employee) {
		if ( employee.isActive() ) {
	    	String name = employee.getRegistry().getName() + " " + employee.getRegistry().getSurname();
	    	ITreeNode leaf = 
	    		getBranchTreeNode( employee, BRANCH_TYPE, name, EMPLOYEE_ICON, EMPLOYEE_ICON , "" );
	    	this.currentParent.getChildren().add(leaf);
		}
	}

	private ITreeNode getBranchTreeNode(Object obj, String type, String label, 
	        String iOpen, String iClose, String reference) {
			String parentId = this.currentParent.getIdentifier();
			String id = parentId + ":";
			Counter counter = stack.peek();
			int i = counter.add();
			ITreeNode treeNode = new DefaultMutableTreeNode( obj, type, label, id + i, false, iOpen, iClose, reference );
			this.nodeMap.put( treeNode.getIdentifier(), treeNode );
			return treeNode;
	}

}
