package com.code.aon.ui.marketing.event;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignActionTargetSearchListener extends LinesControllerListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignActionTargetSearchListener.class.getName());
	
	private ActionTarget to;
	
	private boolean showFilterPanel;
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanAdded(event);
		reset();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);
		reset();
	}
	
	private void reset() throws ControllerListenerException {
		try {
			this.to = (ActionTarget) BeanManager.getManagerBean(ActionTarget.class).createNewTo();
			this.to.setStatus(null);
		} catch ( ManagerBeanException e ) {
			throw new ControllerListenerException(e.getMessage(), e );
		}		
	}

	private void updateCriteria() {
		BasicController controller = (BasicController) getDetailController();
		try {		
			Criteria criteria = controller.getCriteria();
			if ( to.getStatus() != null ) {
				String alias = controller.getFieldName(IEntityAlias.ACTION_TARGET_STATUS);
				criteria.addEqualExpression(alias, to.getStatus());
			}
			if (! StringUtils.isEmpty(to.getComments()) ) {
				controller.addExpression(criteria, IEntityAlias.ACTION_TARGET_COMMENTS, to.getComments());
			}
			if ( (to.getTarget() != null) && (to.getTarget().getId() != null) ) {
				String alias = controller.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
				criteria.addEqualExpression(alias, to.getTarget().getId());			
			}
			if ( (to.getUser() != null) && (to.getUser().getId() != null) ) {
				String alias = controller.getFieldName(IEntityAlias.ACTION_TARGET_USER_ID);
				criteria.addEqualExpression(alias, to.getUser().getId());			
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	public boolean isShowFilterPanel() {
		return showFilterPanel;
	}

	public void setShowFilterPanel(boolean showFilterPanel) {
		this.showFilterPanel = showFilterPanel;
	}	

	public void onFilter( ActionEvent event ) throws ControllerListenerException {
		if (getDetailController().getTo() != null) {
			getDetailController().onCancel(null);
		}
		updateDetailCriteria(getController(), false);
		updateCriteria();
		getDetailController().initializeModel();
		setShowFilterPanel(false);
	}

	public void onClearSearch( ActionEvent event ) throws ControllerListenerException {
		reset();
	}
	
	public ActionTarget getTo() {
		return to;
	}

	public void setTo(ActionTarget to) {
		this.to = to;
	}	
	
}