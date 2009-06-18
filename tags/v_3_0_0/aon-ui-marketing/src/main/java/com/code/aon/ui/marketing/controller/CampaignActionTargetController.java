package com.code.aon.ui.marketing.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Action;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class CampaignActionTargetController extends BasicController {

	private List<Integer> getCurrentTargets() throws ManagerBeanException {
		Set<Integer> targets = new HashSet<Integer>();
		Criteria criteria = getCriteria();
		String targetId = getFieldName(IMarketingAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList projectList = new ProjectionList(Projection.property(targetId));
		return getManagerBean().getList(projectList, criteria);
	}
	
	private Action getAction() {
		IController actionController = AonUtil.getController(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME);
		return (Action) actionController.getTo();
	}
	
	public void onAcceptTargets( ActionEvent event ) throws ManagerBeanException {
		List<Integer> currentTargets = getCurrentTargets();
		IController targetController = AonUtil.getController(ICommercialConstants.TARGET_CONTROLLER_NAME);
		Criteria criteria = targetController.getCriteria();
		String filedId = targetController.getFieldName(ICommercialAlias.TARGET_ID);
		ProjectionList projectList = new ProjectionList(Projection.property(filedId));
		List<Integer> targets = targetController.getManagerBean().getList(projectList, criteria);
		Action action = getAction();
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
