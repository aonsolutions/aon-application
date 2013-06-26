package com.esferalia.aon.ui.payroll.event.contract;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.contract.ContractAttachController;

public class ContractAttachControllerListener extends AttachmentControllerListener {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		super.beforeModelInitialized(event);
		try {
			ContractAttachController controller = (ContractAttachController) event.getController();
			controller.clearChecks();
			Criteria criteria = controller.getCriteria();
			IManagerBean attachBean = controller.getManagerBean();
			if ( controller.getType() != null ) {
				criteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), controller.getType());
			}
			// TODO: excluir ficheros SEPE del modelo
//			criteria.addNotEqualExpression(attachBean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA_RESPONSE);
//			criteria.addNotEqualExpression(attachBean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA_STATUS);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		ContractAttachController controller = (ContractAttachController) event.getController();
		ContractAttachment attach = (ContractAttachment) controller.getTo();
		if ( controller.getType() != null ) {
			attach.setAttachmentType( controller.getType() );
		}
	}
	
	
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		super.afterEditSearch(event);
		ContractAttachController controller = (ContractAttachController) event.getController();
		Contract contract = (Contract) controller.getMasterController().getTo();
		try {		
			String label = controller.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID);
			controller.getCriteria().addEqualExpression(label, contract.getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error after edit search",e);
		}
	}
	
}
