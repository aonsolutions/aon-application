package com.code.aon.ui.audit.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.Session;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditSessionMoreUsedController extends DataScrollerState {

	@SuppressWarnings("unchecked")
	public void onInit( ActionEvent event ) throws ManagerBeanException {
		IController sessionController = FormUtil.getController(IAuditConstants.SESSION_CONTROLLER_NAME);
		IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);			
		Criteria criteria = new Criteria();
		String sessionIdAlias = sessionController.getFieldName(IEntityAlias.SESSION_ID);
		ProjectionList sessionPL = new ProjectionList( Projection.property(sessionIdAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(Session.class, sessionController.getCriteria(), sessionPL);
		criteria.addInExpression(bean.getFieldName(IEntityAlias.ACTION_ENTRY_SESSION_ID), exp);
		ProjectionList pl = new ProjectionList( Projection.rowCount(), Projection.group("ActionEntry.action.name") );
		List<Object[]> values = bean.getList(pl, criteria);
		List<ActionMoreUsed> list = new ArrayList<ActionMoreUsed>();
		if (! values.isEmpty() ) {
			for( Object[] value : values ) {
	        	Integer count = (Integer) value[0];
	        	String action = (String) value[1];				
	        	boolean added = false;
	        	ActionMoreUsed amu = new ActionMoreUsed(count, action); 
	        	for( int i=0; i < list.size(); i++) {
	        		ActionMoreUsed entry = (ActionMoreUsed) list.get(i);
	        		if ( entry.getCount() < count ) {
	        			list.add(i, amu);
	        			added = true;
	        			break;
	        		}
	        	}
	        	if (! added ) {
					list.add( amu );	
	        	}
			}
		}
		setModel(new ListDataModel( list ));
	}

	public String getCurrentDescription() throws ManagerBeanException {
		String description = null;
		if ( getModel().isRowAvailable() ) {
			ActionMoreUsed entry = (ActionMoreUsed) getModel().getRowData();
			description = ActionEntryController.getOptionDescription(entry.getAction());
		}
		return description;
	}			

	public class ActionMoreUsed {
		
		private Integer count;
		
		private String action;

		public ActionMoreUsed(Integer count, String action) {
			this.count = count;
			this.action = action;
		}

		public Integer getCount() {
			return count;
		}

		public String getAction() {
			return action;
		}
		
	}	
	
}
