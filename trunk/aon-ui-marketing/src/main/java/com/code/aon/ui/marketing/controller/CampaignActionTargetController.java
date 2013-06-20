package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;

import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignActionTargetController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignActionTargetController.class.getName());
	
	private IControllerListener actionFilter;
	
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

	public int getCount() throws ManagerBeanException {
		if ( getMasterController().getModel().isRowAvailable() ) {
			MarketingAction action = (MarketingAction) getMasterController().getModel().getRowData();
			Criteria criteria = new Criteria();
			String alias = getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID);
			criteria.addEqualExpression(alias, action.getId());
			return getManagerBean().getCount(criteria);
		}
		return 0;
	}	
	
	private int getCount( ActionTargetStatus status ) {
		Criteria criteria = new Criteria();
		try {
			MarketingAction action = (MarketingAction) getMasterController().getTo();
			criteria.addEqualExpression(getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.ACTION_TARGET_STATUS), status);
			return getManagerBean().getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return 0;
	}	

	public int getPendingCount() {
		return getCount(ActionTargetStatus.PENDING);
	}	

	public int getAbsentCount() {
		return getCount(ActionTargetStatus.ABSENT);
	}	

	public int getIncorrectCount() {
		return getCount(ActionTargetStatus.INCORRECT);
	}	

	public int getTryAgainCount() {
		return getCount(ActionTargetStatus.TRY_AGAIN);
	}	

	public int getCancelCount() {
		return getCount(ActionTargetStatus.CANCEL);
	}	

	public int getFinishedCount() {
		return getCount(ActionTargetStatus.FINISHED);
	}	

	public int getSentCount() {
		return getCount(ActionTargetStatus.SENT);
	}	
	
	public IControllerListener getActionFilter() {
		if ( this.actionFilter == null ) {
			this.actionFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						IController actionController = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
						MarketingAction action = (MarketingAction) actionController.getTo();
						String alias = controller.getFieldName(IEntityAlias.MARKETING_ACTION_ID);
						controller.getCriteria().addNotEqualExpression(alias, action.getId());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering action", e);
					}
				}
			};
		}
		return this.actionFilter;
	}
	
	public void onClearStatus( ActionEvent event ) throws ManagerBeanException {
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			ActionTarget at = (ActionTarget) to;
			at.setStatus(ActionTargetStatus.PENDING);
			at.setUser(null);
			getManagerBean().update(at);
		}
	}
	
}