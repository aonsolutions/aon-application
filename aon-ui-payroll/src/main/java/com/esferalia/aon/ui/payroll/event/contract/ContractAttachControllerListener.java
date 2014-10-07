package com.esferalia.aon.ui.payroll.event.contract;

import com.code.aon.AonVersion;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.ui.payroll.controller.contract.ContractAttachController;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;

public class ContractAttachControllerListener extends AttachmentControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
			SepeAppParamsController paramsController = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
			if(!paramsController.getDevelopmentMode() && !AonUtil.getRoleManager().isSysAdmin()){
				String alias = attachBean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE);
				Expression expToAdd = null;
				expToAdd = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.CONTRACT_DOC_DRAFT);				
				Expression exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.CONTRACT_DOC);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.BASIC_COPY_DRAFT);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.BASIC_COPY);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.TRAINING_CENTER_DIRECT_DEBIT);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.TRAINING_ANNEX_I);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.TRAINING_ANNEX_II);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.EXTENSION_DOC_DRAFT);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.EXTENSION_DOC);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getEqualExpression(alias, ContractAttachmentType.ENTERPRISE_CERTIFICATE_DOC_DRAFT);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				exp  = ExpressionUtilities.getNullExpression(alias);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				criteria.addExpression(expToAdd);
			}
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
//		ContractAttachController controller = (ContractAttachController) event.getController();
//		Contract contract = (Contract) controller.getMasterController().getTo();
//		try {		
//			String label = controller.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID);
//			controller.getCriteria().addEqualExpression(label, contract.getId());
//		} catch (ManagerBeanException e) {
//			throw new ControllerListenerException("Error after edit search",e);
//		}
	}
	
}
