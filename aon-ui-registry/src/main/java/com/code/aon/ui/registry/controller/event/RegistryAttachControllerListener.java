package com.code.aon.ui.registry.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAttachControllerListener extends AttachmentControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		RegistryAttachment attach = (RegistryAttachment) raController.getTo();
		if ( raController.getType() != null ) {
			attach.setRegistryAttachmentType( raController.getType() );
		}	
	}
			
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		super.afterEditSearch(event);
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		Integer registryId = raController.getRegistryId();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		try {		
			Criteria criteria = raController.getCriteria();
			String id = raController.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
			if ( ds.isChildDomain() && (!raController.isSkipHeredity()) ) {
				Integer parentCompanyId = AdminUtil.getCompanyId(ds.getParentDomainId());
				criteria.addInExpression(id, new Object[] {parentCompanyId, registryId});
			} else {
				criteria.addEqualExpression(id, registryId);	
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error after edit search",e);
		}
	}
	
}
