package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditSessionController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(AuditSessionController.class);
	
	private IControllerListener listener;
	
	private Integer moreUsedPageLimit;
	
	private DataModel moreUsedModel;
	
	public void onInit( ActionEvent event ) {
		ActionDeniedController denied = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		User user = UserUtils.getInstance().getLoggedUser();		
		denied.initEdit(user);
		denied.updateActionList();		
		this.moreUsedPageLimit = AonUtil.getConfigurationController().getPageLimit();
	}
	
	private DomainApplication getDomainApplication() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			Criteria criteria = new Criteria();
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_DOMAIN), DomainManager.getCurrentDomain());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), principal.getApplicationId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (DomainApplication) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}	
	
	public boolean isShowAudit() {
		DomainApplication da = getDomainApplication();
		if ( da != null ) {
			return (da.getAuditLevel() != AuditLevel.NONE);
		}
		return false;
	}

	public int getCount() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Session session = (Session) getModel().getRowData();
			IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_ENTRY_SESSION_ID), session.getId());
			return bean.getCount(criteria);
		}
		return 0;
	}	

	@SuppressWarnings("unchecked")
	public void onRemoveSessions( ActionEvent event ) {
    	try {
    		String idAlias = getFieldName(IEntityAlias.SESSION_ID);
    		ProjectionList pl = new ProjectionList(Projection.property(idAlias));
    		List<Integer> ids = getManagerBean().getList(pl, getCriteria());
    		if (! ids.isEmpty() ) {
    			for( Integer id : ids ) {
    				ITransferObject to = getManagerBean().get(id);
    				getManagerBean().remove(to);
    			}
    		}
    		initializeModel();
		} catch (Throwable e) {
			LOGGER.error("Error removing sessions", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public IControllerListener getListener() {
		if ( listener == null ) {
			this.listener = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						Criteria criteria = controller.getCriteria();
						DomainSwitcher dw = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
						if ( dw.isChildDomain() ) {
							criteria.setSkipDomainFilter(true);
							List<Integer> domains = new LinkedList<Integer>();
							domains.add(dw.getParentDomain());
							domains.add(dw.getDomainId());
							criteria.addInExpression("User.domain", domains);
						}
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering domain", e);
					}
				}
			};
		}
		return listener;
	}	

	@SuppressWarnings("unchecked")
	public void onInitMoreUsed( ActionEvent event ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);			
		Criteria criteria = new Criteria();
		String sessionIdAlias = getFieldName(IEntityAlias.SESSION_ID);
		ProjectionList sessionPL = new ProjectionList( Projection.property(sessionIdAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(Session.class, getCriteria(), sessionPL);
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
		this.moreUsedModel = new ListDataModel( list );
	}
	
	public DataModel getMoreUsedModel() {
		return moreUsedModel;
	}
	
	public String getCurrentDescription() throws ManagerBeanException {
		String description = null;
		if ( getMoreUsedModel().isRowAvailable() ) {
			ActionMoreUsed entry = (ActionMoreUsed) getMoreUsedModel().getRowData();
			description = ActionEntryController.getOptionDescription(entry.getAction());
		}
		return description;
	}		
	
	public Integer getMoreUsedPageLimit() {
		return moreUsedPageLimit;
	}

	public void setMoreUsedPageLimit(Integer moreUsedPageLimit) {
		this.moreUsedPageLimit = moreUsedPageLimit;
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
