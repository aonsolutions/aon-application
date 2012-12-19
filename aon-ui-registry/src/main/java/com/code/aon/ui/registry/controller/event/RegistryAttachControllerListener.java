package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.faces.controller.event.AttachmentControllerListener;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAttachControllerListener extends AttachmentControllerListener {
	
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
	
	private Integer getParentCompanyId( Integer domainId ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression("Company.domain", domainId);
		String id = bean.getFieldName(IEntityAlias.COMPANY_ID);
		return (Integer) bean.getUniqueResult(Projection.property(id), criteria);
	}
			
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		super.afterEditSearch(event);
		RegistryAttachController raController = (RegistryAttachController) event.getController();
		Registry registry = (Registry) raController.getMasterController().getTo();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		try {		
			Criteria criteria = raController.getCriteria();
			String id = raController.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
			if ( ds.isChildDomain() ) {
				Integer parentCompanyId = getParentCompanyId(ds.getParentDomainId());
				criteria.addInExpression(id, new Object[] {parentCompanyId, registry.getId()});
			} else {
				criteria.addEqualExpression(id, registry.getId());	
			}
			if ( raController.getType() != null ) {
				String type = raController.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
				criteria.addEqualExpression(type, raController.getType());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error after edit search",e);
		}
	}
	
}
