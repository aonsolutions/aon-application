package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignActionTargetController extends LinesController {

	private Criteria previousCriteria;
	
	private MarketingAction action;
	
	private boolean actionSelected;
	
	@SuppressWarnings("unchecked")
	private List<Integer> getCurrentTargets() throws ManagerBeanException {
		Criteria criteria = getCriteria();
		String targetId = getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList projectList = new ProjectionList(Projection.property(targetId));
		return getManagerBean().getList(projectList, criteria);
	}
	
	public void onAcceptTargets( ActionEvent event ) throws ManagerBeanException {
		List<Integer> currentTargets = getCurrentTargets();
		TargetController targetController = (TargetController) AonUtil.getRegisteredBean(ICommercialConstants.TARGET_CONTROLLER_NAME);
		Set<Integer> targets = targetController.getCheckedTargets();
		IController actionController = FormUtil.getController(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) actionController.getTo();
		for( Integer targetId : targets ) {
			if (! currentTargets.contains(targetId) ) {
				ActionTarget at = new ActionTarget();
				at.setAction( action );
				Target target = new Target();
				target.setId( targetId );
				at.setTarget( target );
				getManagerBean().insert( at );
			}
		}
		initializeModel();
	}
	
	public void onStartImport( ActionEvent event ) throws ManagerBeanException {
		this.previousCriteria = getCriteria();
		this.action = new MarketingAction();
		this.actionSelected = false;
	}

	public void onImport( ActionEvent event ) throws ManagerBeanException {
		IController actionController = FormUtil.getController(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) actionController.getTo();

		setCriteria(this.previousCriteria);
		Collection<Integer> currentTargets = getCurrentTargets();
		Collection<Serializable> actionTargetIds = getCheckList();
		for( Serializable id : actionTargetIds ) {
			Target target = ((ActionTarget) getManagerBean().get(id)).getTarget();
			if (! currentTargets.contains(target.getId()) ) {
				ActionTarget at = new ActionTarget();
				at.setAction( action );
				at.setTarget( target );
				getManagerBean().insert( at );
			}
		}
		initializeModel();
	}
	
	public void onCancelImport( ActionEvent event ) throws ManagerBeanException {
		setCriteria(this.previousCriteria);
		initializeModel();
	}

	public boolean isActionSelected() {
		return actionSelected;
	}

	public void setActionSelected(boolean actionSelected) {
		this.actionSelected = actionSelected;
	}

	public void setAction(MarketingAction action) {
		this.action = action;
	}
	
	public MarketingAction getAction() {
		return action;
	}

	protected Integer getId( Object o ) {
		return ((ActionTarget) o).getId();
	}

	public void onActionLookupChange(LookupChangeEvent event) throws ManagerBeanException {
		this.actionSelected = (event.getNewValue() != null);	
		if (this.actionSelected) {
			clearCriteria();
			Criteria criteria = getCriteria();
			String alias = getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID);
			MarketingAction action = (MarketingAction) event.getNewValue();
			criteria.addEqualExpression(alias, action.getId());
			initializeModel();
			checkAll(null);
		}
	}	

	public int getCount() throws ManagerBeanException {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		if ( controller.getModel().isRowAvailable() ) {
			MarketingAction action = (MarketingAction) controller.getModel().getRowData();
			Criteria criteria = new Criteria();
			String alias = getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID);
			criteria.addEqualExpression(alias, action.getId());
			return getManagerBean().getCount(criteria);
		}
		return 0;
	}	
	
}