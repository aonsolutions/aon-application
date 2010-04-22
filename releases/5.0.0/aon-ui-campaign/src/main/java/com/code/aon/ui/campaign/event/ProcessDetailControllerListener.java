package com.code.aon.ui.campaign.event;

import java.util.List;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.ProcessDetailTransition;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.campaign.enumeration.ProcessDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProcessDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		ProcessDetail processDetail = (ProcessDetail) controller.getTo();
		try {
			int index = controller.getModel().getRowCount();
			processDetail.setPosition(index);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		try {
			Criteria criteria = controller.getCriteria();
			String field = controller.getFieldName(ICampaignAlias.PROCESS_DETAIL_POSITION);
			criteria.addOrder(field);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		ProcessDetail processDetail = (ProcessDetail) controller.getTo();
		try {
			List<ProcessDetail> list = (List<ProcessDetail>) controller.getModel().getWrappedData();
			for (int i = processDetail.getPosition() + 1; i < list.size(); i++) {
				ProcessDetail pd = list.get(i);
				pd.setPosition(i - 1);
				controller.getManagerBean().update(pd);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			IController controller = event.getController();
			ProcessDetail processDetail = (ProcessDetail) controller.getTo();
			if (processDetail.getStatus() == ProcessDetailStatus.INACTIVE) {
				IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICampaignAlias.PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL_ID), processDetail.getId());
				int i = bean.getCount(criteria);
				if (i > 0) {
					throw new ControllerListenerException("No se puede marcar como inactivo el proceso porque está señalado desde una transición");		
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
