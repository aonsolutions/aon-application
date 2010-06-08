package com.code.aon.ui.marketing.controller;

import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CampaignActionTargetController extends LinesController {

	@SuppressWarnings("unchecked")
	private List<Integer> getCurrentTargets() throws ManagerBeanException {
		Criteria criteria = getCriteria();
		String targetId = getFieldName(IMarketingAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList projectList = new ProjectionList(Projection.property(targetId));
		return getManagerBean().getList(projectList, criteria);
	}
	
	private MarketingAction getAction() {
		IController actionController = FormUtil.getController(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME);
		return (MarketingAction) actionController.getTo();
	}
	
	public void onAcceptTargets( ActionEvent event ) throws ManagerBeanException {
		List<Integer> currentTargets = getCurrentTargets();
		TargetController targetController = (TargetController) AonUtil.getRegisteredBean(ICommercialConstants.TARGET_CONTROLLER_NAME);
		Set<Integer> targets = targetController.getCheckedTargets();
		MarketingAction action = getAction();
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
	
}
