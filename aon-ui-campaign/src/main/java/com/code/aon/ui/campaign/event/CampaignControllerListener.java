package com.code.aon.ui.campaign.event;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.campaign.controller.CampaignController;
import com.code.aon.ui.campaign.controller.CampaignDossierController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CampaignControllerListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(CampaignControllerListener.class.getName());

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        CampaignController controller = (CampaignController)event.getController();
        try {
        	Expression empWorkGroupsExpr = obtainEmployeeWorkGroupsExpr(UserUtils.getInstance().getLoggedUser());
        	if(empWorkGroupsExpr != null){
                controller.getCriteria().addExpression(empWorkGroupsExpr);
        	}else{
        		controller.getCriteria().addExpression(ExpressionUtilities.getNullExpression(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_WORK_GROUP_ID)));
        	}
            controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_DESCRIPTION));
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Campaign Model", e);
        }
    }

    private Expression obtainEmployeeWorkGroupsExpr(User user) throws ManagerBeanException {
        IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
        return UserUtils.obtainUserWorkGroupsExpr(user, campaignBean.getFieldName(ICampaignAlias.CAMPAIGN_WORK_GROUP_ID));
    }

    @Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
        ((CampaignDossierController)FormUtil.getController("campaignDossier")).setSortColumn(null);
    }

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        ((Campaign)event.getController().getTo()).setStatus(CampaignStatus.PENDING);
        WorkGroup group = obtainLoggedUserWorkGroup();
        if(group != null){
        	((Campaign)event.getController().getTo()).setWorkGroup(group);
        }
    }

	@Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        beforeBeanSaved(event);
    }

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        beforeBeanSaved(event);
    }

    private void beforeBeanSaved(ControllerEvent event) throws ControllerListenerException {
        Date startDate = ((Campaign)event.getController().getTo()).getStartDate();
        Date endDate = ((Campaign)event.getController().getTo()).getEndDate();
        if (endDate.compareTo(startDate) < 0) {
            throw new ControllerListenerException("Fecha Inicio no puede ser posterior a Fecha Fin.");
        }
    }

    @SuppressWarnings("unchecked")
    private WorkGroup obtainLoggedUserWorkGroup() throws ControllerListenerException {
		User user = UserUtils.getInstance().getLoggedUser();
		try {
			IManagerBean empWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(empWorkGroupBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_ID), user.getId());
			Iterator iter = empWorkGroupBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((UserWorkGroup)iter.next()).getWorkGroup();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining workGroup related with user:" + user.getLogin());
		}
		return null;
	}
}