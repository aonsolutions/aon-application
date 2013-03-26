package com.code.aon.ui.groupware.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.ProcessDetailTransition;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

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
			String field = controller.getFieldName(IEntityAlias.PROCESS_DETAIL_POSITION);
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
			checkNextDetail(processDetail, "No se puede borrar el proceso" );
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
			if (!processDetail.isActive()) {
				checkNextDetail(processDetail, "No se puede marcar como inactivo el proceso" );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void checkNextDetail(ProcessDetail processDetail, String msg) throws ManagerBeanException, ControllerListenerException {
		IManagerBean bean = BeanManager.getManagerBean(ProcessDetailTransition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROCESS_DETAIL_TRANSITION_NEXT_PROCESS_DETAIL_ID), processDetail.getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if (list != null && list.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (ITransferObject to: list){
				ProcessDetailTransition pdt = (ProcessDetailTransition) to;
				buf.append(msg);
				buf.append(", porque está señalado desde la transición \"");	
				buf.append(pdt.getProcessTransitionType().getDescription());
				buf.append("\" del detalle de proceso \"");
				buf.append(pdt.getProcessDetail().getDescription());
				buf.append("\". ");
			}
			throw new ControllerListenerException(buf.toString());		
		}
	}

}
