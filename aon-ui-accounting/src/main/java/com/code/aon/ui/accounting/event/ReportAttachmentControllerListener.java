package com.code.aon.ui.accounting.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.accounting.controller.report.ReportAttachmentController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ReportAttachmentControllerListener extends AttachmentControllerListener {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ReportAttachmentController rac = getReportAttachmentController(event);
		RegistryAttachment ra = (RegistryAttachment) rac.getTo();
		ra.setRegistry( rac.getCompany() );
		super.beforeBeanAdded(event);
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			ReportAttachmentController rac = getReportAttachmentController(event);
			rac.clearCriteria();
			// Solo los de Company
			rac.getCriteria().addEqualExpression(rac.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), rac.getCompany().getId());

			// Solo los RegistryAttachmentType.FISCAL_REPORTS y RegistryAttachmentType.FISCAL_TEMPLATES
			String typeAlias = rac.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
			Expression e1 = ExpressionUtilities.getEqualExpression(typeAlias, RegistryAttachmentType.FISCAL_REPORTS);
			Expression e2 = ExpressionUtilities.getEqualExpression(typeAlias, RegistryAttachmentType.FISCAL_TEMPLATES);
			rac.getCriteria().addExpression(ExpressionUtilities.getOrExpression(e1,e2));
			
			super.beforeModelInitialized(event);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		} 
	}
	
	private ReportAttachmentController getReportAttachmentController(ControllerEvent event) {
		return (ReportAttachmentController) event.getController();
	}
}
