package com.code.aon.ui.audit.event;

import java.util.List;

import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.Action;
import com.code.aon.audit.ActionEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditSessionSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer applicationId;
	
	private User user;
	
	private String action;
	
	public AuditSessionSearchListener() {
		this.applicationId = AonUtil.getAuthPrincipal().getApplicationId();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setAction( null ); 
		setUser( (User) BeanManager.getManagerBean(User.class).createNewTo() );		
	}

	private Integer getCurrentSession() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpSession httpSession = (HttpSession) ctx.getExternalContext().getSession(false);
		if ( httpSession != null ) {
			return AuditManager.getSessionId(httpSession);
		}
		return null;
	}
	
	private Action getAction( String action ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Action.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_NAME), action);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty()) {
			return (Action) list.get(0);
		}
		return null;
	}
	
	private void addActionSubQuery(Action action, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_ENTRY_ACTION_ID), action.getId());
		String idAlias = bean.getFieldName(IEntityAlias.ACTION_ENTRY_SESSION_ID);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(ActionEntry.class, subCriteria, pl);
		criteria.addInExpression(getFieldName(IEntityAlias.SESSION_ID), exp);			
	}	
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.SESSION_APPLICATION_ID), this.applicationId);
		if ( (getUser() != null) && (getUser().getId() != null) ) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.SESSION_USER_ID), getUser().getId());			
		}		
		Integer sessionId = getCurrentSession();
		if ( sessionId != null ) {
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.SESSION_ID), sessionId);
		}
		if (! StringUtils.isEmpty(action) ) {
			Action actionTo = getAction(action);
			if ( actionTo != null ) {
				addActionSubQuery(actionTo, criteria);
			} else {
				criteria.addNullExpression(getFieldName(IEntityAlias.SESSION_ID));
			}
		}
	}

}
